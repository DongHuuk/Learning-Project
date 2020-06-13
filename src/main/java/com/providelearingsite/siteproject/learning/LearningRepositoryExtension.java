package com.providelearingsite.siteproject.learning;

import com.providelearingsite.siteproject.tag.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional
public interface LearningRepositoryExtension {

    Page<Learning> findByKeywordWithPageable(String keyword, Pageable pageable);

    Page<Learning> findAllWithPageable(Boolean tf, Pageable pageable);

    Page<Learning> findByKategorieWithPageable(boolean b, String kategorie, Pageable pageable);

    Page<Learning> findByKategorieAndKeywordWithPageable(boolean b, String keyword, String kategorie, Pageable pageable);

    List<Learning> findTop4ByTagsOrderByRatingDesc(Set<Tag> tags);
}
