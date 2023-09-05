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

	private String localFilePath = "/home/luanl/Documents/Eclipse/mdt/UmlModel/MyProjectToReceiveModification.uml";

	@Autowired
	private ChangePropagation changePropagationService;

	@PostMapping(value = "/uml/send")
	public int sendUmlServer() {
		try {
			Boolean propagated = changePropagationService.sendUMLChanges(this.localFilePath);

			if (propagated) {
				return HttpServletResponse.SC_OK;
			}
			
			System.out.println("Propagated: " + propagated);

			return HttpServletResponse.SC_BAD_REQUEST;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return HttpServletResponse.SC_NOT_FOUND;
	}

	@PostMapping(value = "/uml/receive")
	public int receiveVersion(@RequestParam String umlVersion) {
		try {
			System.out.println("Receive the version: " + umlVersion);

			Boolean merged = changePropagationService.receiveUMLChanges(umlVersion, this.localFilePath);
			System.out.println("New version merged: " + merged + "!\n");

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
