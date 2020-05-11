package com.providelearingsite.siteproject.profile;

import com.providelearingsite.siteproject.account.Account;
import com.providelearingsite.siteproject.account.CurrentAccount;
import com.providelearingsite.siteproject.profile.form.AccountUpdateForm;
import com.providelearingsite.siteproject.profile.validator.ProfileNicknameValidator;
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

    @InitBinder("accountUpdateForm")
    public void nicknameUpdate(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(profileNicknameValidator);
    }

    @GetMapping("/profile/{nickname}")
    public String viewProfile(@CurrentAccount Account account, @PathVariable String nickname, Model model){
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

        return "profile/custom_profile";
    }

    @PostMapping("/update/{nickname}")
    public String updateAccountForm(@CurrentAccount Account account, @PathVariable String nickname,
                                    @Valid AccountUpdateForm accountUpdateForm, Errors errors, Model model) {
        if (account == null) {
            return "index";
        }

        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "redirect:/profile/" + account.getId() + "/custom";
        }


        return "index";
    }

}
