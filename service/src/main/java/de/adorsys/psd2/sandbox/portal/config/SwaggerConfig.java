package de.adorsys.psd2.sandbox.portal.config;

import java.util.ArrayList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.InMemorySwaggerResourcesProvider;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

  @Bean
  UiConfiguration uiConfiguration() {
    return UiConfigurationBuilder.builder()
        .supportedSubmitMethods(new String[]{})
        .build();
  }

  @Bean
  Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(new ApiInfoBuilder()
            .title("Certificate Generator")
            .description("Certificate Generator for Testing Purposes of PSD2 Sandbox Environment")
            .contact(new Contact(
                "swi, adorsys GmbH & Co. KG",
                "https://adorsys.de",
                "swi@adorsys.de")
            )
            .version("1.0.0")
            .build())
        .groupName("Certificate Generator API")
        .select()
        .apis(RequestHandlerSelectors
            .basePackage("de.adorsys.psd2.sandbox.portal.certificateserver"))
        .paths(PathSelectors.any())
        .build();
  }

  @Bean
  @Primary
  SwaggerResourcesProvider swaggerResourcesProvider(
      InMemorySwaggerResourcesProvider defaultResourcesProvider) {

    return () -> {
      SwaggerResource swaggerResource = new SwaggerResource();
      swaggerResource.setName("PSD2 API");
      swaggerResource.setSwaggerVersion("3.0.1");
      swaggerResource.setUrl("/psd2-api-1.2-Update-2018-08-17.yaml");

      ArrayList<SwaggerResource> resources = new ArrayList<>();

      resources.add(swaggerResource);
      resources.addAll(defaultResourcesProvider.get());

      return resources;
    };
  }
}
