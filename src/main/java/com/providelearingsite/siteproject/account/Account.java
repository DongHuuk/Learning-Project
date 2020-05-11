package com.providelearingsite.siteproject.account;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@AllArgsConstructor @NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    private String password;

    @Column(unique = true)
    private String nickname;
    @Column(unique = true)
    private String email;

    @Lob
    private String profileImage;

    private String description;

    private LocalDateTime createAccount;
    private LocalDateTime createEmailToken;

    private String emailCheckToken;

    private boolean tokenChecked = false;

    public void createEmailCheckToken(){
        this.emailCheckToken = UUID.randomUUID().toString();
        this.createEmailToken = LocalDateTime.now();
    }

    public Boolean canCheckingEmailToken() {
        return this.createEmailToken.isBefore(LocalDateTime.now().minusHours(1));
    }


}
