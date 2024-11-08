import requests
from bs4 import BeautifulSoup
import pandas as pd
import json
from datetime import datetime, timedelta
import time  # Import time module for timing

# Start the timer
start_time = time.time()

# Load issuer names from JSON file
with open("Baza/issuer_names.json", 'r') as file:
    issuer_names = json.load(file)

url = "https://www.mse.mk/mk/stats/symbolhistory/ALK"

def fetch_data_for_period(firm_code, start_date, end_date):
    session = requests.Session()
    payload = {
        "FromDate": start_date,
        "ToDate": end_date,
        "Code": firm_code
    }
    response = session.post(url, data=payload)
    if response.status_code == 200:
        soup = BeautifulSoup(response.text, 'html.parser')
        table = soup.find('table')
        if table:
            rows = []
            headers = [th.text.strip() for th in table.find_all('th')]
            for tr in table.find_all('tr')[1:]:
                cells = [td.text.strip() for td in tr.find_all('td')]
                if cells:
                    rows.append(cells)
            data = pd.DataFrame(rows, columns=headers)
            data.insert(0, "Issuer", firm_code)  # Add issuer name as a new first column
            return data
    return None

def fetch_data_for_full_period(firm_code, total_years):
    all_data = []
    current_end_date = datetime.strptime("05.11.2024", "%d.%m.%Y")
    for _ in range(total_years):
        current_start_date = current_end_date - timedelta(days=365)  # Approximately 1 year back
        start_date_str = current_start_date.strftime("%d.%m.%Y")
        end_date_str = current_end_date.strftime("%d.%m.%Y")
        data = fetch_data_for_period(firm_code, start_date_str, end_date_str)
        if data is not None:
            all_data.append(data)
        current_end_date = current_start_date - timedelta(days=1)  # Move to the previous year range

    # Combine all yearly data into one DataFrame
    if all_data:
        return pd.concat(all_data, ignore_index=True)
    return None

# Fetch data for each issuer in the list over the 10-year period
all_issuers_data = []
for issuer in issuer_names:
    firm_code = issuer['name']
    data = fetch_data_for_full_period(firm_code, total_years=10)
    if data is not None:
        all_issuers_data.append(data)
        print(f"Data fetched for issuer: {firm_code}")
    else:
        print(f"No data for issuer: {firm_code}")

# Concatenate all issuer data and save to CSV
if all_issuers_data:
    combined_data = pd.concat(all_issuers_data, ignore_index=True)
    combined_data.to_csv('mega-data.csv', index=False)

# End the timer and calculate the duration
end_time = time.time()
duration = end_time - start_time
print(f"Program completed in {duration:.2f} seconds")
