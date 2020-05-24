package com.providelearingsite.siteproject.learning;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
public class Video {

    @GeneratedValue
    @Id
    private Long id;
    //video info
    private Long videoSize;
    private String videoServerPath;
    private String videoTitle;
    private LocalDateTime saveTime;
    private String banner;

    @NotNull
    private String title;
    @NotNull
    private String subscription;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

}
