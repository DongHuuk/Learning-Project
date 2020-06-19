package com.providelearingsite.siteproject.video;

import com.providelearingsite.siteproject.learning.Learning;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface VideoRepositoryExtension {

    List<Video> findByTitleAndLearning(String title, Learning learning);

}
