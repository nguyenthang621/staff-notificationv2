import os 
from dotenv import load_dotenv

load_dotenv()
API_SERVER = os.getenv('API_SERVER')
PATH_ATTENDENCE = os.getenv('PATH_ATTENDENCE')
PATH_FIND_EMPLOYEE = os.getenv('PATH_FIND_EMPLOYEE')
SHEET_NAME = os.getenv('SHEET_NAME')

start_col_body = os.getenv('start_col')
start_row_date_header = os.getenv('start_row_date')
start_row_weekday_header = os.getenv('start_row_weekday')

destination_folder = os.getenv('destination_folder')
template_file = os.getenv('template_file')



if __name__ == '__main__':
    print(f"{API_SERVER=}")