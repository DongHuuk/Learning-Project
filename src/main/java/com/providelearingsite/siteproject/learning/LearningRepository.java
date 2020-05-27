package com.providelearingsite.siteproject.learning;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LearningRepository extends JpaRepository<Learning, Long> {
}
