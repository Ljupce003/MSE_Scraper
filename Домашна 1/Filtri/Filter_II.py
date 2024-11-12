import os
import pandas as pd
import json
from datetime import datetime
from dateutil.relativedelta import relativedelta
import Filter_III

csv_file_path = "../Baza/mega-data.csv"
json_file_path = "../Baza/issuer_names.json"
output_json = "../Baza/last_dates.json"

def load_or_create_csv(csv_file):
    folder = os.path.dirname(csv_file)
    if folder and not os.path.exists(folder):
        os.makedirs(folder)
        print(f"Created folder: {folder}")

    if not os.path.isfile(csv_file):
        headers = ["code", "date", "open", "high", "low", "close", "change", "volume", "value1", "value2"]
        pd.DataFrame(columns=headers).to_csv(csv_file, index=False)
        print(f"Created CSV file: {csv_file}")

    return pd.read_csv(csv_file, header=0, names=["code", "date", "open", "high", "low", "close", "change", "volume", "value1", "value2"])

def get_last_dates_for_firms(csv_file, json_file, output_json):
    csv_data = load_or_create_csv(csv_file)
    csv_data["date"] = pd.to_datetime(csv_data["date"], errors='coerce', format="%d.%m.%Y")
    csv_data = csv_data.sort_values(by=["code", "date"])

    with open(json_file, "r", encoding="utf-8") as file:
        json_data = json.load(file)

    last_dates = []
    for firm in json_data:
        code = firm["name"]
        firm_data = csv_data[csv_data["code"] == code]

        if not firm_data.empty:
            last_date = firm_data["date"].max().strftime("%d.%m.%Y")
        else:
            date_10_years_ago = (datetime.today() - relativedelta(years=10)).strftime("%d.%m.%Y")
            last_date = date_10_years_ago

        last_dates.append({"code": code, "last_date": last_date})

    with open(output_json, "w", encoding="utf-8") as file:
        json.dump(last_dates, file, ensure_ascii=False, indent=4)
    print(f"Last dates saved to {output_json}.")

def outdated_firms(last_dates_json):
    with open(last_dates_json, "r", encoding="utf-8") as file:
        last_dates_data = json.load(file)

    today = datetime.today().strftime("%d.%m.%Y")
    for entry in last_dates_data:
        code = entry["code"]
        last_date = entry["last_date"]

        if last_date and last_date != today:
            print(f"Code: {code}, Last Date: {last_date}, Today's Date: {today}")
            Filter_III.Call_save_data_from_to(code, last_date, today)

def Call_Filter_II():
    get_last_dates_for_firms(csv_file_path, json_file_path, output_json)
    outdated_firms(output_json)

