package com.providelearingsite.siteproject.profile;

import com.providelearingsite.siteproject.account.Account;
import com.providelearingsite.siteproject.account.AccountService;
import com.providelearingsite.siteproject.account.CurrentAccount;
import com.providelearingsite.siteproject.learning.form.VideoForm;
import com.providelearingsite.siteproject.profile.form.NotificationUpdateForm;
import com.providelearingsite.siteproject.profile.form.ProfileUpdateForm;
import com.providelearingsite.siteproject.profile.form.PasswordUpdateForm;
import com.providelearingsite.siteproject.profile.validator.ProfileNicknameValidator;
import com.providelearingsite.siteproject.profile.validator.ProfilePasswordValidator;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@Controller
public class ProfileController {

    @Autowired
    private ProfileNicknameValidator profileNicknameValidator;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ProfilePasswordValidator profilePasswordValidator;

    private void addForms(@CurrentAccount Account account, Model model) {
        model.addAttribute(new ProfileUpdateForm());
        model.addAttribute(new PasswordUpdateForm());
        model.addAttribute(modelMapper.map(account, NotificationUpdateForm.class));
    }

    @InitBinder("accountUpdateForm")
    public void nicknameUpdate(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(profileNicknameValidator);
    }

    @InitBinder("passwordUpdateForm")
    public void passwordUpdate(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(profilePasswordValidator);
    }

    @GetMapping("/profile/{id}")
    public String viewProfile(@CurrentAccount Account account, @PathVariable Long id, Model model) {
        model.addAttribute(account);
        return "navbar/profile";
    }

    @GetMapping("/profile/{id}/custom")
    public String viewRevise(@CurrentAccount Account account, @PathVariable Long id, Model model) {
        model.addAttribute(account);
        addForms(account, model);

        return "profile/custom_profile";
    }

    @PostMapping("/update/nickname/{id}")
    public String updateNicknameForm(@CurrentAccount Account account, @PathVariable Long id,
                                     @Valid ProfileUpdateForm profileUpdateForm, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(new PasswordUpdateForm());
            model.addAttribute(modelMapper.map(account, NotificationUpdateForm.class));
            return "profile/custom_profile";
        }

        Account newAccount = accountService.updateNicknameAndDescription(profileUpdateForm, account);

        model.addAttribute(newAccount);
        addForms(account, model);
        model.addAttribute("message", "프로필이 수정되었습니다.");
        return "redirect:/profile/" + account.getId() + "/custom";
    }

    @PostMapping("/update/password/{id}")
    public String updatePasswordForm(@CurrentAccount Account account, @PathVariable Long id,
                                     @Valid PasswordUpdateForm passwordUpdateForm, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(new ProfileUpdateForm());
            model.addAttribute(modelMapper.map(account, NotificationUpdateForm.class));
            return "profile/custom_profile";
        }

        final Account newAccount = accountService.updatePassword(passwordUpdateForm, account);

        model.addAttribute(newAccount);
        model.addAttribute("message", "비밀번호가 수정되었습니다.");
        addForms(account, model);
        return "redirect:/profile/" + account.getId() + "/custom";
    }

    @PostMapping("/update/noti/{id}")
    public String updateNotificationForm(@CurrentAccount Account account, @PathVariable Long id, Model model,
                                         NotificationUpdateForm notificationUpdateForm) {
        Account newAccount = accountService.updateNotifications(notificationUpdateForm, account);

        model.addAttribute(newAccount);
        model.addAttribute("message", "알림 설정이 완료되었습니다.");
        addForms(account, model);
        return "redirect:/profile/" + account.getId() + "/custom";
    }

    @GetMapping("/profile/{id}/upload")
    public String viewUpload(@CurrentAccount Account account, @PathVariable Long id, Model model) {
        model.addAttribute(account);
        model.addAttribute(new VideoForm());
        return "profile/upload";
    }

    @PostMapping("/profile/{id}/upload")
    public String updateVideo(@CurrentAccount Account account, @PathVariable Long id, Model model
            , @Valid VideoForm videoForm, Errors errors) {
        model.addAttribute(account);
        model.addAttribute(new VideoForm());
        return "profile/upload";
    }
}
