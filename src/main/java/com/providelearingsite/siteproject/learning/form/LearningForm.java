package com.providelearingsite.siteproject.learning.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LearningForm {

    @NotBlank
    @Length(max = 255)
    @Column(unique = true, nullable = false)
    private String title;

    @NotBlank @NotNull
    private String subscription;

    @NotNull
    @Column(nullable = false)
    private String lecturerName;
}
