package com.scb.trade.controller;

import com.scb.trade.exception.InvalidTradeDataException;
import com.scb.trade.service.TradeService;
import com.scb.trade.valdation.ValidateCsvFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

@Validated
@RestController
@RequestMapping("/api/v1")
public class TradeController {
  private static final Logger logger = LoggerFactory.getLogger(TradeController.class);
  private final TradeService tradeService;

  public TradeController(TradeService tradeService) {
    this.tradeService = tradeService;
  }

  @PostMapping(value = "/enrich",
      produces = "text/csv",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> enrichTradeData(@RequestPart("file")
                                                @ValidateCsvFile
                                                MultipartFile file) {
    try (InputStream inputStream = file.getInputStream()) {
      StringWriter enrichTradeData = tradeService.enrichTradeData(inputStream);

      return ResponseEntity.ok().body(enrichTradeData.toString().trim());
    } catch (IOException ioException) {
      logger.error("Error occurred while enriching trade data", ioException);
      throw new InvalidTradeDataException("Invalid trade data. Please check the uploaded file.");
    }
  }
}
