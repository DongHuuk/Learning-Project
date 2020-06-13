package com.providelearingsite.siteproject.notification;

import com.providelearingsite.siteproject.account.Account;
import com.providelearingsite.siteproject.account.AccountPredicates;
import com.providelearingsite.siteproject.account.AccountRepository;
import com.providelearingsite.siteproject.config.AppProperties;
import com.providelearingsite.siteproject.learning.Learning;
import com.providelearingsite.siteproject.learning.LearningRepository;
import com.providelearingsite.siteproject.learning.event.LearningClosedEvent;
import com.providelearingsite.siteproject.learning.event.LearningCreateEvent;
import com.providelearingsite.siteproject.learning.event.LearningUpdateEvent;
import com.providelearingsite.siteproject.mail.EmailMessage;
import com.providelearingsite.siteproject.mail.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Optional;


@Component
@Async
public class NotificationLearningEventListener {

    @Autowired private AccountRepository accountRepository;
    @Autowired private LearningRepository learningRepository;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private EmailService emailService;
    @Autowired private TemplateEngine templateEngine;
    @Autowired private AppProperties appProperties;

    @EventListener
    public void learningCreateListener(LearningCreateEvent event) {
        Optional<Learning> learning = learningRepository.findById(event.getLearning().getId());
        Learning newLearning = learning.orElseThrow();
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByTags(newLearning.getTags()));
        accounts.forEach(account -> {
            if (account.isSiteMailNotification()) {
                sendEmail(account, "강의가 새롭게 생성되었습니다.", "/learning/" + newLearning.getId(), newLearning.getTitle(), "강의 생성 알림");
            }
            if (account.isSiteWebNotification()) {
                createNotification(newLearning, account, "강의가 새롭게 생성되었습니다.", NotificationType.CREATE);
            }
        });
    }

    @EventListener
    public void learningClosedListener(LearningClosedEvent event) {
        Optional<Learning> learning = learningRepository.findById(event.getLearning().getId());
        Learning newLearning = learning.orElseThrow();
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByTags(newLearning.getTags()));
        accounts.forEach(account -> {
            if (account.isSiteMailNotification()) {
                sendEmail(account, "강의가 종료되었습니다.", "/learning/" + newLearning.getId(), newLearning.getTitle(), "강의 종료 알림");
            }
            if (account.isSiteWebNotification()) {
                createNotification(newLearning, account, "강의가 종료되었습니다.", NotificationType.CLOSED);
            }
        });
    }

    @EventListener
    public void learningUpdateListener(LearningUpdateEvent event) {
        Optional<Learning> learning = learningRepository.findById(event.getLearning().getId());
        Learning newLearning = learning.orElseThrow();
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByTags(newLearning.getTags()));
        accounts.forEach(account -> {
            if (account.isSiteMailNotification()) {
                sendEmail(account, "강의 내용이 갱신되었습니다.", "/learning/" + newLearning.getId(), newLearning.getTitle(), "강의 변경 알림");
            }
            if (account.isSiteWebNotification()) {
                createNotification(newLearning, account, "강의 내용이 갱신되었습니다.", NotificationType.UPDATE);
            }
        });
    }

    private void sendEmail(Account account, String message, String url, String learningTitle, String subject) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("message", message);
        context.setVariable("host", appProperties.getHost());
        context.setVariable("link", url);
        context.setVariable("linkName", learningTitle);

        String process = templateEngine.process("mail/notificationmail", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getEmail())
                .subject(subject)
                .message(process)
                .build();

        emailService.sendEmail(emailMessage);
    }

    private void createNotification(Learning newLearning, Account account, String message, NotificationType type) {
        Notification notification = new Notification();
        notification.setTitle(newLearning.getTitle() + " " + message);
        notification.setLectureName(newLearning.getLecturerName());
        notification.setDescription(newLearning.getSimplesubscription());
        notification.setAccount(account);
        notification.setChecked(false);
        notification.setCreateNotification(LocalDateTime.now());
        notification.setNotificationType(type);
        notificationRepository.save(notification);
    }
}
