package org.example.school_bus_tracker_be.Config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI/Swagger documentation.
 *
 * <p>
 * Defines a global security scheme named {@code bearerAuth} using the HTTP
 * bearer authentication type. It also adds a default security requirement so
 * that all secured endpoints are marked as requiring a JWT token in the
 * generated documentation. Unsecured endpoints (e.g. login) will override
 * this requirement by omitting the annotation at the controller method level.
 */
@OpenAPIDefinition(
        info = @Info(
                title = "School Bus Tracker API",
                version = "1.0",
                description = "API documentation for the School Bus Tracker application"
        ),
        security = {@SecurityRequirement(name = "bearerAuth")}
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Enter the JWT token in the format: Bearer &lt;token&gt;"
)
@Configuration
public class OpenApiConfig {
    // Intentionally left blank. Configuration is handled via annotations.
}