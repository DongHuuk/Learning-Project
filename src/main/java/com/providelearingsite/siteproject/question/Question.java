package com.providelearingsite.siteproject.question;

import com.providelearingsite.siteproject.account.Account;
import com.providelearingsite.siteproject.learning.Learning;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@EqualsAndHashCode(of = "id")
@Builder
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Question {

    @Id @Column(name = "question_id")
    @GeneratedValue
    private Long id;
    private String s_name;
    private String s_title;
    @Lob
    private String s_description;
    @Lob
    private String s_answer;
    private LocalDateTime time_questionTime;
    private LocalDateTime time_answerTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "learning_id")
    private Learning learning;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

}
