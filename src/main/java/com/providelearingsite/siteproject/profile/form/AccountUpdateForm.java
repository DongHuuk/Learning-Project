package com.providelearingsite.siteproject.profile.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class AccountUpdateForm {

    @NotBlank
    @Length(min = 2, max = 20)
    private String nickname;

    private String description;

}
