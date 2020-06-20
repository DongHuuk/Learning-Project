package com.providelearingsite.siteproject.review;

import com.providelearingsite.siteproject.account.Account;
import com.providelearingsite.siteproject.learning.Learning;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@EqualsAndHashCode(of = "id")
@Builder
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
public class Review {

    @Id @Column(name = "review_id")
    @GeneratedValue
    private Long id;

    private float rating;
    private String description;
    private LocalDateTime createTime;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    @ManyToOne
    @JoinColumn(name = "learning_id")
    private Learning learning;

}
