package me.ssu.spring_rest_api.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccountService implements UserDetailsService {

    @Autowired
    AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO username이 없으면 UsernameNotFoundException 처리
        Account account = accountRepository.findByEmail(username)

                .orElseThrow(() -> new UsernameNotFoundException(username));

        // TODO User라는 Class를 이용해
        //  Spring Security가 이해할 수 있는 UserDetails Type으로 변환-1
        //  getRoles을 authroties으로 변환-2
        return new User(account.getEmail(), account.getPassword(), authorities(account.getRoles()));
    }

    private Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
        // TODO Roles 목록을 stream으로 map을 써서 맵핑하기
        //  collect해서 role을 SimpleGrantedAuthority 변환
        return roles.stream().map(r -> {
            return new SimpleGrantedAuthority("ROLE_" + r.name());
        }).collect(Collectors.toSet());
    }
}