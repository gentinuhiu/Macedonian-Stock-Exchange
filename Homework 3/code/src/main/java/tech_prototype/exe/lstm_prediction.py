import sys
import pandas as pd
import numpy as np
from sklearn.preprocessing import MinMaxScaler
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import LSTM, Dense, Dropout
from tensorflow.keras.regularizers import l2
from tensorflow.keras.callbacks import EarlyStopping
import json

# Force UTF-8 encoding for standard output
sys.stdout.reconfigure(encoding='utf-8')

# Read company CSV file passed as an argument
if len(sys.argv) != 2:
    print("Usage: python prediction_script.py <csv_file_path>")
    sys.exit(1)

csv_file_path = sys.argv[1]

# Load data
data = pd.read_csv(f"csv/{csv_file_path}.csv")
data['Date'] = pd.to_datetime(data['Date'])
data.set_index('Date', inplace=True)

# Convert LastTradePrice to numeric and handle commas
data['LastTradePrice'] = data['LastTradePrice'].str.replace(',', '').astype(float)

# Feature selection and scaling
close_prices = data['LastTradePrice'].values.reshape(-1, 1)
scaler = MinMaxScaler(feature_range=(0, 1))
scaled_data = scaler.fit_transform(close_prices)

# Prepare training and validation data
def prepare_data(data, time_steps):
    X, y = [], []
    for i in range(len(data) - time_steps):
        X.append(data[i:i + time_steps])
        y.append(data[i + time_steps])
    return np.array(X), np.array(y)

TIME_STEPS = 60
train_size = int(len(scaled_data) * 0.7)
train_data = scaled_data[:train_size]
val_data = scaled_data[train_size:]

X_train, y_train = prepare_data(train_data, TIME_STEPS)
X_val, y_val = prepare_data(val_data, TIME_STEPS)

# Augment training data by adding noise
def add_noise(data, noise_factor=0.02):
    noisy_data = data + noise_factor * np.random.normal(loc=0.0, scale=1.0, size=data.shape)
    return np.clip(noisy_data, 0.0, 1.0)

X_train_noisy = add_noise(X_train)

# Build LSTM model with reduced complexity
model = Sequential([
    LSTM(32, return_sequences=True, input_shape=(X_train.shape[1], X_train.shape[2]), kernel_regularizer=l2(0.01)),
    Dropout(0.3),
    LSTM(32, return_sequences=False, kernel_regularizer=l2(0.01)),
    Dropout(0.3),
    Dense(20, activation='relu', kernel_regularizer=l2(0.01)),
    Dense(1)
])

model.compile(optimizer='adam', loss='mean_squared_error')

# Early stopping callback
early_stopping = EarlyStopping(monitor='val_loss', patience=3, restore_best_weights=True)

# Train the model
history = model.fit(X_train_noisy, y_train, epochs=30, batch_size=64, validation_data=(X_val, y_val), callbacks=[early_stopping])

# Evaluate the model
predicted_prices = model.predict(X_val)
predicted_prices = scaler.inverse_transform(predicted_prices)

# Prepare actual values for comparison
actual_prices = scaler.inverse_transform(y_val.reshape(-1, 1))

# Calculate evaluation metrics
from sklearn.metrics import mean_squared_error
rmse = np.sqrt(mean_squared_error(actual_prices, predicted_prices))

# Future Predictions
def predict_future(model, last_sequence, n_future, scaler):
    future_predictions = []
    current_sequence = last_sequence.reshape(1, -1, 1)  # Reshape to match LSTM input

    for _ in range(n_future):
        prediction = model.predict(current_sequence)
        future_predictions.append(prediction[0, 0])
        # Update the sequence with the new prediction
        # Ensure prediction is reshaped to match the sequence dimensions
        prediction_reshaped = np.array([[prediction[0, 0]]]).reshape(1, 1, 1)
        current_sequence = np.append(current_sequence[:, 1:, :], prediction_reshaped, axis=1)

    # Reverse scaling
    future_predictions = scaler.inverse_transform(np.array(future_predictions).reshape(-1, 1)).flatten()
    return future_predictions


# Use the last sequence from the dataset for future predictions
n_future = 7  # Predict for the next 7 days
last_sequence = scaled_data[-TIME_STEPS:]
future_prices = predict_future(model, last_sequence, n_future, scaler)

# Append future predictions to the predicted prices
predicted_prices = np.append(predicted_prices.flatten(), future_prices).tolist()

# Prepare data for output
output = {
    "training_loss": history.history['loss'],
    "validation_loss": history.history['val_loss'],
    "actual_prices": actual_prices.flatten().tolist(),
    "predicted_prices": predicted_prices,  # Includes both historical and future predictions
    "predicted_price": float(future_prices[0]),  # Only include the next predicted price
    "rmse": rmse
}
print("EXCHANGE")
# Print the output as JSON
print(json.dumps(output))
