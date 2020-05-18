package com.providelearingsite.siteproject.profile.form;

import lombok.Data;

@Data
public class NotificationUpdateForm {
    private boolean siteMailNotification;
    private boolean siteWebNotification;
    private boolean learningMailNotification;
    private boolean learningWebNotification;
}
