package com.providelearingsite.siteproject.notification;

import com.providelearingsite.siteproject.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    long countByAccountAndChecked(Account account, boolean check);
}
