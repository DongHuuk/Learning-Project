package com.providelearingsite.siteproject.learning;

import com.providelearingsite.siteproject.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface LearningRepository extends JpaRepository<Learning, Long>, QuerydslPredicateExecutor<Learning>, LearningRepositoryExtension {
    Learning findByTitle(String title);

    List<Learning> findAllByStartingLearning(boolean tf);
}
