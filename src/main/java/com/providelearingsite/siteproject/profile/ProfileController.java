package com.providelearingsite.siteproject.profile;

import com.providelearingsite.siteproject.account.Account;
import com.providelearingsite.siteproject.account.AccountRepository;
import com.providelearingsite.siteproject.account.AccountService;
import com.providelearingsite.siteproject.account.CurrentAccount;
import com.providelearingsite.siteproject.profile.form.AccountUpdateForm;
import com.providelearingsite.siteproject.profile.form.PasswordUpdateForm;
import com.providelearingsite.siteproject.profile.validator.ProfileNicknameValidator;
import com.providelearingsite.siteproject.profile.validator.ProfilePasswordValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class ProfileController {

    @Autowired private ProfileNicknameValidator profileNicknameValidator;
    @Autowired private AccountService accountService;
    @Autowired private ModelMapper modelMapper;
    @Autowired private ProfilePasswordValidator profilePasswordValidator;

    @InitBinder("accountUpdateForm")
    public void nicknameUpdate(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(profileNicknameValidator);
    }
    @InitBinder("passwordUpdateForm")
    public void passwordUpdate(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(profilePasswordValidator);
    }


    @GetMapping("/profile/{id}")
    public String viewProfile(@CurrentAccount Account account, @PathVariable Long id, Model model){
        if(account == null){
            return "index";
        }

        model.addAttribute(account);
        return "navbar/profile";
    }

    @GetMapping("/profile/{id}/custom")
    public String viewRevise(@CurrentAccount Account account, @PathVariable Long id, Model model) {
        if(account == null){
            return "index";
        }

        model.addAttribute(account);
        model.addAttribute(new AccountUpdateForm());
        model.addAttribute(new PasswordUpdateForm());

        return "profile/custom_profile";
    }

    @PostMapping("/update/nickname/{id}")
    public String updateNicknameForm(@CurrentAccount Account account, @PathVariable Long id,
                                    @Valid AccountUpdateForm accountUpdateForm, Errors errors, Model model) {
        if (account == null) {
            return "index";
        }

        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(new PasswordUpdateForm());
            return "profile/custom_profile";
        }

        accountService.updateNicknameAndDescription(accountUpdateForm, account);

        model.addAttribute(account);
        model.addAttribute("message", "프로필이 수정되었습니다.");
        return "redirect:/profile/" + account.getId() + "/custom";
    }

    @PostMapping("/update/password/{id}")
    public String updatePasswordForm(@CurrentAccount Account account, @PathVariable Long id,
                                    @Valid PasswordUpdateForm passwordUpdateForm, Errors errors, Model model) {
        if (account == null) {
            return "index";
        }

        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(new AccountUpdateForm());
            return "profile/custom_profile";
        }

        accountService.updatePassword(passwordUpdateForm, account);

        model.addAttribute(account);
        model.addAttribute("message", "비밀번호가 수정되었습니다.");
        return "redirect:/profile/" + account.getId() + "/custom";
    }

}
