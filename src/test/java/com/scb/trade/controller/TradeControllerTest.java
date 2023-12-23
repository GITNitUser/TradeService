package com.scb.trade.controller;

import com.scb.trade.exception.InvalidTradeDataException;
import com.scb.trade.exception.InvalidProductDataException;
import com.scb.trade.service.TradeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@RunWith(SpringRunner.class)
public class TradeControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TradeService tradeService;


  @Test
  public void test_enrich_trade_data_with_valid_csv_file() throws Exception {
    String testData = "date,product_id,currency,price\n20160101,1,EUR,10.0";
      MockMultipartFile file = new MockMultipartFile("file", "validTradeFile.csv",
          MediaType.MULTIPART_FORM_DATA_VALUE, testData.getBytes());
      StringWriter enrichTradeData = new StringWriter();
      enrichTradeData.write(testData);

      when(tradeService.enrichTradeData(any(InputStream.class))).thenReturn(enrichTradeData);

      mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/enrich")
              .file(file))
          .andExpect(status().isOk())
          .andExpect(MockMvcResultMatchers.content().contentType("text/csv;charset=UTF-8"))
          .andExpect(MockMvcResultMatchers.content().string(testData));

      verify(tradeService, times(1)).enrichTradeData(any(InputStream.class));
  }

  @Test
  public void test_enrich_trade_data_with_invalid_file_extension() throws Exception {
      MockMultipartFile file = new MockMultipartFile("file", "invalidTradeFileExtension.txt",
          MediaType.MULTIPART_FORM_DATA_VALUE, "Test Data".getBytes(StandardCharsets.UTF_8));

      mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/enrich")
              .file(file))
          .andExpect(status().is4xxClientError())
          .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=UTF-8"))
          .andExpect(MockMvcResultMatchers.content().string("Invalid CSV file"));
  }

  @Test
  public void test_enrich_trade_data_with_empty_csv_file() throws Exception {
    MockMultipartFile file = new MockMultipartFile("file", "emptyTradeFile.csv",
        MediaType.MULTIPART_FORM_DATA_VALUE, "".getBytes(StandardCharsets.UTF_8));

    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/enrich")
            .file(file))
        .andExpect(status().is4xxClientError())
        .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.content().string("Invalid CSV file"));
  }

  @Test
  public void test_enrich_trade_data_with_invalid_csv_file() throws Exception {
    String testData = "invalid data in csv file\n20160101,1,EUR,10.0";
    MockMultipartFile file = new MockMultipartFile("file", "invalidTradeFile.csv",
        MediaType.MULTIPART_FORM_DATA_VALUE, testData.getBytes());

    when(tradeService.enrichTradeData(any(InputStream.class)))
        .thenThrow(new InvalidTradeDataException("Invalid trade data. Please check the uploaded file."));

    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/enrich")
            .file(file))
        .andExpect(status().is4xxClientError())
        .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.content().string("Invalid trade data. Please check the uploaded file."));

    verify(tradeService, times(1)).enrichTradeData(any(InputStream.class));
  }

  @Test
  public void test_enrich_trade_data_with_invalid_product_file() throws Exception {
    String testData = "date,product_id,currency,price\n20160101,1,EUR,10.0";
    MockMultipartFile file = new MockMultipartFile("file", "validTradeFile.csv",
        MediaType.MULTIPART_FORM_DATA_VALUE, testData.getBytes());

    when(tradeService.enrichTradeData(any(InputStream.class)))
        .thenThrow(new InvalidProductDataException("Error while loading product data"));

    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/enrich")
            .file(file))
        .andExpect(status().is5xxServerError())
        .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.content().string("Error while loading product data"));

    verify(tradeService, times(1)).enrichTradeData(any(InputStream.class));
  }

  @Test
  public void test_enrich_trade_data_with_array_out_of_bounds_exception() throws Exception {
    String testData = "date,product_id,currency,price\n20160101,1,EUR,10.0";
    MockMultipartFile file = new MockMultipartFile("file", "validTradeFile.csv",
        MediaType.MULTIPART_FORM_DATA_VALUE, testData.getBytes());

    when(tradeService.enrichTradeData(any(InputStream.class)))
        .thenThrow(new ArrayIndexOutOfBoundsException("line[0] is out of bounds"));

    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/enrich")
            .file(file))
        .andExpect(status().is5xxServerError())
        .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.content().string("line[0] is out of bounds"));

    verify(tradeService, times(1)).enrichTradeData(any(InputStream.class));
  }

  @Test
  public void test_enrich_trade_data_with_io_exception() throws Exception {
    String testData = "dsslflsnkmcx,cz,clddzl:clddzl\n20160101,1,EUR,10.0";
    MockMultipartFile file = spy(new MockMultipartFile("file", "invalidTradeFile.csv",
        MediaType.MULTIPART_FORM_DATA_VALUE, testData.getBytes()));

    when(file.getInputStream()).thenThrow(new IOException("File not found"));

    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/enrich")
            .file(file))
        .andExpect(status().is4xxClientError())
        .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.content().string("Invalid trade data. Please check the uploaded file."));
  }

  @Test
  public void test_enrich_trade_data_with_invalid_multipart_request_parameter() throws Exception {
    String testData = "date,product_id,currency,price\n20160101,1,EUR,10.0";
    MockMultipartFile file = spy(new MockMultipartFile("file1", "validTradeFile.csv",
        MediaType.MULTIPART_FORM_DATA_VALUE, testData.getBytes()));

    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/enrich")
            .file(file))
        .andExpect(status().is4xxClientError())
        .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.content().string("Required part 'file' is not present."));
  }
}
