package com.providelearingsite.siteproject.learning;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface LearningRepositoryExtension {

    List<Learning> findByKeyword(String keyword);

}
