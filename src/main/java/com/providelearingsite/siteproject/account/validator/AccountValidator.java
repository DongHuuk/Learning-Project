package com.providelearingsite.siteproject.account.validator;

import com.providelearingsite.siteproject.account.AccountRepository;
import com.providelearingsite.siteproject.account.form.AccountForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class AccountValidator implements Validator {

    @Autowired private AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(AccountForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AccountForm accountForm = (AccountForm) target;

        if(accountRepository.existsByEmail(accountForm.getEmail())){
            errors.rejectValue("email", "wrong.email", new Object[]{accountForm.getEmail()},"이 이메일은 사용하실 수 없습니다.");
        }

        if(accountRepository.existsByNickname(accountForm.getNickname())){
            errors.rejectValue("nickname", "wrong.nickname", new Object[]{accountForm.getNickname()}, "이 닉네임은 사용하실 수 없습니다.");
        }

        if (!accountForm.checkingPassword()) {
            errors.rejectValue("password", "wrong.password", new Object[]{accountForm.getPassword()}, "패스워드가 일치하지 않습니다.");
        }

    }
}
