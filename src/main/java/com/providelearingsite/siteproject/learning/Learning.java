package com.providelearingsite.siteproject.learning;

import com.providelearingsite.siteproject.account.Account;
import com.providelearingsite.siteproject.video.Video;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
public class Learning {

    @Id @Column(name = "learning_id")
    @GeneratedValue
    private Long id;
    @NotNull
    private String title;
    @NotNull
    private String subscription;

    private LocalDateTime createLearning;
    private LocalDateTime openLearning;
    private LocalDateTime closeLearning;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToMany(mappedBy = "learning")
    private List<Video> videos = new ArrayList<>();

    public void setVideos(Video video) {
        this.videos.add(video);
        if(video.getLearning() != this){
            video.setLearning(this);
        }
    }

    public void setAccount(Account account) {
        this.account = account;
        if(!account.getLearningSet().contains(this)){
            account.getLearningSet().add(this);
        }
    }
}
