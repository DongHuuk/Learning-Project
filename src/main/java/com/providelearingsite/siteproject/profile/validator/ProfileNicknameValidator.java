package com.providelearingsite.siteproject.profile.validator;

import com.providelearingsite.siteproject.account.Account;
import com.providelearingsite.siteproject.account.AccountRepository;
import com.providelearingsite.siteproject.profile.form.AccountUpdateForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ProfileNicknameValidator implements Validator {

    @Autowired private AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(AccountUpdateForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AccountUpdateForm accountUpdateForm = (AccountUpdateForm) target;

        if (accountRepository.existsByNickname(accountUpdateForm.getNickname())) {
            errors.rejectValue("nickname", "wrong.nickname", "닉네임을 사용할 수 없습니다.");
        }
    }
}
