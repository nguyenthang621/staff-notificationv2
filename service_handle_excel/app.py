from flask import Flask, request, send_file, jsonify
from func import create_excel_file, initHeader, getDataAttendence, handleLeaveRequestEmployee, mergeData, fillDataBody

app = Flask(__name__)

@app.route('/download', methods=['POST'])
def download_file():
    try:
        data = request.get_json()
        print(f"{data=}")
        start_date = data.get('startDate')
        end_date = data.get('endDate')
        print(f"{start_date=}, {end_date=}")

        file_download = create_excel_file(start_date, end_date)
        dates_body = initHeader(start_date, end_date, file_download)  # dates_body list date and special date: holiday, weekend
        dict_attendance_employee = getDataAttendence(start_date, end_date)
        additional_info = handleLeaveRequestEmployee(dict_attendance_employee, dates_body)
        employees_have_leave = mergeData(dates_body, additional_info)
        fillDataBody(dates_body, employees_have_leave, file_download)
        return send_file(file_download, as_attachment=True)

    except Exception as e:
        print(f"Error: {e}")
        return jsonify("Server Download Error"), 500

if __name__ == '__main__':
    app.run(debug=True, host="0.0.0.0")
