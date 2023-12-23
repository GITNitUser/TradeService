package com.scb.trade;

import org.apache.catalina.core.ApplicationContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class TradeServiceApplicationTests {

	@Autowired
	private TradeServiceApplication tradeServiceApplication;

	@Test
	void contextLoads() {
		assertNotNull(tradeServiceApplication);
	}

}
