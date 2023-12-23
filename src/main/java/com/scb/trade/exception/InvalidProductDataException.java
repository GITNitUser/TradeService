package com.scb.trade.exception;

/**
 * Exception thrown when a product file is not found.
 */
public class InvalidProductDataException extends RuntimeException {
  public InvalidProductDataException(String message) {
    super(message);
  }
}
