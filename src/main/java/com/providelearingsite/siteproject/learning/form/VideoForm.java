package com.providelearingsite.siteproject.learning.form;

import com.providelearingsite.siteproject.learning.Tag;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Data
public class VideoForm {

//    private File file;
    @NotBlank
    private String title;
    private String subscription;
    private String banner;
    private Set<Tag> tags = new HashSet<>();
}
