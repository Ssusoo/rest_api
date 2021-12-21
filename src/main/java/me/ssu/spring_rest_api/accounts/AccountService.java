package me.ssu.spring_rest_api.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements UserDetailsService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    // TODO 시큐리티 폼 설정(패스워드 매칭)
    public Account saveAccount(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));

        return accountRepository.save(account);
    }


    // TODO 유저네임 조회(DB에서 유저 정보 불러오기)-1
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO NotFound Error
        Account account = accountRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        // TODO 유저네임 조회(UserDetails Type 변환)-2
        // TODO 현재 사용자-1
        // TODO @AuthenticationPrincipal User user를 받아오는 객체를 바꿔야 함.
        // TODO 우리가 만든 Account를 만든 걸 알고 있는 객체로 바꿔야함. AccountAdapter Class 생성
        // TODO User를 상속받은 AccountAdapter이기 때문에 결과적으로 AccountAdapter를 리턴해도 됨.
        // TODO EventController에서는 결국 AccountAdapter를 받을 수 있음.
        return new AccountAdapter(account);
    }

    // TODO 유저네임 조회(Roles 목록을 stream으로 map을 써서 맵핑하기)-3
    // TODO 현재 사용자-2
    // TODO AccountAdapter로 구현을 했기 때문에 지워 줌.
//    private Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
//        // TODO collect해서 role을 SimpleGrantedAuthority 변환
//        return roles.stream().map(r -> {
//            return new SimpleGrantedAuthority("ROLE_" + r.name());
//        }).collect(Collectors.toSet());
//    }
}