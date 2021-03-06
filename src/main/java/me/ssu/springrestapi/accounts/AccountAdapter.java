package me.ssu.springrestapi.accounts;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class AccountAdapter extends User {

    private Account account;

    // TODO Error Property or field 'account' cannot be found on object of type
    public Account getAccount() {
        return account;
    }

    public AccountAdapter(Account account) {
        // TODO 부모 new User(account.getEmail(), account.getPassword(), authorities(account.getRoles()))
        super(account.getEmail(), account.getPassword(), authorities(account.getRoles()));
        this.account = account;
    }

    private static Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
        return roles.stream().map(r -> {
           return new SimpleGrantedAuthority("ROLE_" + r.name());
        }).collect(Collectors.toSet());
    }
}
