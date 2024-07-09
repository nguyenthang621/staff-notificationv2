package com.istt.staff_notification_v2.apis;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.istt.staff_notification_v2.configuration.ApplicationProperties;
import com.istt.staff_notification_v2.dto.RequestDownload;

@RestController
public class DownloadAPI {

	@Autowired
	private ApplicationProperties props;

	@PreAuthorize("hasRole('ROLE_DOWLOAD')")
	@PostMapping("/download")
	public ResponseEntity<InputStreamResource> proxyDownload(@RequestBody RequestDownload requestDownload) {
		String flaskUrl = props.getApiServerHandleExcel();

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<RequestDownload> request = new HttpEntity<>(requestDownload, headers);
		ResponseEntity<byte[]> response = restTemplate.exchange(flaskUrl, HttpMethod.POST, request, byte[].class);

		// get name file in server file
		String contentDisposition = response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
		String filename = contentDisposition != null && contentDisposition.contains("filename=")
				? contentDisposition.split("filename=")[1]
				: "file.xlsx";

		InputStream targetStream = new ByteArrayInputStream(response.getBody());
		InputStreamResource resource = new InputStreamResource(targetStream);

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

		return ResponseEntity.ok().headers(responseHeaders).contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(resource);
	}
}