package com.providelearingsite.siteproject.learning;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.providelearingsite.siteproject.account.Account;
import com.providelearingsite.siteproject.account.AccountRepository;
import com.providelearingsite.siteproject.account.CurrentAccount;
import com.providelearingsite.siteproject.learning.form.LearningForm;
import com.providelearingsite.siteproject.learning.validator.LearningValidator;
import com.providelearingsite.siteproject.tag.Tag;
import com.providelearingsite.siteproject.tag.TagForm;
import com.providelearingsite.siteproject.tag.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class LearningController {

    @Autowired
    private LearningService learningService;
    @Autowired
    private LearningValidator learningValidator;
    @Autowired
    private LearningRepository learningRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @InitBinder("learningForm")
    private void initVideoForm(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(learningValidator);
    }

    @GetMapping("/profile/learning/create")
    public String viewUpload(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new LearningForm());
        return "profile/create_learning";
    }

    @PostMapping("/profile/learning/create")
    public String createLearning(@CurrentAccount Account account, Model model,
                                 @Valid LearningForm learningForm, Errors errors, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(new LearningForm());
            model.addAttribute("message", "잘못 입력 하셨습니다.");
            return "profile/create_learning";
        }

        Learning learning = learningService.saveLearning(learningForm, account);

        model.addAttribute(account);
        attributes.addFlashAttribute("message", learning.getTitle() + " 강의가 추가되었습니다");
        return "redirect:/profile/learning/create";
    }

    @GetMapping("/profile/learning/list")
    public String updateLearning(@CurrentAccount Account account, Model model) {
        Optional<Account> accountById = accountRepository.findById(account.getId());
        Account newAccount = accountById.orElseThrow();
        model.addAttribute("account", newAccount);

        return "profile/learning_list";
    }

    @GetMapping("/profile/learning/upload/{id}")
    public String viewLearning(@CurrentAccount Account account, Model model, @PathVariable Long id) throws JsonProcessingException {
        Optional<Learning> learning = learningRepository.findById(id);
        List<String> tagList = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
//        Optional<Account> accountById = accountRepository.findById(account.getId());

        model.addAttribute(account);
        model.addAttribute("learning", learning.orElseThrow());
        model.addAttribute("tags", learning.orElseThrow().getTags().stream().map(Tag::getTitle).collect(Collectors.toList()));
        model.addAttribute("whiteList", objectMapper.writeValueAsString(tagList));

        return "profile/learning";
    }

    @PostMapping("/profile/learning/upload/{id}/add")
    @ResponseBody
    public ResponseEntity learningAddTags(@CurrentAccount Account account, @PathVariable Long id,
                                          @RequestBody TagForm tagForm) {
        Optional<Learning> learning = learningRepository.findById(id);
        Tag tag = tagRepository.findByTitle(tagForm.getTitle());

        if (learning.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        if (tag == null) {
            tag = Tag.builder().title(tagForm.getTitle()).build();
        }

        learningService.saveLearningTags(learning.orElseThrow(), tag);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/profile/learning/upload/{id}/remove")
    @ResponseBody
    public ResponseEntity learningRemoveTags(@CurrentAccount Account account, @PathVariable Long id,
                                          @RequestBody TagForm tagForm) {
        Optional<Learning> learning = learningRepository.findById(id);
        Tag tag = tagRepository.findByTitle(tagForm.getTitle());

        if (learning.isEmpty() || tag == null) {
            return ResponseEntity.badRequest().build();
        }

        learningService.removeLearningTags(learning.orElseThrow(), tagForm);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/profile/learning/upload/{id}")
    public String videoUpdate(@CurrentAccount Account account, Model model, @PathVariable Long id,
                              MultipartHttpServletRequest httpServletRequest, RedirectAttributes attributes) {
        List<MultipartFile> videofile = httpServletRequest.getFiles("videofile");
        MultipartFile banner = httpServletRequest.getFile("banner");
        Optional<Learning> learningById = learningRepository.findById(id);
        Learning learning = learningById.orElseThrow();

        if (videofile.size() < 1 || banner.getSize() < 1) {
            attributes.addFlashAttribute("message", "잘못된 파일입니다. 다시 입력해주세요");

            return "redirect:/profile/learning/upload/" + learning.getId();
        }

        learningService.saveVideoAndBanner(videofile, banner, account, learning);
        attributes.addFlashAttribute("message", "저장되었습니다.");
        return "redirect:/profile/learning/upload/" + learning.getId();
    }
}
