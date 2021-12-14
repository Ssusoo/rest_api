package me.ssu.spring_rest_api.config;

import me.ssu.spring_rest_api.accounts.Account;
import me.ssu.spring_rest_api.accounts.AccountRole;
import me.ssu.spring_rest_api.accounts.AccountService;
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

            @Override
            public void run(ApplicationArguments args) throws Exception {

                Account ssu = Account.builder()
                        .email("zzanggoon8@email.com")
                        .password("1234")
                        .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                        .build();

                accountService.saveAccount(ssu);
            }
        };
    }
}
