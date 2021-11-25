package me.ssu.spring_rest_api.common;

import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentationConfigurer;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

@TestConfiguration
public class RestDocsConfiguration {

    @Bean   // TODO Test에서 Bean Import 하기
    public RestDocsMockMvcConfigurationCustomizer restDocsMockMvcConfigurationCustomizer() {
        return configurer -> configurer.operationPreprocessors()
                .withResponseDefaults(prettyPrint())    // 요청 본문 문서화
                .withResponseDefaults(prettyPrint());   // 응답 본문 문서화
    }
}
