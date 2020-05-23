package com.providelearingsite.siteproject.learning.validator;

import com.providelearingsite.siteproject.learning.VideoRepository;
import com.providelearingsite.siteproject.learning.form.VideoForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class VideoValidator implements Validator {

    @Autowired private VideoRepository videoRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(VideoForm.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        VideoForm videoForm = (VideoForm) o;
        if(videoRepository.existsByTitle(videoForm.getTitle())){
            errors.rejectValue("title", "wrong.title", "같은 제목의 강의가 존재합니다.");
        }
    }
}
