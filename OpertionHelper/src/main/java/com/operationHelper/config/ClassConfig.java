package com.operationHelper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.operationHelper.utils.HelperFunctions;

@Configuration
public class ClassConfig {

	@Bean
	public HelperFunctions helperFunctions() {
		return new HelperFunctions();
	}
}
