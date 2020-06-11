package com.providelearingsite.siteproject.learning.validator;

import com.providelearingsite.siteproject.learning.Learning;
import com.providelearingsite.siteproject.learning.LearningRepository;
import com.providelearingsite.siteproject.learning.form.LearningForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class LearningValidator implements Validator {

    @Autowired private LearningRepository learningRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(LearningForm.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        LearningForm learningForm = (LearningForm) o;
        if(learningForm.getTitle() == null || learningForm.getTitle().isEmpty()){
            errors.rejectValue("title", "wrong.title", "값을 입력해주세요.");
        }
        if(learningForm.getLecturerName() == null || learningForm.getLecturerName().isEmpty()){
            errors.rejectValue("lecturerName", "wrong.lecturerName", "이름 미입력");
        }
        if(learningForm.getSubscription() == null || learningForm.getSubscription().isEmpty()){
            errors.rejectValue("subscription", "wrong.subscription", "설명 미입력");
        }
        if(learningForm.getLecturerDescription() == null || learningForm.getLecturerDescription().isEmpty()){
            errors.rejectValue("lecturerDescription", "wrong.lecturerDescription", "설명 미입력");
        }
        if (learningForm.getKategorie() == null || learningForm.getKategorie().isEmpty()) {
            errors.rejectValue("kategorie", "wrong.kategorie", "카테고리 미설정");
        }
    }
}
