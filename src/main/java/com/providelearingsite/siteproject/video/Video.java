package com.providelearingsite.siteproject.video;

import com.providelearingsite.siteproject.learning.Learning;
import com.providelearingsite.siteproject.tag.Tag;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
    private String banner_bytes;
    private String bannerPath;
    @ManyToOne
    @JoinColumn(name = "learning_id")
    private Learning learning;
    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    public void setLearning(Learning learning) {
        this.learning = learning;
        if (!learning.getVideos().contains(this)) {
            learning.getVideos().add(this);
        }
    }
}
