package com.istt.staff_notification_v2.apis;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.validation.Valid;

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
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.dto.RuleDTO;
import com.istt.staff_notification_v2.dto.SearchDTO;
import com.istt.staff_notification_v2.security.securityv2.CurrentUser;
import com.istt.staff_notification_v2.security.securityv2.UserPrincipal;
import com.istt.staff_notification_v2.service.RuleService;

@RestController
@RequestMapping("/rule")
public class RuleAPI {
	@Autowired
	private RuleService ruleService;

	private static final String ENTITY_NAME = "isttRule";

	@PreAuthorize("hasRole('ROLE_RULE_CREATE')")
	@PostMapping("")
	public ResponseDTO<RuleDTO> create(@RequestBody RuleDTO ruleDTO) throws URISyntaxException {

		if (ruleDTO.getEmployee() == null) {
			throw new BadRequestAlertException("Bad request: missing data", ENTITY_NAME, "missing_rule");
		}
		ruleService.create(ruleDTO);
		return ResponseDTO.<RuleDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(ruleDTO).build();
	}

	@PreAuthorize("hasRole('ROLE_RULE_DELETE')")
	@DeleteMapping("/{id}")
	public ResponseDTO<Void> delete(@CurrentUser UserPrincipal currentUser, @PathVariable(value = "id") String id)
			throws URISyntaxException {
		if (id == null) {
			throw new BadRequestAlertException("Bad request: missing data", ENTITY_NAME, "missing_id");
		}
		ruleService.delete(id);
		return ResponseDTO.<Void>builder().code(String.valueOf(HttpStatus.OK.value())).build();
	}

	@PreAuthorize("hasRole('ROLE_RULE_SEARCH')")
	@PostMapping("/search")
	public ResponseDTO<List<RuleDTO>> search(@RequestBody @Valid SearchDTO searchDTO) {
		return ruleService.search(searchDTO);
	}

	@PreAuthorize("hasRole('ROLE_RULE_UPDATE')")
	@PutMapping("/")
	public ResponseDTO<RuleDTO> update(@RequestBody @Valid RuleDTO ruleDTO) throws IOException {
		ruleService.update(ruleDTO);
		return ResponseDTO.<RuleDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(ruleDTO).build();

	}

	@PreAuthorize("hasRole('ROLE_RULE_GETALL')")
	@GetMapping("/all")
	public ResponseDTO<List<RuleDTO>> getAll() throws IOException {
		return ResponseDTO.<List<RuleDTO>>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(ruleService.getAll()).build();
	}

}
