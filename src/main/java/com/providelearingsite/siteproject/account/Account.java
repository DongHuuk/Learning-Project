package com.providelearingsite.siteproject.account;

import com.providelearingsite.siteproject.learning.Learning;
import com.providelearingsite.siteproject.notification.Notification;
import com.providelearingsite.siteproject.question.Question;
import com.providelearingsite.siteproject.review.Review;
import com.providelearingsite.siteproject.tag.Tag;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

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

    //cart List
    @ManyToMany
    private Set<Learning> cartList;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Tag> tags = new HashSet<>();

    //can listen Learning buy or free videos
    @ManyToMany(mappedBy = "accounts")
    private Set<Learning> listenLearning = new HashSet<>();

    @OneToMany(mappedBy = "account")
    private Set<Learning> learnings = new HashSet<>();

    @OneToMany(mappedBy = "account")
    private Set<Question> questions = new HashSet<>();

    @OneToMany(mappedBy = "account")
    private Set<Review> reviews = new HashSet<>();

    @OneToMany(mappedBy = "account")
    private Set<Notification> notifications = new HashSet<>();

    public void setLearnings(Learning learning) {
        this.learnings.add(learning);
        if(learning.getAccount() != this){
            learning.setAccount(this);
        }
    }

    public void setReviews(Review review) {
        this.reviews.add(review);
        if(review.getAccount() != this){
            review.setAccount(this);
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
        return !this.learnings.isEmpty();
    }

}
