import pandas as pd
import Filter_III
from datetime import datetime

import json

from dateutil.relativedelta import relativedelta

csv_file_path = "../Baza/mega-data.csv"  # замени со вистинскиот пат до CSV документот
json_file_path = "../Baza/issuer_names.json"  # замени со вистинскиот пат до JSON документот
output_json="../Baza/last_dates.json"
last_dates_json_path = "../Baza/last_dates.json"  # замени со вистинската патека до JSON документот

def get_last_dates_for_firms(csv_file, json_file, output_json):
    csv_data = pd.read_csv(csv_file, header=0, names=["code", "date", "open", "high", "low", "close", "change", "volume", "value1", "value2"])
    csv_data = csv_data[csv_data["date"].str.match(r"\d{2}\.\d{2}\.\d{4}")]
    csv_data["date"] = pd.to_datetime(csv_data["date"], format="%d.%m.%Y")
    csv_data = csv_data.sort_values(by=["code", "date"])

    with open(json_file, "r", encoding="utf-8") as file:
        json_data = json.load(file)

    last_dates = []

    # За секоја шифра во JSON документот, најди ја последната дата од CSV документот
    for firm in json_data:
        code = firm["name"]
        firm_data = csv_data[csv_data["code"] == code]

        # Ако има податоци за таа шифра, земи ја последната дата
        if not firm_data.empty:
            last_date = firm_data["date"].max().strftime("%d.%m.%Y")
            last_dates.append({"code": code, "last_date": last_date})
        else:
            # Ако нема податоци, стави None за последната дата
            today = datetime.today()
            date_10_years_ago = today - relativedelta(years=10)
            last_dates.append({"code": code, "last_date": date_10_years_ago.strftime("%d.%m.%Y")})

    # Запиши ги резултатите во нов JSON документ
    with open(output_json, "w", encoding="utf-8") as file:
        json.dump(last_dates, file, ensure_ascii=False, indent=4)

    print(f"Резултатите се запишани во {output_json}")
    return last_dates




def outdated_firms(last_dates_json):
    # Учитај ги податоците од JSON документот
    with open(last_dates_json, "r", encoding="utf-8") as file:
        last_dates_data = json.load(file)

    # Дефинирај ја денешната дата во формат "дд.мм.гггг"
    today = datetime.today().strftime("%d.%m.%Y")

    # Провери која шифра има последна дата различна од денешната и испечати ги тие информации
    for entry in last_dates_data:
        code = entry["code"]
        last_date = entry["last_date"]

        if last_date and last_date != today:
            print(f"Шифра: {code}, Последна дата: {last_date}, Денешна дата: {today}")
            Filter_III.Call_save_data_from_to(code, last_date, today)

def Call_Filter_II():
    get_last_dates_for_firms(csv_file_path, json_file_path,output_json)
    outdated_firms(last_dates_json_path)
    get_last_dates_for_firms(csv_file_path, json_file_path,output_json)

# get_last_dates_for_firms(csv_file_path, json_file_path,output_json)
# outdated_firms(last_dates_json_path)
# get_last_dates_for_firms(csv_file_path, json_file_path,output_json)



