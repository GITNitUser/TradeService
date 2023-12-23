package com.scb.trade.service.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.scb.trade.exception.InvalidTradeDataException;
import com.scb.trade.service.CsvFileService;
import com.scb.trade.service.TradeService;
import com.scb.trade.valdation.DateFormatValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TradeServiceImpl implements TradeService {
  private static final Logger logger = LoggerFactory.getLogger(TradeServiceImpl.class);
  private static final String DEFAULT_PRODUCT_NAME = "Missing Product Name";
  private final CsvFileService<Map<String, String>> csvFileService;

  public TradeServiceImpl(CsvFileService<Map<String, String>> csvFileService) {
    this.csvFileService = csvFileService;
  }

  @Override
  public StringWriter enrichTradeData(InputStream tradeData) {
    Map<String, String> productMapping = this.csvFileService.loadCsvFileData();

    try (CSVReader reader = new CSVReader(new InputStreamReader(tradeData));
         StringWriter streamWriter = new StringWriter();
         CSVWriter writer = new CSVWriter(streamWriter)) {

      String[] header = reader.readNext();
      header[1] = productMapping.get(header[1]).trim();
      writer.writeNext(header);

      List<String[]> enrichedLines = reader.readAll()
          .parallelStream()
          .filter(line -> Arrays.stream(line).noneMatch(String::isBlank))
          .filter(this::validateTradeDate)
          .peek(line -> {
            if (productMapping.get(line[1]) == null) {
              logger.error("Product not found for product id: {}", line[1]);
              line[1] = DEFAULT_PRODUCT_NAME;
            } else {
              line[1] = productMapping.get(line[1]).trim();
            }
          })
          .collect(Collectors.toList());

      writer.writeAll(enrichedLines);
      logger.info("File processed successfully");

      return streamWriter;
    } catch (Exception e) {
      logger.error("Error while enriching trade data", e);
      throw new InvalidTradeDataException("Invalid trade data. Please check the uploaded file.");
    }
  }

  private boolean validateTradeDate(String[] line) {
    boolean isValidDate = DateFormatValidator.isValidDateFormat(line[0]);
    if (!isValidDate) {
      logger.error("Invalid date format {} for product id: {}", line[0], line[1]);
    }
    return isValidDate;
  }
}
