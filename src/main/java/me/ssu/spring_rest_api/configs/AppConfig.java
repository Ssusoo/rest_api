package me.ssu.spring_rest_api.configs;

import me.ssu.spring_rest_api.accounts.Account;
import me.ssu.spring_rest_api.accounts.AccountRole;
import me.ssu.spring_rest_api.accounts.AccountService;
import me.ssu.spring_rest_api.common.AppProperties;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class AppConfig {

    // TODO 입력값 제한하기(DTO)
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    // TODO 시큐리티 기본 설정
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // TODO 시큐리티 폼 설정
    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {
            @Autowired
            AccountService accountService;

            @Autowired
            AppProperties appProperties;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                // TODO 문자열 외부설정으로 빼내기(기본 유저 만들기(ADMIN))-1
                Account admin = Account.builder()
                        // TODO 문자열 외부설정으로 빼내기(프로퍼티 값 넣기)-3
                        .email(appProperties.getAdminUsername())
                        .password(appProperties.getAdminPassword())
//                        .email("admin@email.com")
//                        .password("admin")
                        .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                        .build();

                // TODO 패스워드 인코딩해서 매칭 시키기
                accountService.saveAccount(admin);

                // TODO 문자열 외부설정으로 빼내기(기본 유저 만들기(USER))-2
                Account user = Account.builder()
                        // TODO 문자열 외부설정으로 빼내기(프로퍼티 값 넣기)-3
                        .email(appProperties.getUserUsername())
                        .password(appProperties.getUserPassword())
//                        .email("user@email.com")
//                        .password("user")
                        .roles(Set.of(AccountRole.USER))
                        .build();

                // TODO 패스워드 인코딩해서 매칭 시키기
                accountService.saveAccount(user);
            }
        };
    }
}
