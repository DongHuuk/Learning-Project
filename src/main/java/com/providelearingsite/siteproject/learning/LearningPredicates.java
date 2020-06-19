package com.providelearingsite.siteproject.learning;

import com.providelearingsite.siteproject.tag.Tag;
import com.querydsl.core.types.Predicate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LearningPredicates {

    public static Predicate findTop4ByTags(Set<Tag> tags){
        QLearning learning = QLearning.learning;
        List<String> tagList = tags.stream().map(Tag::getTitle).collect(Collectors.toList());

        if (tagList.isEmpty()) {
            return null;
        }

        return learning.tags.any().in(tags)
                .or(learning.title.containsIgnoreCase(tagList.get(0)))
                .or(learning.title.containsIgnoreCase(tagList.get(tagList.size()-1)));
    }

}
