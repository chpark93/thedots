package com.ch.core.config;

import com.ch.core.common.response.ErrorResponse;
import com.ch.core.model.code.Errors;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .version("v1.0.0")
                .title("째깍섬 예약 API")
                .description("째깍섬 예약 및 현황 조회 API");

        return new OpenAPI()
                .info(info)
                .components(new Components()
                        .addResponses("BAD_REQUEST", createBadRequestResponse())
                        .addResponses("UNAUTHORIZED", createUnauthorizedResponse())
                        .addResponses("FORBIDDEN", createForbiddenResponse())
                        .addResponses("NOT_FOUND", createNotFoundResponse())
                        .addResponses("INTERNAL_SERVER_ERROR", createServerErrorResponse())
                );
    }

    private ApiResponse createBadRequestResponse() {
        MediaType jsonMediaType = new MediaType().addExamples("DUPLICATED_USER_AND_COURSE", createErrorExample(Errors.DUPLICATED_USER_AND_COURSE))
                .addExamples("NOT_FOUND_USER", createErrorExample(Errors.NOT_FOUND_USER))
                .addExamples("NOT_FOUND_RESERVATION_INFO", createErrorExample(Errors.NOT_FOUND_RESERVATION_INFO))
                .addExamples("NOT_POSSIBLE_CANCEL", createErrorExample(Errors.NOT_POSSIBLE_CANCEL))
                .addExamples("NOT_POSSIBLE_RESERVATION", createErrorExample(Errors.NOT_POSSIBLE_RESERVATION))
                .addExamples("NOT_FOUND_STORE_COURSE", createErrorExample(Errors.NOT_FOUND_STORE_COURSE));

        return new ApiResponse()
                .description("BAD REQUEST")
                .content(new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, jsonMediaType));
    }

    private ApiResponse createUnauthorizedResponse() {
        MediaType jsonMediaType = new MediaType().addExamples("UNAUTHORIZED", createErrorExample(Errors.UNAUTHORIZED));

        return new ApiResponse()
                .description("UNAUTHORIZED")
                .content(new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, jsonMediaType));
    }

    private ApiResponse createForbiddenResponse() {
        MediaType jsonMediaType = new MediaType().addExamples("FORBIDDEN", createErrorExample(Errors.FORBIDDEN));

        return new ApiResponse()
                .description("FORBIDDEN")
                .content(new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, jsonMediaType));
    }

    private ApiResponse createNotFoundResponse() {
        MediaType jsonMediaType = new MediaType().addExamples("NOT_FOUND", createErrorExample(Errors.NOT_FOUND));

        return new ApiResponse()
                .description("NOT FOUND")
                .content(new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, jsonMediaType));
    }

    private ApiResponse createServerErrorResponse() {
        MediaType jsonMediaType = new MediaType().addExamples("INTERNAL_SERVER_ERROR", createErrorExample(Errors.INTERNAL_SERVER_ERROR));

        return new ApiResponse()
                .description("INTERNAL SERVER ERROR")
                .content(new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, jsonMediaType));
    }

    private Example createErrorExample(Errors code) {
        ErrorResponse response = ErrorResponse.of(code);

        return new Example().value(response);
    }
}