package com.scb.trade.service.impl;

import com.scb.trade.exception.InvalidProductDataException;
import com.scb.trade.service.CsvFileService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductCsvFileServiceImpl implements CsvFileService<Map<String, String>> {
  private static final Logger logger = LoggerFactory.getLogger(ProductCsvFileServiceImpl.class);
  private static final String ERROR_MESSAGE = "Error while loading product data";
  private static final String LINE_SEPARATOR = ",";
  private final String productFilePath;

  public ProductCsvFileServiceImpl(@Value("${product.file}") String productFilePath) {
    this.productFilePath = productFilePath;
  }

  @Override
  public Map<String, String> loadCsvFileData() {
    try (BufferedReader reader = Files.newBufferedReader(Paths.get(productFilePath),
        StandardCharsets.UTF_8)) {

      return reader.lines()
          .parallel()
          .filter(StringUtils::isNotBlank)
          .filter(line -> line.contains(LINE_SEPARATOR))
          .map(line -> line.split(LINE_SEPARATOR))
          .collect(Collectors.toMap(
              lines -> lines[0],
              lines -> lines[1],
              (oldValue, newValue) -> newValue
          ));

    } catch (Exception e) {
      logger.error(ERROR_MESSAGE, e);
      throw new InvalidProductDataException(ERROR_MESSAGE);
    }
  }
}
