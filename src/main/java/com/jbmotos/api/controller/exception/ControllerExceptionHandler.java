package com.jbmotos.api.controller.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.jbmotos.services.exception.RegraDeNegocioException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ObjetoNaoEncontradoException.class)
    public ResponseEntity<StandardError> ObjetoNaoEncontrado(
            ObjetoNaoEncontradoException ex, HttpServletRequest request) {
        StandardError error = StandardError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<StandardError> RegraDeNegocio(
            RegraDeNegocioException ex, HttpServletRequest request) {
        StandardError error = StandardError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> MethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();
        ex.getBindingResult().getAllErrors()
                .stream()
                .forEach(erro -> {
                    String nomeCampo = ((FieldError) erro).getField();
                    String mensagemErro = erro.getDefaultMessage();
                    int posicaoPrimeiroPonto = nomeCampo.indexOf(".");
                    if (posicaoPrimeiroPonto != -1) {
                        nomeCampo = nomeCampo.substring(posicaoPrimeiroPonto + 1);
                    }
                    erros.put(nomeCampo, mensagemErro);
                });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> erros = new HashMap<>();
        ex.getConstraintViolations()
                .stream()
                .forEach(erro -> {
                    String nomeMetodoComNomeCampo = erro.getPropertyPath().toString();
                    int posicaoPrimeiroPonto = nomeMetodoComNomeCampo.indexOf(".");
                    String somenteNomeCampo = nomeMetodoComNomeCampo.substring(posicaoPrimeiroPonto + 1);
                    String mensagemErro = erro.getMessage();
                    erros.put(somenteNomeCampo, mensagemErro);
                });
        return ResponseEntity.badRequest().body(erros);
    }
}
