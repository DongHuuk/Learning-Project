package com.providelearingsite.siteproject.learning.event;

import com.providelearingsite.siteproject.learning.Learning;
import lombok.Getter;

@Getter
public class LearningUpdateEvent {
    private Learning learning;

    public LearningUpdateEvent(Learning learning) {
        this.learning = learning;
    }
}
