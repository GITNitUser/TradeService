package com.scb.trade.valdation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateFormatValidator {
  private static final String DATE_FORMAT_PATTERN = "yyyyMMdd";

  public static boolean isValidDateFormat(String inputDate) {
    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);
      //noinspection ResultOfMethodCallIgnored
      LocalDate.parse(inputDate, formatter);
      return true;
    } catch (DateTimeParseException e) {
      return false;
    }
  }
}
