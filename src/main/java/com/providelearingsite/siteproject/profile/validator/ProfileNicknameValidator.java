package com.providelearingsite.siteproject.profile.validator;

import com.providelearingsite.siteproject.account.AccountRepository;
import com.providelearingsite.siteproject.profile.form.ProfileUpdateForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ProfileNicknameValidator implements Validator {

    @Autowired private AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(ProfileUpdateForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ProfileUpdateForm profileUpdateForm = (ProfileUpdateForm) target;

        if (accountRepository.existsByNickname(profileUpdateForm.getNickname())) {
            errors.rejectValue("nickname", "wrong.nickname", "닉네임을 사용할 수 없습니다.");
        }
        if (profileUpdateForm.getDescription() == null || profileUpdateForm.getDescription().isEmpty()) {
            errors.rejectValue("description", "wrong.description", "자기 소개를 입력해주세요.");
        }
    }
}
