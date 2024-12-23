from datetime import datetime, timedelta
import os
import requests
from bs4 import BeautifulSoup
import pandas as pd
from concurrent.futures import ThreadPoolExecutor, as_completed

# Ensure the /csv directory exists
def ensure_csv_directory():
    base_path = "./csv"
    if not os.path.exists(base_path):
        os.makedirs(base_path)
    return base_path

CSV_BASE_PATH = ensure_csv_directory()

def ensure_updates_directory():
    folder_name = "./csv-updates"
    if not os.path.exists(folder_name):
        os.makedirs(folder_name)

def save_last_update(code, content):
    # Define the file path
    file_path = os.path.join("csv-updates", f"{code}.txt")

    # Write the content to the file
    with open(file_path, 'w') as file:
        file.write(str(content))

# FILTER 1
def get_codes():
    url = "https://www.mse.mk/en/stats/symbolhistory/ALK"
    with requests.Session() as session:
        response = session.get(url)
    soup = BeautifulSoup(response.content, "html.parser")
    dropdown = soup.find("select", id="Code")
    if not dropdown:
        return []
    return [
        option.text.strip()
        for option in dropdown.find_all("option")
        if not any(char.isdigit() for char in option.text) and not option.text.strip().startswith(('E'))
    ]

# FILTER 2
def get_last_update(code):
    path = os.path.join(CSV_BASE_PATH, f"{code}.csv")
    try:
        df = pd.read_csv(path)
        return pd.to_datetime(df['Date']).max()
    except (FileNotFoundError, pd.errors.EmptyDataError):
        return None

def fetch_code(session, code, start_date, end_date):
    url = (
        f"https://www.mse.mk/en/stats/symbolhistory/{code}"
        f"?FromDate={start_date.strftime('%m/%d/%Y')}"
        f"&ToDate={end_date.strftime('%m/%d/%Y')}"
    )
    response = session.get(url)
    soup = BeautifulSoup(response.content, 'html.parser')
    tbody = soup.select_one('tbody')
    if not tbody:
        return []
    return [[cell.get_text(strip=True) for cell in row.find_all('td')] for row in tbody.find_all('tr')]

def update_code(session, code):
    current_date = datetime.now()
    last_update = get_last_update(code)
    all_data = []

    if last_update:
        start_date = (last_update + timedelta(days=1))
    else:
        start_date = current_date - timedelta(days=3650)

    start_date_tmp = start_date
    while start_date <= current_date:
        year_end = datetime(start_date.year, 12, 31)
        end_date = min(year_end, current_date)
        data = fetch_code(session, code, start_date, end_date)
        all_data.extend(data)
        start_date_tmp = start_date
        start_date = end_date + timedelta(days=1)

    save_last_update(code, start_date_tmp)
    if all_data:
        save_to_csv(code, all_data)

def save_to_csv(code, data):
    columns = ['Date', 'LastTradePrice', 'Max', 'Min', 'Avg. Price', '%chg.', 'Volume', 'Turnover in BEST', 'TotalTurnover']
    df = pd.DataFrame(data, columns=columns)

    file_path = os.path.join(CSV_BASE_PATH, f"{code}.csv")
    df.to_csv(file_path, mode='a', header=not os.path.exists(file_path), index=False)

if __name__ == "__main__":
    ensure_updates_directory()
    codes = get_codes()
    with requests.Session() as session:
        with ThreadPoolExecutor(max_workers=5) as executor:
            futures = {executor.submit(update_code, session, code): code for code in codes}
            for future in as_completed(futures):
                code = futures[future]
                try:
                    future.result()
                    print(f"{code} updated")
                except Exception as e:
                    print(f"Error updating {code}: {e}")
