import sys
import pandas as pd
import ta
import os
import matplotlib.pyplot as plt

stock_symbol = sys.argv[1]
stock_data = pd.read_csv(f"csv/{stock_symbol}.csv")
stock_data['Date'] = pd.to_datetime(stock_data['Date'], format='%m/%d/%Y', errors='coerce')
columns_to_clean = ['LastTradePrice', 'Max', 'Min', 'Avg. Price', 'Volume', 'Turnover in BEST', 'TotalTurnover']
for col in columns_to_clean:
    stock_data[col] = stock_data[col].replace({',': ''}, regex=True)
    stock_data[col] = pd.to_numeric(stock_data[col], errors='coerce')

stock_data = stock_data.sort_values(by='Date', ascending=True)
stock_data = stock_data.reset_index(drop=True)

# Filter the last 30 days
most_recent_date = stock_data['Date'].max()
stock_data = stock_data[stock_data['Date'] >= most_recent_date - pd.Timedelta(days=30)]

# Calculate Moving Averages (SMA & EMA)
stock_data['SMA_20'] = stock_data['LastTradePrice'].rolling(window=20).mean()
stock_data['EMA_20'] = stock_data['LastTradePrice'].ewm(span=20, adjust=False).mean()

# Calculate RSI (Relative Strength Index)
stock_data['RSI'] = ta.momentum.RSIIndicator(stock_data['LastTradePrice'], window=14).rsi()

# Calculate MACD (Moving Average Convergence Divergence)
stock_data['MACD'] = ta.trend.MACD(stock_data['LastTradePrice']).macd()
stock_data['MACD_signal'] = ta.trend.MACD(stock_data['LastTradePrice']).macd_signal()

# Generate Buy/Sell Signals based on SMA and RSI
stock_data['SMA_signal'] = 0
stock_data['SMA_signal'][stock_data['SMA_20'] > stock_data['EMA_20']] = 1  # Buy signal
stock_data['SMA_signal'][stock_data['SMA_20'] < stock_data['EMA_20']] = -1  # Sell signal

stock_data['RSI_signal'] = 0
stock_data['RSI_signal'][stock_data['RSI'] < 30] = 1  # Buy signal
stock_data['RSI_signal'][stock_data['RSI'] > 70] = -1  # Sell signal

stock_data['Signal'] = stock_data[['SMA_signal', 'RSI_signal']].sum(axis=1)

print("EXCHANGE")
for row in stock_data['LastTradePrice']:
    print(row)
    print("#")
print("$")
for row in stock_data['Date']:
    print(row)
    print("#")
print("$")
for row in stock_data['LastTradePrice'][stock_data['Signal'] == 1]:
    print(row)
    print("#")
print("$")
for row in stock_data['LastTradePrice'][stock_data['Signal'] == -1]:
    print(row)
    print("#")