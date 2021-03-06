package com.providelearingsite.siteproject.account;

import com.providelearingsite.siteproject.config.AppProperties;
import com.providelearingsite.siteproject.learning.Learning;
import com.providelearingsite.siteproject.mail.EmailMessage;
import com.providelearingsite.siteproject.mail.EmailService;
import com.providelearingsite.siteproject.profile.form.NotificationUpdateForm;
import com.providelearingsite.siteproject.profile.form.ProfileUpdateForm;
import com.providelearingsite.siteproject.profile.form.PasswordUpdateForm;
import com.providelearingsite.siteproject.review.Review;
import com.providelearingsite.siteproject.review.ReviewRepository;
import com.providelearingsite.siteproject.tag.Tag;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@Transactional
public class AccountService implements UserDetailsService {

    @Autowired private AccountRepository accountRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private EmailService emailService;
    @Autowired private ModelMapper modelMapper;
    @Autowired private TemplateEngine templateEngine;
    @Autowired private AppProperties appProperties;
    @Autowired private ReviewRepository reviewRepository;

    public void login(Account account){
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new AccountUser(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
    }//정석이 아님

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmailAndTokenChecked(email, true);

        if(account == null){
            throw new UsernameNotFoundException(email);
        }

        return new AccountUser(account);
    }// security가 DB에 access해서 값을 가져올 수 있게 해주는 adapter

    public Account createAccount(Account account) {
        account.setCreateAccount(LocalDateTime.now());
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setTokenChecked(false);
        sendEmailToken(account);

        return accountRepository.save(account);
    }

    private void sendEmailToken(Account account) {
        account.createEmailCheckToken();
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message", "이메일 인증을 마치시려면 링크를 클릭해주세요.");
        context.setVariable("host", appProperties.getHost());
        context.setVariable("link", "/check-token?token=" + account.getEmailCheckToken() + "&email=" + account.getEmail());

        String process = templateEngine.process("mail/simplemail", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getEmail())
                .subject("회원 가입 안내 메일")
                .message(process)
                .build();

        emailService.sendEmail(emailMessage);

        log.info("/check-token?token=" + account.getEmailCheckToken() + "&email=" + account.getEmail());
    }

    public void createAccountToken(Account account) {
        account.setTokenChecked(true);
        login(account);
    }

    public Account findAccountByEmailWithNotChecking(String email) {
        return accountRepository.findByEmailAndTokenChecked(email, false);
    }

    public void reCheckingEmailToken(Account account) {
        sendEmailToken(account);
    }

    public Account updateNicknameAndDescription(ProfileUpdateForm profileUpdateForm, Account account) {
        if (profileUpdateForm.getNickname() != null && !profileUpdateForm.getNickname().isEmpty()) {
            account.setNickname(profileUpdateForm.getNickname());
        }

        if (profileUpdateForm.getDescription() != null) {
            account.setDescription(profileUpdateForm.getDescription());
        }

        return accountRepository.save(account);
    }

    public Account updatePassword(PasswordUpdateForm passwordUpdateForm, Account account) {
        account.setPassword(passwordEncoder.encode(passwordUpdateForm.getNewPassword()));
        return accountRepository.save(account);
    }

    public Account updateNotifications(NotificationUpdateForm notificationUpdateForm, Account account) {
        modelMapper.map(notificationUpdateForm, account);
        return accountRepository.save(account);
    }

    public void addTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().add(tag));
    }

    public Set<Tag> getTags(Account account) {
        Account newAccount = accountRepository.findById(account.getId()).orElseThrow();
        return newAccount.getTags();
    }

    public void deleteTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.orElseThrow().getTags().remove(tag);
    }

    public Review saveReview(Account account, Review review) {
        review.setCreateTime(LocalDateTime.now());
        Review newReview = reviewRepository.save(review);
        account.setReviews(review);
        return newReview;
    }

    public void addLearningInCart(Account newAccount, Learning learning) {
        if (!newAccount.getCartList().contains(learning)) {
            newAccount.getCartList().add(learning);
        }
    }

    public Account setListenLearningAndRemoveCartList(Account account, List<Learning> learningList) {
        Account newAccount = accountRepository.findById(account.getId()).orElseThrow();

        for (Learning learning :
                learningList) {
            newAccount.getListenLearning().add(learning);
            learning.getAccounts().add(account);
            learning.setBuyLearning(LocalDateTime.now());
            newAccount.getCartList().remove(learning);
        }

        return newAccount;
    }

    public Account removeListenLearning(Account account, List<Learning> learningList) {
        Account newAccount = accountRepository.findById(account.getId()).orElseThrow();

        for (Learning learning :
                learningList) {
            newAccount.getListenLearning().remove(learning);
            learning.getAccounts().remove(newAccount);
        }

        return newAccount;
    }
}
