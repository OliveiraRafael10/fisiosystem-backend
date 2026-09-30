package com.fisio.fisiosystem.Exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<Map<String, Object>> tratarResponseStatusException(
	        ResponseStatusException ex) {

	    Map<String, Object> erro = new HashMap<>();

	    erro.put("status", ex.getStatusCode().value());
	    erro.put("erro", ex.getStatusCode().toString());
	    erro.put("mensagem", ex.getReason());
	    erro.put("dataHora", LocalDateTime.now());

	    return ResponseEntity
	            .status(ex.getStatusCode())
	            .body(erro);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> tratarErroGenerico(
	        Exception ex) {

	    Map<String, Object> erro = new HashMap<>();

	    erro.put("status", 500);
	    erro.put("erro", "INTERNAL_SERVER_ERROR");
	    erro.put("mensagem", "Ocorreu um erro interno no servidor");
	    erro.put("dataHora", LocalDateTime.now());

	    return ResponseEntity
	            .status(500)
	            .body(erro);
	}
}
