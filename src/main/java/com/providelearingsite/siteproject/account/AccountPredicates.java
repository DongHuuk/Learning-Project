package com.providelearingsite.siteproject.account;

import com.providelearingsite.siteproject.tag.Tag;
import com.querydsl.core.types.Predicate;

import java.util.Set;

public class AccountPredicates {

    public static Predicate findByTags(Set<Tag> tags) {
        QAccount account = QAccount.account;
        return account.tags.any().in(tags);
    }

}
