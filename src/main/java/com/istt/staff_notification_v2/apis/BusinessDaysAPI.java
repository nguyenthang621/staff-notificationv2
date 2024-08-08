package com.istt.staff_notification_v2.apis;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.dto.BusinessDaysDTO;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.dto.SearchAttendence;
import com.istt.staff_notification_v2.dto.SearchDTO;
import com.istt.staff_notification_v2.service.BusinessDaysService;

@RestController
@RequestMapping("/businessdays")
public class BusinessDaysAPI {
	@Autowired
	private BusinessDaysService businessDaysService;

	private static final String ENTITY_NAME = "isttBusinessDays";
	private static final Logger logger = LogManager.getLogger(BusinessDaysAPI.class);

//	@PreAuthorize("hasRole('ROLE_BUSINESSDAYS_CREATE')&&hasRole('ROLE_BUSINESSDAYS_ACCESS')")
	@PostMapping("")
	public ResponseDTO<BusinessDaysDTO> create(@RequestBody @Valid BusinessDaysDTO businessDaysDTO)
			throws URISyntaxException {
//		logger.info("create by :" + currentuser.getUsername());
		if (businessDaysDTO.getStartdate() == null || businessDaysDTO.getType() == null
				|| businessDaysDTO.getEnddate() == null) {
			logger.error("missing data");
			throw new BadRequestAlertException("Bad request: missing data", ENTITY_NAME, "missin_name");
		}
		businessDaysService.create(businessDaysDTO);
		return ResponseDTO.<BusinessDaysDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(businessDaysDTO)
				.build();
	}

	@PreAuthorize("hasRole('ROLE_BUSINESSDAYS_DELETE')&&hasRole('ROLE_BUSINESSDAYS_ACCESS')")
	@DeleteMapping("/{id}")
	public ResponseDTO<Void> delete(@PathVariable(value = "id") String id) throws URISyntaxException {

		if (id == null) {
			logger.error("missing data");
			throw new BadRequestAlertException("Bad request: missing id", ENTITY_NAME, "missing_id");
		}
		businessDaysService.delete(id);
		return ResponseDTO.<Void>builder().code(String.valueOf(HttpStatus.OK.value())).build();
	}

//	@PostMapping("/search")
//	public ResponseDTO<List<BusinessDaysDTO>> search(@RequestBody @Valid SearchDTO searchDTO) {
////		return departmentService.search(searchDTO);
//		return null;
//	}

	@PreAuthorize("hasRole('ROLE_BUSINESSDAYS_DELETE')&&hasRole('ROLE_BUSINESSDAYS_ACCESS')")
	@DeleteMapping("/ids")
	public ResponseDTO<List<String>> deletebyListId(@RequestBody @Valid List<String> ids) throws URISyntaxException {
//		logger.info("create by :" + currentuser.getUsername());
		if (ids.isEmpty()) {
//			logger.error("missing data");
			throw new BadRequestAlertException("Bad request: missing departments", ENTITY_NAME, "missing_departments");
		}
		businessDaysService.deleteByListId(ids);
		return ResponseDTO.<List<String>>builder().code(String.valueOf(HttpStatus.OK.value())).data(ids).build();
	}

	@PreAuthorize("hasRole('ROLE_BUSINESSDAYS_UPDATE')&&hasRole('ROLE_BUSINESSDAYS_ACCESS')")
	@PutMapping("/")
	public ResponseDTO<BusinessDaysDTO> update(@RequestBody @Valid BusinessDaysDTO businessDaysDTO) throws IOException {
		businessDaysService.update(businessDaysDTO);
		return ResponseDTO.<BusinessDaysDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(businessDaysDTO)
				.build();

	}

	@PreAuthorize("hasRole('ROLE_BUSINESSDAYS_VIEW')&&hasRole('ROLE_BUSINESSDAYS_ACCESS')")
	@GetMapping("/all")
	public ResponseDTO<List<BusinessDaysDTO>> getAll() throws IOException {
		return ResponseDTO.<List<BusinessDaysDTO>>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(businessDaysService.getAll()).build();
	}

	@PreAuthorize("hasRole('ROLE_BUSINESSDAYS_VIEW'&&hasRole('ROLE_BUSINESSDAYS_ACCESS'))")
	@PostMapping("/searchByType")
	public ResponseDTO<List<BusinessDaysDTO>> searchbyType(@RequestBody @Valid SearchDTO searchDTO) {
//		logger.info("Create by" + currentuser.getUsername());
		return businessDaysService.searchByType(searchDTO);
	}
//	@PreAuthorize("hasRole('ROLE_BUSINESSDAYS_VIEW')&&hasRole('ROLE_BUSINESSDAYS_ACCESS')")
	@PostMapping("/search")
	public ResponseDTO<List<BusinessDaysDTO>> search(@RequestBody @Valid SearchAttendence searchDTO) {
//		logger.info("Create by"+ currentuser.getUsername());
		return businessDaysService.search(searchDTO);
	}

}
