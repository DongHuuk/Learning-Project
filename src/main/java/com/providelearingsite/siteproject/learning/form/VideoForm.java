package com.providelearingsite.siteproject.learning.form;

import com.providelearingsite.siteproject.learning.Tag;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Data
public class VideoForm {
    @NotBlank
    private String address;
    @NotBlank
    private String title;
    private String subscription;
    private String banner;
    @NotBlank
    private Set<Tag> tags = new HashSet<>();
}
