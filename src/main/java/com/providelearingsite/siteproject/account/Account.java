package com.providelearingsite.siteproject.account;

import com.providelearingsite.siteproject.learning.Learning;
import com.providelearingsite.siteproject.tag.Tag;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@AllArgsConstructor @NoArgsConstructor
public class Account {

    @Id @Column(name = "account_id")
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

    //notifications
    private boolean siteMailNotification;
    private boolean siteWebNotification;
    private boolean learningMailNotification;
    private boolean learningWebNotification;

    //uploader
    private boolean uploader = true;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
    private Set<Learning> learningSet = new HashSet<>();

    public void setLearningSet(Learning learning) {
        this.learningSet.add(learning);
        if(learning.getAccount() != this){
            learning.setAccount(this);
        }
    }

    public void createEmailCheckToken(){
        this.emailCheckToken = UUID.randomUUID().toString();
        this.createEmailToken = LocalDateTime.now();
    }

    public Boolean canCheckingEmailToken() {
        return this.createEmailToken.isBefore(LocalDateTime.now().minusHours(1));
    }
    public boolean canUploadVideo(){
        return !this.learningSet.isEmpty();
    }

}
