package com.threedsoft.config.endpoint.rest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.threedsoft.config.dto.responses.ConfigDTO;
import com.threedsoft.config.service.ConfigServiceImpl;
import com.threedsoft.util.dto.ErrorResourceDTO;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/api/v1/configs")
@RefreshScope
@Slf4j
public class ConfigRestEndPoint {

	private String healthMsg="service is healthy";

	private String readyMsg="service is ready";

	@Autowired
	private ConfigServiceImpl configService;

	@GetMapping("/ready")
	public ResponseEntity ready() throws Exception {
		return ResponseEntity.ok(readyMsg);
	}

	@GetMapping("/health")
	public ResponseEntity health() throws Exception {
		return ResponseEntity.ok(healthMsg);
	}

	@GetMapping("/{busName}")
	public ResponseEntity getConfigForBusName(@PathVariable("busName") String busName) throws IOException {
		try {
			return ResponseEntity.ok(configService.getAllConfig(busName,-1));
		} catch (Exception e) {
			log.error("Error Occured for busName:" + busName + e.getMessage());
			return ResponseEntity.badRequest().body(new ErrorResourceDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Error Occured while getting config for busName:" + busName + " : " + e.getMessage()));
		}
	}

	@GetMapping("/{busName}/{locnNbr}")
	public ResponseEntity getConfigForBusNameAndLocnNbr(@PathVariable("busName") String busName,
			@PathVariable("locnNbr") Integer locnNbr) throws IOException {
		try {
			return ResponseEntity.ok(configService.getAllConfig(busName, locnNbr));
		} catch (Exception e) {
			log.error("Error Occured for busName:" + busName + ", locnNbr:" + locnNbr + " : " + e.getMessage());
			return ResponseEntity.badRequest()
					.body(new ErrorResourceDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(),
							"Error Occured while getting config for busName:" + busName + ", locnNbr:" + locnNbr + " : "
									+ e.getMessage()));
		}
	}

	@PostMapping("/{busName}")
	public ResponseEntity overrideConfigForBusName(@PathVariable("busName") String busName,
			@RequestBody ConfigDTO configDTO) throws IOException {
		long startTime = System.currentTimeMillis();
		log.info("Received config override request for busName: " + busName
				+ ": at :" + LocalDateTime.now() );
		ResponseEntity resEntity = null;
		try {
			resEntity = ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
					.body(configService.overrideConfigForBusName(busName, configDTO));
		} catch (Exception ex) {
			log.error("overrideConfigForBusName Error:", ex);
			resEntity = ResponseEntity.badRequest().body(new ErrorResourceDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Error occured while overriding properties:" + ex.getMessage(), configDTO));
		}
		long endTime = System.currentTimeMillis();
		log.info("Completed config override request for busName: " + busName 
				+ ": at :" + LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs");
		return resEntity;
	}

	@PostMapping("/{busName}/{locnNbr}")
	public ResponseEntity overrideConfigForBusNameAndLocnNbr(@PathVariable("busName") String busName,
			@PathVariable("locnNbr") Integer locnNbr, @RequestBody ConfigDTO configDTO) throws IOException {
		long startTime = System.currentTimeMillis();
		log.info("Received config override request for busName: " + busName + ",locnNbr:" + locnNbr 
				+ ": at :" + LocalDateTime.now() + ":" + configDTO);
		ResponseEntity resEntity = null;
		try {
			resEntity = ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
					.body(configService.overrideConfigForBusNameAndLocnNbr(busName, locnNbr, configDTO));
		} catch (Exception ex) {
			log.error("overrideConfigForBusName Error:", ex);
			resEntity = ResponseEntity.badRequest().body(new ErrorResourceDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Error occured while overriding properties:" + ex.getMessage(), configDTO));
		}
		long endTime = System.currentTimeMillis();
		log.info("Completed config override request for busName: " + busName + ",locnNbr:" + locnNbr
				+ ": at :" + LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs");
		return resEntity;
	}
}
