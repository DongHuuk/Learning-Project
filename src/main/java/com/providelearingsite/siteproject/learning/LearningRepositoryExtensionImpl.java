package com.providelearingsite.siteproject.learning;

import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class LearningRepositoryExtensionImpl extends QuerydslRepositorySupport implements LearningRepositoryExtension{

    public LearningRepositoryExtensionImpl() {
        super(Learning.class);
    }

    @Override
    public List<Learning> findByKeyword(String keyword) {
        QLearning learning = QLearning.learning;
        JPQLQuery<Learning> query = from(learning).where(learning.startingLearning.isTrue()
                .and(learning.title.containsIgnoreCase(keyword))
                .or(learning.tags.any().title.containsIgnoreCase(keyword)));
        return query.fetch();
    }
}
