package com.example.jbmotos.api.controller.exception;

import com.example.jbmotos.services.exception.ObjetoNaoEncontradoException;
import com.example.jbmotos.services.exception.RegraDeNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
        Map<String, String> erros = new HashMap<String, String>();
        ex.getBindingResult().getAllErrors()
                .stream()
                .forEach(erro -> {
                    String nomeCampo = ((FieldError) erro).getField();
                    String mensagemErro = erro.getDefaultMessage();
                    erros.put(nomeCampo, mensagemErro);
                });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> erros = new HashMap<String, String>();
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
