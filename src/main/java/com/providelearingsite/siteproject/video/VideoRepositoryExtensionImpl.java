package com.providelearingsite.siteproject.video;

import com.providelearingsite.siteproject.learning.Learning;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class VideoRepositoryExtensionImpl extends QuerydslRepositorySupport implements VideoRepositoryExtension {

    public VideoRepositoryExtensionImpl() {
        super(Video.class);
    }

    @Override
    public List<Video> findByTitleAndLearning(String title, Learning learning) {
        QVideo video = QVideo.video;

        JPQLQuery<Video> query = from(video).where(video.videoTitle.containsIgnoreCase(title)
                .and(video.learning.eq(learning)))
                .distinct();

        return query.fetch();
    }
}
