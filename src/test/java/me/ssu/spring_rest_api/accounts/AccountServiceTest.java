package me.ssu.spring_rest_api.accounts;

import me.ssu.spring_rest_api.common.BaseControllerTest;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
class AccountServiceTest extends BaseControllerTest {

    // TODO 회원 정보 조회
    @Test
    @DisplayName("회원 정보 조회")
    void findByUsername() {
        // TODO 이벤트 생성
        String username = "zzanggoon8@email.com";
        String password = "1234";
        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        // TODO 시큐리티 기본 설정
        accountRepository.save(account);

        // TODO When, 유저네임 조회
        UserDetailsService userDetailsService = accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // TODO Then, 시큐리티 기본 설정
        assertThat(userDetails.getPassword()).isEqualTo(password);
    }

    // TODO 예외 테스트
    @Test
    @DisplayName("유저 네임을 실패한 경우")
    void findByUsernameFail() {
        // TODO When
        String username = "random@mail.com";
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            accountService.loadUserByUsername(username);
        });

        // TODO Then
        AssertionsForClassTypes.assertThat(exception.getMessage()).contains(username);
    }
}