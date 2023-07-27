package com.example.affablebeanui.config;

import graphql.scalars.ExtendedScalars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Controller
public class webConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/webui/");
    }

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer(){
        return runtimeWiringConfigurer -> runtimeWiringConfigurer.scalar(ExtendedScalars.Date)
                .scalar(ExtendedScalars.Url);
    }

    private final Logger logger = LoggerFactory.getLogger(webConfig.class);

    @Bean
    public GraphQlSourceBuilderCustomizer inspectionCustomizer(){
        return source -> source.inspectSchemaMappings(
                schemaReport -> logger.info(schemaReport.toString())
        );
    }
}
