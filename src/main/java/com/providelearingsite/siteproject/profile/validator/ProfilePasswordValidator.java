package com.providelearingsite.siteproject.profile.validator;

import com.providelearingsite.siteproject.account.Account;
import com.providelearingsite.siteproject.account.AccountRepository;
import com.providelearingsite.siteproject.profile.form.PasswordUpdateForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ProfilePasswordValidator implements Validator {

    @Autowired private AccountRepository accountRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(PasswordUpdateForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordUpdateForm form = (PasswordUpdateForm) target;
        Account account = accountRepository.findByNicknameAndTokenChecked(form.getAccountNickname(), true);

        if (!passwordEncoder.matches(form.getNowPassword(), account.getPassword())) {
            errors.rejectValue("nowPassword", "wrong.nowPassword", "패스워드가 일치하지 않습니다.");
        }

        if (!form.checkPassword()) {
            errors.rejectValue("newPassword", "wrong.newPassword", "패스워드가 일치하지 않습니다.");
        }
    }
}
