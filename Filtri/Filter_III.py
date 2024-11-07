from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait, Select
from selenium.webdriver.support import expected_conditions as EC
from datetime import datetime, timedelta
import time


def fetch_data_for_period(firm_code, start_date, end_date):
    options = webdriver.ChromeOptions()
    options.add_argument('--headless')  # Ова обезбедува да се стартува без отворен прозорец
    options.add_argument('--no-sandbox')
    options.add_argument('--disable-dev-shm-usage')

    browser = webdriver.Chrome(options=options)
    browser.get('https://www.mse.mk/mk/stats/symbolhistory/ALK')

    start_date_str = start_date.strftime("%d.%m.%Y")
    end_date_str = end_date.strftime("%d.%m.%Y")

    # Внесување на почетен и краен датум
    date_pickers = browser.find_elements(By.CSS_SELECTOR, ".datepicker")
    date_pickers[1].clear()
    date_pickers[0].clear()
    date_pickers[1].send_keys(end_date_str)
    date_pickers[0].send_keys(start_date_str)

    # Селекција на фирма кодот
    code_select = Select(browser.find_element(By.CSS_SELECTOR, '#Code'))
    code_select.select_by_visible_text(firm_code)

    # Клик на "Прикажи"
    dugme = browser.find_element(By.CSS_SELECTOR, ".container-end input")
    dugme.click()

    # Чекање за вчитување на табелата
    WebDriverWait(browser, 10).until(EC.presence_of_element_located((By.CSS_SELECTOR, "#resultsTable tbody tr")))

    # Скролување до дното на страницата за да се вчитаат сите податоци
    previous_row_count = 0
    while True:
        # Број на моментално вчитани редови
        rows = browser.find_elements(By.CSS_SELECTOR, "#resultsTable tbody tr")
        current_row_count = len(rows)

        # Ако не се додаваат нови редови, прекинувај го циклусот
        if current_row_count == previous_row_count:
            break  # Ако бројот на редови не се зголемува, сите податоци се вчитани

        # Ажурирање на претходниот број на редови и скрол до дното
        previous_row_count = current_row_count
        browser.execute_script("window.scrollTo(0, document.body.scrollHeight);")
        time.sleep(3)  # Поголема пауза за да се вчитаат нови редови

    # Собирање на податоци по скролувањето
    firm_data = []
    rows = browser.find_elements(By.CSS_SELECTOR, "#resultsTable tbody tr")  # Повторно земаме редови после скролувањето
    for row in rows:
        cl = row.find_elements(By.CSS_SELECTOR, "td")

        # Ако редот не е празен
        if len(cl) > 1:
            data = cl[0].text
            price = cl[1].text
            max_val = cl[2].text
            min_val = cl[3].text
            volume = cl[6].text
            best = cl[7].text

            parsed_row = {
                "Date": data,
                "Price": price,
                "Max": max_val,
                "Min": min_val,
                "Volume": volume,
                "BEST": best
            }

            firm_data.append(parsed_row)

    browser.quit()
    return firm_data


# Повик на функцијата со дадениот датумски опсег
firm_code = "KMB"
start_date = datetime(2024, 9, 7)
end_date = datetime(2024, 11, 7)

result = fetch_data_for_period(firm_code, start_date, end_date)
for entry in result:
    print(entry)
