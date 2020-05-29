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
        String formStr = learningForm.getTitle().trim();
        Learning repositoryByTitle = learningRepository.findByTitle(learningForm.getTitle());
        if(repositoryByTitle != null && formStr.equals(repositoryByTitle.getTitle().trim())){
            errors.rejectValue("title", "wrong.title", "같은 제목의 강의가 존재합니다.");
        }
    }
}
