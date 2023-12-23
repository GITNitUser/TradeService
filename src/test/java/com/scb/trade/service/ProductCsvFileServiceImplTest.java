package com.scb.trade.service;

import com.scb.trade.exception.InvalidProductDataException;
import com.scb.trade.service.impl.ProductCsvFileServiceImpl;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ProductCsvFileServiceImplTest {
  private CsvFileService<Map<String, String>> productService;

  @Test
  public void test_loads_and_returns_product_data() {
    // Given
    String productFilePath = "src/test/resources/product/product.csv";
    productService = new ProductCsvFileServiceImpl(productFilePath);

    // When
    Map<String, String> result = productService.loadCsvFileData();

    // Then
    assertNotNull(result);
    assertFalse(result.isEmpty());
  }

  @Test
  public void test_handles_empty_csv_file() {
    // Given
    String productFilePath = "src/test/resources/product/empty_product.csv";
    productService = new ProductCsvFileServiceImpl(productFilePath);

    // When
    Map<String, String> result = productService.loadCsvFileData();

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void test_handles_csv_file_with_only_header() {
    // Given
    String productFilePath = "src/test/resources/product/header_only_product.csv";
    productService = new ProductCsvFileServiceImpl(productFilePath);

    // When
    Map<String, String> result = productService.loadCsvFileData();

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
  }

  @Test
  public void test_handles_csv_file_with_missing_values() {
    // Given
    String productFilePath = "src/test/resources/product/missing_values_product.csv";
    ProductCsvFileServiceImpl productService = new ProductCsvFileServiceImpl(productFilePath);

    // When, Then
    assertThrows(InvalidProductDataException.class, productService::loadCsvFileData);
  }

  @Test
  public void test_handles_csv_file_with_missing_keys() {
    // Given
    String productFilePath = "src/test/resources/product/missing_keys_product.csv";
    productService = new ProductCsvFileServiceImpl(productFilePath);

    // When
    Map<String, String> result = productService.loadCsvFileData();

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
  }

  @Test
  public void test_handles_csv_file_with_invalid_format() {
    // Given
    String productFilePath = "src/test/resources/product/invalid_format_product.csv";
    productService = new ProductCsvFileServiceImpl(productFilePath);

    // When, Then
    assertThrows(InvalidProductDataException.class, () -> productService.loadCsvFileData());
  }
}
