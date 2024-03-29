package me.ssu.springrestapi.configs;

import me.ssu.springrestapi.accounts.AccountService;
import me.ssu.springrestapi.common.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * 인증 서버(토큰 발행 / 토큰 인증)
 *  리소스 서버보다 우선 순위가 높다.
 */
@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    PasswordEncoder passwordEncoder;

    // TODO 리프레쉬 토큰 호출시 에러 처리
    @Autowired
    AuthenticationManager authenticationManager;

    // TODO 리프레쉬 토큰 호출시 에러 처리
    @Autowired
    AccountService accountService;

    // TODO 리프레쉬 토큰 호출시 에러 처리
    @Autowired
    TokenStore tokenStore;

    // TODO 문자열 외부설정으로 빼내기(OAuth)
    @Autowired
    AppProperties appProperties;

    // TODO 패스워드 인코더 설정
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.passwordEncoder(passwordEncoder);
    }

    // TODO 클라이언트 설정
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                // TODO 문자열 외부설정으로 빼내기(OAuth, clientId)
                .withClient(appProperties.getClientId())
                // TODO 액세스 토큰 발급 가능한 인증 타입
                .authorizedGrantTypes("password", "refresh_token")
                // TODO 해당 API로 접근했을 때 접근 범위 제한시키는 속성
                .scopes("read", "write")
                // TODO 문자열 외부설정으로 빼내기(OAuth, clientSecret)
                .secret(passwordEncoder.encode(appProperties.getClientSecret()))
                // TODO 클라이언트로 발급된 액세스 토큰 시간(10분)
                .accessTokenValiditySeconds(10*60)
                // TODO 클라이언트로 발급된 리프 러시 토큰 시간(60분)
                .refreshTokenValiditySeconds(6*10*60)
        ;
    }

    // TODO 리프레쉬 토큰 호출시 에러 처리
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(accountService)
                .tokenStore(tokenStore);
    }
}