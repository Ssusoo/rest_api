package me.ssu.spring_rest_api.accounts;

import me.ssu.spring_rest_api.common.BaseControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.assertj.core.api.Java6Assertions.assertThat;


@SpringBootTest
class AccountServiceTest extends BaseControllerTest {

    @Test
    void findByUsername() {
        // TODO Given
        String username = "zzanggoon@mail.com";
        String password = "1234";
        Account account = Account.builder()
                .email(username)
                .password(password)
                .build();

        accountRepository.save(account);

        // TODO When
        UserDetailsService userDetailsService = accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // TODO Then
        assertThat(userDetails.getPassword()).isEqualTo(password);
    }
}