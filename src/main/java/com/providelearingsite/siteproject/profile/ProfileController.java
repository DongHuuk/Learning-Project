package com.providelearingsite.siteproject.profile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.providelearingsite.siteproject.account.Account;
import com.providelearingsite.siteproject.account.AccountService;
import com.providelearingsite.siteproject.account.CurrentAccount;
import com.providelearingsite.siteproject.learning.validator.LearningValidator;
import com.providelearingsite.siteproject.profile.form.NotificationUpdateForm;
import com.providelearingsite.siteproject.profile.form.ProfileUpdateForm;
import com.providelearingsite.siteproject.profile.form.PasswordUpdateForm;
import com.providelearingsite.siteproject.profile.validator.ProfileNicknameValidator;
import com.providelearingsite.siteproject.profile.validator.ProfilePasswordValidator;
import com.providelearingsite.siteproject.tag.Tag;
import com.providelearingsite.siteproject.tag.TagForm;
import com.providelearingsite.siteproject.tag.TagRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class ProfileController {

    @Autowired private ProfileNicknameValidator profileNicknameValidator;
    @Autowired private AccountService accountService;
    @Autowired private ModelMapper modelMapper;
    @Autowired private ProfilePasswordValidator profilePasswordValidator;
    @Autowired private LearningValidator learningValidator;
    @Autowired private TagRepository tagRepository;
    @Autowired private ObjectMapper objectMapper;

    private final static String CUSTOM_PROFILE = "profile/custom_profile";

    private static String redirectPath_Custom(Long id){
        return "redirect:/profile/" + id + "/custom";
    }

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

    @InitBinder("videoForm")
    public void uploadFiles(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(learningValidator);
    }

    @GetMapping("/profile/{id}")
    public String viewProfile(@CurrentAccount Account account, @PathVariable Long id, Model model) {
        model.addAttribute(account);
        return "navbar/profile";
    }

    @GetMapping("/profile/{id}/custom")
    public String viewRevise(@CurrentAccount Account account, @PathVariable Long id, Model model) throws JsonProcessingException {
        Set<Tag> tags = accountService.getTags(account);
        List<String> tagList = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());

        model.addAttribute(account);
        model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toList()));
        model.addAttribute("whiteList", objectMapper.writeValueAsString(tagList));
        addForms(account, model);

        return CUSTOM_PROFILE;
    }

    @PostMapping("/update/nickname/{id}")
    public String updateNicknameForm(@CurrentAccount Account account, @PathVariable Long id, Model model,
                                     @Valid ProfileUpdateForm profileUpdateForm, Errors errors, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(new PasswordUpdateForm());
            model.addAttribute(modelMapper.map(account, NotificationUpdateForm.class));
            return CUSTOM_PROFILE;
        }

        Account newAccount = accountService.updateNicknameAndDescription(profileUpdateForm, account);

        model.addAttribute(newAccount);
        addForms(newAccount, model);
        attributes.addFlashAttribute("message", "프로필이 수정되었습니다.");
        return redirectPath_Custom(newAccount.getId());
    }

    @PostMapping("/update/password/{id}")
    public String updatePasswordForm(@CurrentAccount Account account, @PathVariable Long id,
                                     @Valid PasswordUpdateForm passwordUpdateForm, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(new ProfileUpdateForm());
            model.addAttribute(modelMapper.map(account, NotificationUpdateForm.class));
            return redirectPath_Custom(account.getId());
        }

        final Account newAccount = accountService.updatePassword(passwordUpdateForm, account);

        model.addAttribute(newAccount);
        model.addAttribute("message", "비밀번호가 수정되었습니다.");
        addForms(account, model);
        return redirectPath_Custom(account.getId());
    }

    @PostMapping("/update/noti/{id}")
    public String updateNotificationForm(@CurrentAccount Account account, @PathVariable Long id, Model model,
                                         NotificationUpdateForm notificationUpdateForm) {
        Account newAccount = accountService.updateNotifications(notificationUpdateForm, account);

        model.addAttribute(newAccount);
        model.addAttribute("message", "알림 설정이 완료되었습니다.");
        addForms(account, model);
        return redirectPath_Custom(account.getId());
    }

    @PostMapping("/update/tags/add")
    @ResponseBody
    public ResponseEntity addTag(@CurrentAccount Account account, @RequestBody TagForm tagForm){
        String title = tagForm.getTitle();
        Tag tag = tagRepository.findByTitle(title);
        if(tag == null){
            tag = tagRepository.save(Tag.builder()
                    .title(tagForm.getTitle())
                    .build());
        }

        accountService.addTag(account, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update/tags/remove")
    @ResponseBody
    public ResponseEntity removeTag(@CurrentAccount Account account, @RequestBody TagForm tagForm){
        String title = tagForm.getTitle();
        Tag tag = tagRepository.findByTitle(title);

        if(tag == null){
            return ResponseEntity.badRequest().build();
        }

        accountService.deleteTag(account, tag);
        return ResponseEntity.ok().build();
    }

}
