package com.scb.trade.valdation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.springframework.web.multipart.MultipartFile;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CsvFileValidator.class)
public @interface ValidateCsvFile {
  String message() default "Invalid CSV file";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}

class CsvFileValidator implements ConstraintValidator<ValidateCsvFile, MultipartFile> {
  @Override
  public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
    return multipartFile != null && !multipartFile.isEmpty()
        && multipartFile.getOriginalFilename() != null
        && multipartFile.getOriginalFilename().endsWith(".csv");
  }
}
