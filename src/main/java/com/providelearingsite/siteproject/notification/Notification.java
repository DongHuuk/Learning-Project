package com.providelearingsite.siteproject.notification;

import com.providelearingsite.siteproject.account.Account;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@AllArgsConstructor @NoArgsConstructor
public class Notification {

    @Id @GeneratedValue
    private Long id;

    private String title;

    private String lectureName;

    private int price;

    private String description;

    private boolean checked = false;

    @ManyToOne
    private Account account;

    private LocalDateTime createNotification;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

}
