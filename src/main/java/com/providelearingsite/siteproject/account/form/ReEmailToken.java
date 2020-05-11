package com.providelearingsite.siteproject.account.form;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class ReEmailToken {

    @NotBlank
    @Email
    private String email;

}
