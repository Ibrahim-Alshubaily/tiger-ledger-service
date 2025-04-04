package com.alshubaily.fintech.tiger_ledger_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TigerLedgerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TigerLedgerServiceApplication.class, args);
	}

}
