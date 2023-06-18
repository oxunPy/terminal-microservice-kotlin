package com.example.store.configuration

import com.example.common.swagger.BaseSwaggerConfiguration
import org.springframework.context.annotation.Configuration
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
class SwaggerDocumentation(
    basePackage: String =  "com.example.store",
    service: String = "store-service"
): BaseSwaggerConfiguration(basePackage, service)