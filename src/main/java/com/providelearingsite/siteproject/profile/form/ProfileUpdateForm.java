package com.providelearingsite.siteproject.profile.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class ProfileUpdateForm {


    @Length(max = 20)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_!@#$%-]{0,20}$")
    private String nickname;

    private String description;


}
