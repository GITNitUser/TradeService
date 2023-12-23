package com.scb.trade.service;

import com.scb.trade.exception.InvalidTradeDataException;
import com.scb.trade.service.impl.TradeServiceImpl;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TradeServiceImplTest {
  private TradeService tradeService;

  @Test
  public void test_enrich_trade_data_with_valid_input() {
    // Given
    InputStream tradeData = new ByteArrayInputStream("date,product_id,currency,price\n20160101,1,EUR,10.0" .getBytes());
    CsvFileService<Map<String, String>> csvFileService = mock(CsvFileService.class);
    when(csvFileService.loadCsvFileData()).
        thenReturn(Map.of("product_id", "product_name",
            "1", "Treasury Bills Domestic"));

    tradeService = new TradeServiceImpl(csvFileService);

    // When
    StringWriter result = tradeService.enrichTradeData(tradeData);

    // Then
    assertNotNull(result);
    assertEquals(expectedOutput(), result.toString());
  }

  @Test
  public void test_handles_missing_product_names_gracefully() {
    // Given
    InputStream tradeData = new ByteArrayInputStream("date,product_id,currency,price\n20160101,1,EUR,10.0" .getBytes());
    CsvFileService<Map<String, String>> csvFileService = mock(CsvFileService.class);
    when(csvFileService.loadCsvFileData()).thenReturn(Collections.singletonMap("product_id", "product_name"));
    tradeService = new TradeServiceImpl(csvFileService);

    // When
    StringWriter result = tradeService.enrichTradeData(tradeData);

    // Then
    assertNotNull(result);
    assertEquals("\"date\",\"product_name\",\"currency\",\"price\"\n" +
        "\"20160101\",\"Missing Product Name\",\"EUR\",\"10.0\"\n", result.toString());
  }

  @Test
  public void test_handles_invalid_trade_date_gracefully() {
    // Given
    InputStream tradeData = new ByteArrayInputStream(("date,product_id,currency,price\n" +
        "20160101,1,EUR,10.0\n" +
        "2016-01-01,1,EUR,10.0")
        .getBytes());
    CsvFileService<Map<String, String>> csvFileService = mock(CsvFileService.class);
    when(csvFileService.loadCsvFileData()).thenReturn(Map.of("product_id", "product_name",
        "1", "Treasury Bills Domestic"));
    TradeService tradeService = new TradeServiceImpl(csvFileService);

    // When
    StringWriter result = tradeService.enrichTradeData(tradeData);

    // Then
    assertNotNull(result);
    assertEquals(expectedOutput(), result.toString());
  }

  @Test
  public void test_filters_out_blank_lines_from_input() {
    // Given
    InputStream tradeData = new ByteArrayInputStream("date,product_id,currency,price\n\n20160101,1,EUR,10.0\n\n"
        .getBytes());
    CsvFileService<Map<String, String>> csvFileService = mock(CsvFileService.class);
    when(csvFileService.loadCsvFileData()).thenReturn(Map.of("product_id", "product_name",
        "1", "Treasury Bills Domestic"));
    TradeService tradeService = new TradeServiceImpl(csvFileService);

    // When
    StringWriter result = tradeService.enrichTradeData(tradeData);

    // Then
    assertNotNull(result);
    assertEquals(expectedOutput(), result.toString());
  }

  @Test
  public void test_throws_trade_exception_for_invalid_input_file() {
    // Given
    InputStream tradeData = new ByteArrayInputStream("date,product_id,currency,price\n20160101\n" .getBytes());
    CsvFileService<Map<String, String>> csvFileService = mock(CsvFileService.class);
    when(csvFileService.loadCsvFileData()).thenReturn(Collections.singletonMap("product_id", "product_name"));
    TradeService tradeService = new TradeServiceImpl(csvFileService);

    // When/Then
    assertThrows(InvalidTradeDataException.class, () -> tradeService.enrichTradeData(tradeData));
  }

  @Test
  public void test_handles_null_input_stream_gracefully() {
    // Given
    CsvFileService<Map<String, String>> csvFileService = mock(CsvFileService.class);
    when(csvFileService.loadCsvFileData()).thenReturn(Collections.singletonMap("product_id", "product_name"));
    TradeService tradeService = new TradeServiceImpl(csvFileService);

    // When/Then
    assertThrows(InvalidTradeDataException.class, () -> tradeService.enrichTradeData(null));
  }

  @Test
  public void test_handles_empty_input_stream_gracefully() {
    // Given
    InputStream tradeData = new ByteArrayInputStream("" .getBytes());
    CsvFileService<Map<String, String>> csvFileService = mock(CsvFileService.class);
    when(csvFileService.loadCsvFileData()).thenReturn(Map.of("product_id", "product_name",
        "1", "Treasury Bills Domestic"));
    TradeService tradeService = new TradeServiceImpl(csvFileService);

    // When/Then
    assertThrows(InvalidTradeDataException.class, () -> tradeService.enrichTradeData(tradeData));
  }

  private String expectedOutput() {
    return "\"date\",\"product_name\",\"currency\",\"price\"\n" +
        "\"20160101\",\"Treasury Bills Domestic\",\"EUR\",\"10.0\"\n";
  }
}