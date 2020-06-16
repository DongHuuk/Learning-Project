package com.providelearingsite.siteproject.learning;

import com.providelearingsite.siteproject.account.Account;
import com.querydsl.core.types.Predicate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional
public interface LearningRepository extends JpaRepository<Learning, Long>, QuerydslPredicateExecutor<Learning>, LearningRepositoryExtension {
    Learning findByTitle(String title);

    List<Learning> findTop4ByStartingLearningOrderByCreateLearningDesc(boolean b);

    List<Learning> findTop4ByStartingLearningOrderByRatingDesc(boolean b);

    List<Learning> findAllByAccountOrderByCreateLearningDesc(Account account);
}
