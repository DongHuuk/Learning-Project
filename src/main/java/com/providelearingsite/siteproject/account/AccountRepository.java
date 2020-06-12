package com.providelearingsite.siteproject.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long>, QuerydslPredicateExecutor<Account> {

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    Account findByEmailAndTokenChecked(String email, boolean check);
    Account findByNicknameAndTokenChecked(String nickname, boolean check);


    List<Account> findAllByListenLearningId(Long id);
}
