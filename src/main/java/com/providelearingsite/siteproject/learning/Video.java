package com.providelearingsite.siteproject.learning;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Video {

    @GeneratedValue
    @Id
    private Long id;
    private Long videoSize;
    private String videoServerPath;
    private String videoTitle;
    private LocalDateTime saveTime;

}
