package com.providelearingsite.siteproject.learning;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Tag {

    @GeneratedValue
    @Id
    private Long id;

    private String title;

}
