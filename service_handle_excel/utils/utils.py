import pandas as pd
import os
import shutil
from configs import config

def create_excel_file(start_date, end_date):
    try:
        destination_folder = config.destination_folder
        template_file = config.template_file

        destination_file = os.path.join(destination_folder, f"Chấm công từ {start_date} đến {end_date}.xlsx")
        shutil.copyfile(template_file, destination_file)
        return destination_file
    except Exception as e:
        print(f"Error copy file: {e}")
        return False

def delete_file(file_path):
    try:
        os.remove(file_path)
    except Exception as e:
        print(f"Error delete file: {e}")