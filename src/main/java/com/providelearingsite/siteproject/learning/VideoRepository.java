package com.providelearingsite.siteproject.learning;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface VideoRepository extends JpaRepository<Video, Long> {
    boolean existsByTitle(String title);
}
