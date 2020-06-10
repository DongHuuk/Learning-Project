package com.providelearingsite.siteproject.learning.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LearningForm {

    @NotBlank
    @NotNull
    @Length(max = 255)
    private String title;

    @NotBlank @NotNull
    private String subscription;

    @NotNull @NotBlank
    private String lecturerName;

    @NotNull @NotBlank
    private String lecturerDescription;

    @NotNull
    private int price;
}
