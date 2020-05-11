package com.providelearingsite.siteproject.account.form;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class AccountForm {

    @NotBlank
    @Length(min = 2, max = 20)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_!@#$%-]{3,20}$")
    private String nickname;

    @NotBlank
    @Email
    private String email;

    @NotBlank
//    @Length(min = 10, max = 50)
    private String password;

    @NotBlank
//    @Length(min = 10, max = 50)
    private String passwordcheck;

    public boolean checkingPassword() {
        return this.password.equals(passwordcheck);
    }

}
