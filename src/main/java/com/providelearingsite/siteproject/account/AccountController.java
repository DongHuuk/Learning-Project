package com.providelearingsite.siteproject.account;

import com.providelearingsite.siteproject.account.form.AccountForm;
import com.providelearingsite.siteproject.account.form.EmailToken;
import com.providelearingsite.siteproject.account.validator.AccountValidator;
import com.providelearingsite.siteproject.learning.Learning;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Request;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.http.HttpRequest;
import java.util.Set;

@Controller
@Slf4j
public class AccountController {

    @Autowired private AccountValidator accountValidator;
    @Autowired private AccountService accountService;
    @Autowired private ModelMapper modelMapper;
    @Autowired private AccountRepository accountRepository;

    @InitBinder("accountForm")
    private void accoutnFormValidator(WebDataBinder webDataBinder){
        webDataBinder.addValidators(accountValidator);
    }

    @GetMapping("/create")
    public String showAccountForm(Model model) {

        model.addAttribute(new AccountForm());

        return "navbar/create_account";
    }

    @PostMapping("/create")
    public String createAccount(@Valid AccountForm accountForm, Errors errors, Model model) {

        if(errors.hasErrors()){
            return "navbar/create_account";
        }

        accountService.createAccount(modelMapper.map(accountForm, Account.class));
        //Repository에 저장 + token chekcing value = false, 이 값이 false일 경우 정상 작동하지 않도록

        model.addAttribute("message", "인증용 메일이 전송 되었습니다. 확인해주세요");
        model.addAttribute(new EmailToken());
        return "navbar/token_validation";
    }

    @GetMapping("/check-token")
    public String checkEmailToken(String token, String email, Model model, RedirectAttributes attributes) {
        Account account = accountService.findAccountByEmailWithNotChecking(email);

        if(account == null || !account.getEmailCheckToken().equals(token)){
            model.addAttribute("message", "인증 주소가 잘못 되었습니다. 다시 신청해주시거나, 재전송 버튼을 눌러주세요");
            model.addAttribute("email", email);
            model.addAttribute(new EmailToken());

            return "navbar/token_validation";
        }

        accountService.createAccountToken(account);

        attributes.addFlashAttribute("account", account);
        attributes.addFlashAttribute("success", "인증이 완료되었습니다.");
        return "redirect:/";
    }

    @PostMapping("/recheck-token")
    public String submitReCheckEmailToken(EmailToken email, Model model, RedirectAttributes attributes) {
        Account account = accountService.findAccountByEmailWithNotChecking(email.getEmail());

        if (account.canCheckingEmailToken()) {
            accountService.reCheckingEmailToken(account);
            attributes.addFlashAttribute("message", "인증용 메일이 전송 되었습니다. 확인해주세요");
            return "redirect:/";
        }

        model.addAttribute("message", "인증용 메일은 1시간에 한번만 전송이 가능합니다. 양해 부탁드립니다");
        model.addAttribute(new EmailToken());
        return "navbar/token_validation";
    }

    @GetMapping("/learning/cart")
    public String viewCartLearning(@CurrentAccount Account account, Model model) {
        Account newAccount = accountRepository.findById(account.getId()).orElseThrow();
        Set<Learning> cartList = newAccount.getCartList();

        model.addAttribute("account", newAccount);
        model.addAttribute("cartList", cartList);
        model.addAttribute("totalPrice", cartList.stream().mapToInt(Learning::getPrice).sum());

        return "profile/cart";
    }

}
