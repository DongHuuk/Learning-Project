package com.providelearingsite.siteproject.learning.event;

import com.providelearingsite.siteproject.learning.Learning;
import lombok.Getter;

@Getter
public class LearningCreateEvent {

    private Learning learning;

    public LearningCreateEvent(Learning learning) {
        this.learning = learning;
    }
}
