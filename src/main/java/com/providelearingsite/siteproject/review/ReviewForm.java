package com.providelearingsite.siteproject.review;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ReviewForm {

    @NotNull
    private String description;
    @NotNull
    private float rating;

}
