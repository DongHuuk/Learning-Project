package com.providelearingsite.siteproject.video;

import com.providelearingsite.siteproject.learning.Learning;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
public class Video {

    @GeneratedValue
    @Id @Column(name = "video_id")
    private Long id;

    private Long videoSize;
    private String videoServerPath;
    private String videoTitle;
    private LocalDateTime saveTime;
    @Lob
    private String bannerBytes;
    private String bannerServerPath;

    @ManyToOne
    @JoinColumn(name = "learning_id")
    private Learning learning;

    public void setLearning(Learning learning) {
        this.learning = learning;
        if (!learning.getVideos().contains(this)) {
            learning.getVideos().add(this);
        }
    }
}
