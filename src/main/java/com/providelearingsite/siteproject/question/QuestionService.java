package com.providelearingsite.siteproject.question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class QuestionService {

    @Autowired private QuestionRepository questionRepository;

    public Question updateQuestion(QuestionForm questionForm, Long id) {
        Question question = questionRepository.findById(id).orElseThrow();
        question.setS_answer(questionForm.getS_answer());

        return question;
    }

    public void deleteQuestion(Question question) {
        questionRepository.deleteById(question.getId());
    }
}
