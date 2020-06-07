package com.providelearingsite.siteproject.learning;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface LearningRepository extends JpaRepository<Learning, Long> {
    Learning findByTitle(String title);
}
