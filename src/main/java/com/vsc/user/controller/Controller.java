package com.vsc.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vsc.user.service.ChangePropagation;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/client")
public class Controller {

	private static final Logger logger = LoggerFactory.getLogger(Controller.class);

	@Autowired
	private ChangePropagation changePropagationService;

	@PostMapping(value = "/uml/send")
	public int sendUmlServer() {
		try {
			Boolean propagated = changePropagationService.sendUMLChanges();

			if (propagated) {
				return HttpServletResponse.SC_OK;
			}
			
			logger.info("Local changes propagated: " + propagated);

			return HttpServletResponse.SC_BAD_REQUEST;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return HttpServletResponse.SC_NOT_FOUND;
	}

	@PostMapping(value = "/uml/receive")
	public int receiveVersion(@RequestParam String umlVersion) {
		try {
			logger.info("Init merge version: " + umlVersion);

			Boolean merged = changePropagationService.mergeUMLVersionChanges(umlVersion);
			
			logger.info("Final merged version: " + merged + "!\n");

			if (merged) {
				return HttpServletResponse.SC_OK;
			}

			return HttpServletResponse.SC_BAD_REQUEST;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return HttpServletResponse.SC_NOT_FOUND;
	}
}
