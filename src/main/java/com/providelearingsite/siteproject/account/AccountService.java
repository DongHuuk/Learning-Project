package com.providelearingsite.siteproject.account;

import com.providelearingsite.siteproject.mail.LocalJavaMailService;
import com.providelearingsite.siteproject.profile.form.AccountUpdateForm;
import com.providelearingsite.siteproject.profile.form.PasswordUpdateForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class AccountService implements UserDetailsService {

    @Autowired private AccountRepository accountRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private LocalJavaMailService localJavaMailService;

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

    public void createAccount(Account account) {
        account.setCreateAccount(LocalDateTime.now());
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setTokenChecked(false);
        sendEmailToken(account);

        accountRepository.save(account);
    }

    private void sendEmailToken(Account account) {
        account.createEmailCheckToken();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(account.getEmail());
        mailMessage.setSubject("회원 가입 안내 메일");
        mailMessage.setText("/check-mail-token?token=" + account.getEmailCheckToken() + "&email=" + account.getEmail());
        localJavaMailService.send(mailMessage);
        log.info("/check-mail-token?token=" + account.getEmailCheckToken() + "&email=" + account.getEmail());
    }

    public void createAccountToken(Account account) {
        account.setTokenChecked(true);
        login(account);
    }

    public Account findAccountByEmailWithNotChecking(String email) {
        Account account = accountRepository.findByEmailAndTokenChecked(email, false);
        return account;
    }

    public void reCheckingEmailToken(String email) {
        Account account = accountRepository.findByEmailAndTokenChecked(email, false);
        sendEmailToken(account);
    }

    public Optional<Account> findAccount(Account account) {
        return accountRepository.findById(account.getId());
    }

    public void updateNicknameAndDescription(AccountUpdateForm accountUpdateForm, Account account) {
        account.setNickname(accountUpdateForm.getNickname());
        account.setDescription(accountUpdateForm.getDescription());
        accountRepository.save(account);
    }

    public void updatePassword(PasswordUpdateForm passwordUpdateForm, Account account) {
        account.setPassword(passwordEncoder.encode(passwordUpdateForm.getNewPassword()));
        accountRepository.save(account);
    }
}
