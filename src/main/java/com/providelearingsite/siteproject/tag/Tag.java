package com.providelearingsite.siteproject.tag;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@EqualsAndHashCode(of = "id")
@Builder
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Tag {

    @GeneratedValue
    @Id @Column(name = "tag_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String title;

}
