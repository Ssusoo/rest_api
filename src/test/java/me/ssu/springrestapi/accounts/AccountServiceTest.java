package me.ssu.springrestapi.accounts;

import me.ssu.springrestapi.common.BaseTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Set;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class AccountServiceTest extends BaseTest {

    // TODO 사용자 조회
    @Test
    @DisplayName("사용자 조회")
    void findByUsername() throws Exception {
        // TODO Given, 이벤트 생성
        String username = "zzanggoon8@mail.com";
        String password = "1234";
        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        // TODO 시큐리티 기본 설정
//        accountRepository.save(account);
        // TODO 시큐리티 폼 설정
        accountService.saveAccount(account);

        // TODO When
        UserDetailsService userDetailsService = accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // TODO Then, 시큐리티 기본 설정
//        assertThat(userDetails.getPassword()).isEqualTo(password);
        // TODO Then, 시큐리티 폼 설정
        Assertions.assertThat(passwordEncoder.matches(password, userDetails.getPassword())).isTrue();
    }
}