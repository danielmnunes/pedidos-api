package com.dev.pedidos_api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI pedidosOpenApi() {
		return new OpenAPI().info(new Info()
				.title("Pedidos API")
				.description("Cadastro de clientes e produtos.")
				.version("1.0.0"));
	}

}
