package com.providelearingsite.siteproject.main;

import com.providelearingsite.siteproject.learning.Learning;
import com.providelearingsite.siteproject.learning.LearningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MainService {

    @Autowired private LearningRepository learningRepository;


    public List<Learning> learningOrderByCreateLearning() {
        return learningRepository.findTop4ByStartingLearningOrderByCreateLearningDesc(true);
    }

    public List<Learning> learningOrderByRating() {
        return learningRepository.findTop4ByStartingLearningOrderByRatingDesc(true);
    }
}
