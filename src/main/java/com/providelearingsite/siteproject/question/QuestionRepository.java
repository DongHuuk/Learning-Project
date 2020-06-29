package com.providelearingsite.siteproject.question;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface QuestionRepository extends JpaRepository<Question, Long> {

}
