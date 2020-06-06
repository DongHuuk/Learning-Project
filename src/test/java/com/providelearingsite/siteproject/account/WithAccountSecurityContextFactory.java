package com.providelearingsite.siteproject.account;

import com.providelearingsite.siteproject.account.form.AccountForm;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

    @Autowired private AccountService accountService;
    @Autowired private ModelMapper modelMapper;

    @Override
    public SecurityContext createSecurityContext(WithAccount withAccount) {
        String email = withAccount.value();

        AccountForm accountForm = new AccountForm();
        accountForm.setNickname("테스트냥이");
        accountForm.setEmail(email);
        accountForm.setPassword("1234567890");
        accountForm.setPasswordcheck("1234567890");
        Account account = accountService.createAccount(modelMapper.map(accountForm, Account.class));
        account.setTokenChecked(true);

        UserDetails principal = accountService.loadUserByUsername(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(),
                principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        return context;
    }
}
