package com.providelearingsite.siteproject.learning;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class LearningRepositoryExtensionImpl extends QuerydslRepositorySupport implements LearningRepositoryExtension{

    public LearningRepositoryExtensionImpl() {
        super(Learning.class);
    }

    @Override
    public Page<Learning> findByKeywordWithPageable(String keyword, Pageable pageable) {
        QLearning learning = QLearning.learning;
        JPQLQuery<Learning> query = from(learning).where(learning.startingLearning.isTrue()
                .and(learning.title.containsIgnoreCase(keyword))
                .or(learning.tags.any().title.containsIgnoreCase(keyword)))
                .distinct();
        JPQLQuery<Learning> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Learning> pageableQueryResult = pageableQuery.fetchResults();

        return new PageImpl<>(pageableQueryResult.getResults(), pageable, pageableQueryResult.getTotal());
    }

    @Override
    public Page<Learning> findAllWithPageable(Boolean tf, Pageable pageable) {
        //.distinct() 중복값 제거

        QLearning learning = QLearning.learning;
        JPQLQuery<Learning> query = from(learning).where(learning.startingLearning.isTrue());
        JPQLQuery<Learning> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Learning> pageableQueryResult = pageableQuery.fetchResults();

        return new PageImpl<>(pageableQueryResult.getResults(), pageable, pageableQueryResult.getTotal());
    }

    @Override
    public Page<Learning> findByKategorieWithPageable(boolean b, String kategorie, Pageable pageable) {
        int index = kategorie.equalsIgnoreCase("web") ? 1 : 2;
        QLearning learning = QLearning.learning;
        JPQLQuery<Learning> query = from(learning).where(learning.startingLearning.isTrue()
                .and(learning.kategorie.eq(index+"")));
        JPQLQuery<Learning> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Learning> pageableQueryResult = pageableQuery.fetchResults();

        return new PageImpl<>(pageableQueryResult.getResults(), pageable, pageableQueryResult.getTotal());
    }

    @Override
    public Page<Learning> findByKategorieAndKeywordWithPageable(boolean b, String keyword, String kategorie, Pageable pageable) {
        int index = kategorie.equalsIgnoreCase("web") ? 1 : 2;
        String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z]";
        keyword = keyword.replaceAll(match, "");
        QLearning learning = QLearning.learning;
        JPQLQuery<Learning> query = from(learning).where(learning.startingLearning.isTrue()
                .and(learning.kategorie.eq(index + ""))
                .and(learning.title.containsIgnoreCase(keyword))
                .or(learning.tags.any().title.containsIgnoreCase(keyword)))
                .distinct();
        JPQLQuery<Learning> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Learning> pageableQueryResult = pageableQuery.fetchResults();

        return new PageImpl<>(pageableQueryResult.getResults(), pageable, pageableQueryResult.getTotal());
    }


}
