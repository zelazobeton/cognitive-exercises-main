package com.zelazobeton.cognitiveexercises.configuration;

import java.util.ArrayList;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.google.common.base.Predicates;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Bean
    public Docket demoApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .ignoredParameterTypes(AuthenticationPrincipal.class)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(Predicates.not(PathSelectors.regex(".*/error")))
                .build()
                .apiInfo(this.metadata());
        }

    private ApiInfo metadata() {
        Contact contact = new Contact("Zelazobeton", "https://github.com/zelazobeton", "");

        return new ApiInfo(
            "Cognitive Exercises",
            "Site with games and exercises supposed to develop your cognitive skills",
            "1.0",
            "",
            contact,
            "Apache License Version 2.0",
            "https://www.apache.org/licenses/LICENSE-2.0.html",
            new ArrayList<>()
        );
    }
}
