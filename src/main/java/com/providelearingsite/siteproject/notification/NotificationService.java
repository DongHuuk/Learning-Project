package com.providelearingsite.siteproject.notification;

import com.providelearingsite.siteproject.account.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationService {

    @Autowired private NotificationRepository notificationRepository;

    public void readNotifications(List<Notification> notCheckedNotifications) {
        for (Notification notification : notCheckedNotifications) {
            notification.setChecked(true);
        }
    }

    public void deleteNotifications(Account account) {
        notificationRepository.deleteByAccountAndChecked(account, true);
    }
}
