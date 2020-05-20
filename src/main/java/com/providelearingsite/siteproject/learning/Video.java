package com.providelearingsite.siteproject.learning;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
    @NotNull
    private String address;
    @NotNull
    private String Title;
    @NotNull
    private String subscription;
    @Nullable
    private String banner;
    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

}
