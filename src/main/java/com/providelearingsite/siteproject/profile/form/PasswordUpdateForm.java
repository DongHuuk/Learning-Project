package com.providelearingsite.siteproject.profile.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PasswordUpdateForm {

    @NotBlank
//    @Length(min = 10, max = 50)
    private String nowPassword;
    @NotBlank
//    @Length(min = 10, max = 50)
    private String newPassword;
    //    @Length(min = 10, max = 50)
    @NotBlank
    private String newPasswordCheck;

    @NotBlank
    private String accountNickname;

    public boolean checkPassword() {
        return newPassword.equals(newPasswordCheck);
    }
}
