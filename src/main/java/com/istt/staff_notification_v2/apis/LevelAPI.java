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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.dto.LevelDTO;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.dto.SearchDTO;
import com.istt.staff_notification_v2.security.securityv2.CurrentUser;
import com.istt.staff_notification_v2.security.securityv2.UserPrincipal;
import com.istt.staff_notification_v2.service.LevelService;

@RestController
@RequestMapping("/level")
public class LevelAPI {
	@Autowired
	private LevelService levelService;

	private static final String ENTITY_NAME = "isttLevel";
	
//	@Autowired
//	private FileService fileService;

//	@PostMapping("/uploadFilesIntoDB")
//	public ResponseDTO<String> storeFilesIntoDB(@RequestParam MultipartFile file) throws IOException, CsvException {
//		fileService.save(file);
//
//		return ResponseDTO.<String>builder().code(String.valueOf(HttpStatus.OK.value())).build();
//	}

	@PostMapping("")
//	@PreAuthorize("hasRole('ROLE_LEVEL_CREATE')")
	public ResponseDTO<LevelDTO> create(@RequestBody LevelDTO levelDTO) throws URISyntaxException {
		if (levelDTO.getLevelCode() == null || levelDTO.getLevelName() == null) {
			throw new BadRequestAlertException("Bad request: missing data", ENTITY_NAME, "missing_level");
		}
		levelService.create(levelDTO);
		return ResponseDTO.<LevelDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(levelDTO).build();
	}

//	@PreAuthorize("hasRole('ROLE_LEVEL_DELETE')")
	@DeleteMapping("/{id}")
	public ResponseDTO<Void> delete(@CurrentUser UserPrincipal currentUser, @PathVariable(value = "id") String id)
			throws URISyntaxException {
		if (id == null) {
			throw new BadRequestAlertException("Bad request: missing data", ENTITY_NAME, "missing_id");
		}
		levelService.delete(id);
		return ResponseDTO.<Void>builder().code(String.valueOf(HttpStatus.OK.value())).build();
	}

//	@PreAuthorize("hasRole('ROLE_LEVEL_SEARCH')")
	@PostMapping("/search")
	public ResponseDTO<List<LevelDTO>> search(@RequestBody @Valid SearchDTO searchDTO) {
		return levelService.search(searchDTO);
	}

//	@PreAuthorize("hasRole('ROLE_LEVEL_DELETE')")
	@DeleteMapping("/ids")
	public ResponseDTO<List<String>> deletebyListId(@RequestBody @Valid List<String> ids) throws URISyntaxException {

		if (ids.isEmpty()) {
			throw new BadRequestAlertException("Bad request: missing levels", ENTITY_NAME, "missing_levels");
		}
		levelService.deleteAllbyIds(ids);
		return ResponseDTO.<List<String>>builder().code(String.valueOf(HttpStatus.OK.value())).data(ids).build();
	}

//	@PreAuthorize("hasRole('ROLE_LEVEL_UPDATE')")
	@PutMapping("/")
	public ResponseDTO<LevelDTO> update(@RequestBody @Valid LevelDTO levelDTO) throws IOException {
		levelService.update(levelDTO);
		return ResponseDTO.<LevelDTO>builder().code(String.valueOf(HttpStatus.OK.value())).data(levelDTO).build();

	}

//	@PreAuthorize("hasRole('ROLE_LEVEL_GETALL')")
	@GetMapping("/all")
	public ResponseDTO<List<LevelDTO>> getAll() throws IOException {
		return ResponseDTO.<List<LevelDTO>>builder().code(String.valueOf(HttpStatus.OK.value()))
				.data(levelService.getAll()).build();
	}

}
