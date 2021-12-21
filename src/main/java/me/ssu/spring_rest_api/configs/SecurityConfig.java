package me.ssu.spring_rest_api.configs;

import me.ssu.spring_rest_api.accounts.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // TODO 시큐리티 기본 설정(사용자 세부 서비스)-3
    @Autowired
    AccountService accountService;

    @Autowired
    PasswordEncoder passwordEncoder;

    // TODO 시큐리티 기본 설정(토큰 저장소)-1
    @Bean
    public TokenStore tokenStore() {
        // TODO InMemoryTokenStore
        //  기본 설정으로 사용하기 때문에 리붓되는 동시에 데이터가 초기화됨.
        return new InMemoryTokenStore();
    }

    // TODO 시큐리티 기본 설정(AuthenticationManager 빈 등록)-2
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // TODO 시큐리티 기본 설정(사용자 세부 서비스)-3
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountService)
                .passwordEncoder(passwordEncoder);
    }

    // TODO 시큐리티 기본 설정(Spring Security Filter 연결)-4
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/docs/index.html");
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}