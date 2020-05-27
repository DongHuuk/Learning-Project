package com.providelearingsite.siteproject.learning;

import com.providelearingsite.siteproject.tag.Tag;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
@Builder
public class Learning {

    @Id
    @GeneratedValue
    private Long id;
    @Lob
    private String banner;
    @NotNull
    private String title;
    @NotNull
    private String subscription;
    @ManyToMany
    private Set<Tag> tags = new HashSet<>();
    @OneToMany
    private List<Video> videoList = new ArrayList<>();

}
