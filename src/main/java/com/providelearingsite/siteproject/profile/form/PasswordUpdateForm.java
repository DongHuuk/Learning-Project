package com.providelearingsite.siteproject.profile.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class PasswordUpdateForm {

    @NotBlank
    @Length(min = 10, max = 50, message = "패스워드 길이가 알맞지 않습니다. 다시 입력해주세요")
    private String nowPassword;
    @NotBlank
    @Length(min = 10, max = 50, message = "패스워드 길이가 알맞지 않습니다. 다시 입력해주세요")
    private String newPassword;
    @Length(min = 10, max = 50, message = "패스워드 길이가 알맞지 않습니다. 다시 입력해주세요")
    @NotBlank
    private String newPasswordCheck;

    @NotBlank
    private String accountNickname;

    public boolean checkPassword() {
        return newPassword.equals(newPasswordCheck);
    }
}
