package com.scb.trade.exception;

/**
 * Exception thrown when a trade file data is invalid.
 */
public class InvalidTradeDataException extends RuntimeException {
  public InvalidTradeDataException(String message) {
    super(message);
  }
}
