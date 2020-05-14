package com.manh.encryptconfigfile.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

//@ControllerAdvice
public class MvcExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity errorHandler(Exception ex) {
		return new ResponseEntity<>("An error occured!", HttpStatus.BAD_REQUEST);
	}
}
