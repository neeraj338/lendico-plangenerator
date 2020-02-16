package com.lendico.plangenerator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class BeanConfiguration {
	
	@Bean
	@Primary
	public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
		ObjectMapper objectMapper = builder
				.featuresToDisable(
		                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
		                SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS,
		                SerializationFeature.WRITE_DATES_WITH_ZONE_ID,
		                
		                DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE,
		                DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,
		                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
		        )
				.createXmlMapper(false).build();
		
		JavaTimeModule javaTimeModule = new JavaTimeModule();
        
		objectMapper.registerModule(javaTimeModule);

		return objectMapper;
	}
	
}
