package com.apiveterinaria.veterinaria.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Veterinaria")
                        .description("API REST para la gestión clínica, inventario y vacunación de mascotas.")
                        .version("1.0.0"));
    }

    @Bean
    public OperationCustomizer customGlobalHeaders() {
        return (operation, handlerMethod) -> {
            operation.addParametersItem(
                    new Parameter()
                            .in("header")
                            .name("X-Usuario-Id")
                            .description("UUID del usuario actor (opcional, para trazabilidad y auditoría)")
                            .required(false)
                            .schema(new StringSchema().format("uuid").example("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"))
            );
            return operation;
        };
    }
}
