package com.scb.trade.service;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

public interface TradeService {
  StringWriter enrichTradeData(InputStream inputStream);
}
