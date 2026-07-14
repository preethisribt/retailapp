package com.preethisri.retailapp.Swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI retailAppOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Retail App API")
                        .description("""
                                REST API documentation for Retail Application.
                                
                                This API provides endpoints to manage products.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Preethi Sri")));
    }
}
