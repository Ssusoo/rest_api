package me.ssu.spring_rest_api.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccountService implements UserDetailsService {

    @Autowired
    AccountRepository accountRepository;
    
    // TODO 유저네임 조회(DB에서 유저 정보 불러오기)-1
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO NotFound Error
        Account account = accountRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        // TODO 유저네임 조회(UserDetails Type 변환)-2
        return new User(account.getEmail(), account.getPassword(), authorities(account.getRoles()));
    }

    // TODO 유저네임 조회(Roles 목록을 stream으로 map을 써서 맵핑하기)-3
    private Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
        // TODO collect해서 role을 SimpleGrantedAuthority 변환
        return roles.stream().map(r -> {
            return new SimpleGrantedAuthority("ROLE_" + r.name());
        }).collect(Collectors.toSet());
    }
}