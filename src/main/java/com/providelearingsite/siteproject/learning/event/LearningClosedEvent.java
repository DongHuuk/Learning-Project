package com.providelearingsite.siteproject.learning.event;

import com.providelearingsite.siteproject.learning.Learning;
import lombok.Getter;

@Getter
public class LearningClosedEvent {

    private Learning learning;

    public LearningClosedEvent(Learning learning) {
        this.learning = learning;
    }
}
