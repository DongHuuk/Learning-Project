package com.providelearingsite.siteproject.learning;

import com.providelearingsite.siteproject.tag.Tag;
import com.querydsl.core.types.Predicate;

import java.util.Set;

public class LearningPredicates {

    public static Predicate findTop4ByTags(Set<Tag> tags){
        QLearning learning = QLearning.learning;
        return learning.tags.any().in(tags);
    }

}
