package com.example.demo.application;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.web.client.RestTemplate;

/**
 * @author Arne Vandamme
 * @since ${projectVersion}
 */
@Configuration
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class PetApiClientConfiguration
{
	@Bean
	public RestTemplate petApiClient() {
		return new RestTemplateBuilder()
				.rootUri( "http://localhost:9966/petclinic/api" )
				.build();
	}

	@Bean
	@Scope("prototype")
	public RestTemplate bookApiClient() {
		return new RestTemplateBuilder()
				.rootUri( "http://localhost:36449" )
				.build();
	}
}
