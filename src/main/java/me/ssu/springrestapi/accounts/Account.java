package me.ssu.springrestapi.accounts;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Builder
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Entity @EqualsAndHashCode(of = "id")
public class Account {

    @Id @GeneratedValue
    private Integer id;

    // TODO 계속해서 저장이 안 되도록
    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<AccountRole> roles;
}
