package com.providelearingsite.siteproject.learning;

import com.providelearingsite.siteproject.account.Account;
import com.providelearingsite.siteproject.tag.Tag;
import com.providelearingsite.siteproject.video.Video;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private String lecturerName;
    @NotNull
    @Lob
    private String subscription;
    @Lob
    private String bannerBytes;
    private String bannerServerPath;
    private float rating = 0;

    private LocalDateTime createLearning;
    private LocalDateTime openLearning;
    private LocalDateTime closeLearning;

    private boolean startingLearning;
    private boolean closedLearning;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToMany(mappedBy = "learning", fetch = FetchType.LAZY)
    private Set<Video> videos = new HashSet<>();
    private int videoCount = 0;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();
    private LocalDateTime uploadVideo = null;
    private LocalDateTime updateLearning = null;

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

    public boolean checkUploadLearning(){
        return !(this.uploadVideo instanceof LocalDateTime) || this.uploadVideo == null;
    }
    public boolean checkUpdateLearning(){
        return !(this.updateLearning instanceof LocalDateTime) || this.updateLearning == null;
    }
}
