package com.providelearingsite.siteproject.account;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long>, QuerydslPredicateExecutor<Account> {

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    Account findByEmailAndTokenChecked(String email, boolean check);
    Account findByNicknameAndTokenChecked(String nickname, boolean check);


    List<Account> findAllByListenLearningId(Long id);

    @EntityGraph(attributePaths = {"learnings"}, type = EntityGraph.EntityGraphType.LOAD)
    Account findAccountWithLearningsById(Long id);

    @EntityGraph(attributePaths = {"questions"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Account> findAccountWithQuestionById(Long id);

    @EntityGraph(attributePaths = {"tags"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Account> findAccountWithTagsById(Long id);

    @EntityGraph(attributePaths = {"learnings", "questions", "listenLearning"}, type = EntityGraph.EntityGraphType.LOAD)
    Account findAccountWithLearningsAndQuestionsAndListenLearningAndTagsById(Long id);
}
