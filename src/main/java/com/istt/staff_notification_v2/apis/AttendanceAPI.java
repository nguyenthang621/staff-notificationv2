package com.istt.staff_notification_v2.apis;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.dto.AttendanceDTO;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.dto.SearchAttendence;
import com.istt.staff_notification_v2.dto.SearchDTO;
import com.istt.staff_notification_v2.service.AttendanceService;

@RestController
@RequestMapping("/attendance")
public class AttendanceAPI {
	@Autowired
	private AttendanceService attendanceService;

	private static final String ENTITY_NAME = "isttAttendance";

	@PostMapping("")
	public ResponseDTO<AttendanceDTO> create(@RequestBody @Valid AttendanceDTO attendanceDTO)
			throws URISyntaxException {
		if (attendanceDTO.getStartDate() == null || attendanceDTO.getEndDate() == null) {
			throw new BadRequestAlertException("Bad request: missing data", ENTITY_NAME, "missing");
		}

		attendanceService.create(attendanceDTO);
		return ResponseDTO.<AttendanceDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(attendanceDTO)
				.build();
	}

	@DeleteMapping("/deletebyList")
	public ResponseDTO<Void> delete(@RequestBody @Valid List<String> ids) throws URISyntaxException {
		if (ids == null) {
			throw new BadRequestAlertException("Bad request: missing id", ENTITY_NAME, "missing_id");
		}
		attendanceService.deletebylistId(ids);
		return ResponseDTO.<Void>builder().code(String.valueOf(HttpStatus.OK.value())).build();
	}

	@DeleteMapping("/{id}")
	public ResponseDTO<Void> delete(@PathVariable(value = "id") String id) throws URISyntaxException {
		if (id == null) {
			throw new BadRequestAlertException("Bad request: missing id", ENTITY_NAME, "missing_id");
		}
		attendanceService.deleteById(id);
		return ResponseDTO.<Void>builder().code(String.valueOf(HttpStatus.OK.value())).build();
	}

	@GetMapping("/{id}")
	// @PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseDTO<AttendanceDTO> get(@PathVariable(value = "id") String id) {
		if (id == null) {
			throw new BadRequestAlertException("Bad request: missing id", ENTITY_NAME, "missing_id");
		}
		return ResponseDTO.<AttendanceDTO>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(attendanceService.get(id)).build();
	}

	@GetMapping("/type/{type}")
	// @PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseDTO<List<AttendanceDTO>> getStatus(@PathVariable(value = "type") String type) {
		if (type == null) {
			throw new BadRequestAlertException("Bad request: missing status", ENTITY_NAME, "missing_status");
		}
		return ResponseDTO.<List<AttendanceDTO>>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(attendanceService.getStatus(type)).build();
	}

	@PostMapping("/search")
	public ResponseDTO<List<AttendanceDTO>> search(@RequestBody @Valid SearchAttendence SearchAttendence) {
		return attendanceService.search(SearchAttendence);
	}

	@PostMapping("/searchByEmployeeStatus")
	public ResponseDTO<List<AttendanceDTO>> searchbyName(@RequestBody @Valid SearchDTO searchDTO) {
		return attendanceService.searchByEmployeeName(searchDTO);
	}

	@PutMapping("")
	public ResponseDTO<AttendanceDTO> update(@RequestBody @Valid AttendanceDTO attendanceDTO)
			throws URISyntaxException {
		if (attendanceDTO.getAttendanceId() == null) {
			throw new BadRequestAlertException("Bad request: missing data", ENTITY_NAME, "missing");
		}

		attendanceService.update(attendanceDTO);
		return ResponseDTO.<AttendanceDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(attendanceDTO)
				.build();
	}

	@GetMapping("/getAll")
	public List<AttendanceDTO> getAll() {
		return attendanceService.getAll();
	}

	@GetMapping("/api/days-in-period")
	public long getDaysInPeriod(@RequestParam int startDay, @RequestParam int endDay, @RequestParam int month,
			@RequestParam int year) {
		try {
			YearMonth yearMonth = YearMonth.of(year, month);

			if (startDay < 1 || startDay > yearMonth.lengthOfMonth() || endDay < 1
					|| endDay > yearMonth.lengthOfMonth()) {
				throw new IllegalArgumentException("Invalid startDay or endDay provided.");
			}

			LocalDate startDate = LocalDate.of(year, month, startDay);
			LocalDate endDate = LocalDate.of(year, month, endDay);

			if (startDate.isAfter(endDate)) {
				throw new IllegalArgumentException("startDay must be before or equal to endDay.");
			}

			return ChronoUnit.DAYS.between(startDate, endDate) + 1;
		} catch (DateTimeParseException | IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid input parameters.");
		}
	}

	@GetMapping("/api/days-in-month")
	public int getDaysInMonth(@RequestParam int year, @RequestParam int month) {
		try {
			YearMonth yearMonth = YearMonth.of(year, month);
			return yearMonth.lengthOfMonth();
		} catch (DateTimeParseException | IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid year or month provided.");
		}
	}

}
