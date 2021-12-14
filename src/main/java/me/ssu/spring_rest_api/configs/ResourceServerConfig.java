package me.ssu.spring_rest_api.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("event");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            // TODO anonymous(익명 사용자)
            .anonymous()
                .and()
            // TODO 요청에 대한 권한
            .authorizeRequests()
                // TODO Get 요청 허용
                .mvcMatchers(HttpMethod.GET, "/api/**")
                    .anonymous()
                // TODO 다른 요청
                .anyRequest()
                    // TODO 인증 필요로 함.
                    .authenticated()
                .and()
            // TODO accessDeniedHandler(접근 권한이 없는 경우)
            .exceptionHandling()
                // TODO OAuth2AccessDeniedHandler 사용하기
                .accessDeniedHandler(new OAuth2AccessDeniedHandler());
    }
}
