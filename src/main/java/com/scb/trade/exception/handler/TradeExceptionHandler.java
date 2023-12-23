package com.scb.trade.exception.handler;

import com.scb.trade.exception.InvalidProductDataException;
import com.scb.trade.exception.InvalidTradeDataException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.stream.Collectors;

/**
 * Trade Exception Handler class
 */
@ControllerAdvice
public class TradeExceptionHandler {
  private static final Logger logger = LoggerFactory.getLogger(TradeExceptionHandler.class);

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<String> handleConstraintViolationException(
      ConstraintViolationException ex,
      HttpServletRequest request) {

    logRequestException(ex, request);
    String errorMessage = ex.getConstraintViolations()
        .stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.joining(", "));

    return ResponseEntity.badRequest().body(errorMessage);
  }

  @ExceptionHandler(MissingServletRequestPartException.class)
  public ResponseEntity<String> handleMissingServletRequestPartException(
      MissingServletRequestPartException ex,
      HttpServletRequest request
  ) {
    logRequestException(ex, request);
    return ResponseEntity.badRequest().body(ex.getMessage());
  }

  @ExceptionHandler(InvalidTradeDataException.class)
  public ResponseEntity<String> handleTradeException(
      InvalidTradeDataException ex,
      HttpServletRequest request
  ) {
    logRequestException(ex, request);
    return ResponseEntity.badRequest().body(ex.getMessage());
  }

  @ExceptionHandler(InvalidProductDataException.class)
  public ResponseEntity<String> handleProductFileNotFoundException(
      InvalidProductDataException ex,
      HttpServletRequest request
  ) {
    logRequestException(ex, request);
    return ResponseEntity.internalServerError().body(ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGeneraException(
      Exception ex,
      HttpServletRequest request
  ) {
    logRequestException(ex, request);
    return ResponseEntity.internalServerError().body(ex.getMessage());
  }

  private void logRequestException(Exception ex, HttpServletRequest request) {
    String message = String.format(
        "%s handled in %s %s. Error is %s", ex.getClass().getName(),
        request.getMethod(),
        request.getRequestURI(),
        ex.getMessage());
    logger.warn(message, ex);
  }
}
