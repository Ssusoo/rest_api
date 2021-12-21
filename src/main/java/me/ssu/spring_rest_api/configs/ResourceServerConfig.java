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
// TODO 리소스 서버는 토큰 기반으로 인증 정보가 있는지 없는지 확인 유무 파악.
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    // TODO 리소스 설정(아이디 설정)
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("event");
    }

    // TODO 리소스 설정(Http)
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
                .permitAll()
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