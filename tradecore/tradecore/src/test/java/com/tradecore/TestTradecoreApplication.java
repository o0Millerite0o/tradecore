package com.tradecore;

import org.springframework.boot.SpringApplication;

public class TestTradecoreApplication {

	public static void main(String[] args) {
		SpringApplication.from(TradecoreApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
