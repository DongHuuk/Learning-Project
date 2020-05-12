package com.providelearingsite.siteproject.account;

import com.providelearingsite.siteproject.account.form.AccountForm;
import com.providelearingsite.siteproject.account.form.ReEmailToken;
import com.providelearingsite.siteproject.account.validator.AccountValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class AccountController {

    @Autowired private AccountValidator accountValidator;
    @Autowired private AccountService accountService;
    @Autowired private ModelMapper modelMapper;

    @InitBinder("accountForm")
    private void accoutnFormValidator(WebDataBinder webDataBinder){
        webDataBinder.addValidators(accountValidator);
    }

    @GetMapping("/create-sign")
    public String showAccountForm(Model model) {

        model.addAttribute(new AccountForm());

        return "navbar/createAccount";
    }

    @PostMapping("/create-sign")
    public String createAccount(@Valid AccountForm accountForm, Errors errors,Model model) {

        if(errors.hasErrors()){
            return "navbar/createAccount";
        }

        accountService.createAccount(modelMapper.map(accountForm, Account.class));
        //Repository에 저장 + token chekcing value = false, 이 값이 false일 경우 정상 작동하지 않도록
        model.addAttribute("message", "인증용 메일이 전송 되었습니다. 확인해주세요");
        model.addAttribute(new ReEmailToken());
        return "navbar/rechecking-token";
    }

    @GetMapping("/check-mail-token")
    public String checkEmailToken(String token, String email, Model model, RedirectAttributes attributes) {
        Account account = accountService.findAccountByEmailWithNotChecking(email);

        if(account == null || !account.getEmailCheckToken().equals(token)){
            model.addAttribute("message", "인증 주소가 잘못 되었습니다. 다시 신청해주시거나, 재전송 버튼을 눌러주세요");
            model.addAttribute("email", email);
            model.addAttribute(new ReEmailToken());
            return "navbar/rechecking-token";
        }

        accountService.createAccountToken(account);

        model.addAttribute(account);
        attributes.addFlashAttribute("success", "인증이 완료되었습니다.");
        return "index";
    }

    @PostMapping("/recheck-mail-token")
    public String submitReCheckEmailToken(ReEmailToken email, Model model) {
        Account account = accountService.findAccountByEmailWithNotChecking(email.getEmail());

        if (account.canCheckingEmailToken()) {
            accountService.reCheckingEmailToken(email.getEmail());
            model.addAttribute("message", "인증용 메일이 전송 되었습니다. 확인해주세요");

            return "index";
        }

        model.addAttribute("message", "인증용 메일은 1시간에 한번만 전송이 가능합니다. 양해 부탁드립니다");
        model.addAttribute(new ReEmailToken());
        return "navbar/rechecking-token";
    }
}
