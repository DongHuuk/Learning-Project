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
import org.modelmapper.ModelMapper;
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
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class LearningController {

    static final String CREATE_LEARNING = "profile/create_learning";

    @Autowired private LearningService learningService;
    @Autowired private LearningValidator learningValidator;
    @Autowired private LearningRepository learningRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private TagRepository tagRepository;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ModelMapper modelMapper;

    @InitBinder("learningForm")
    private void initVideoForm(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(learningValidator);
    }

    @GetMapping("/profile/learning/create")
    public String viewUpload(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new LearningForm());

        return CREATE_LEARNING;
    }

    @PostMapping("/profile/learning/create")
    public String createLearning(@CurrentAccount Account account, Model model,
                                 @Valid LearningForm learningForm, Errors errors) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        Account account_1 = byId.orElseThrow();
        if (errors.hasErrors()) {
            model.addAttribute("account", account_1);
            model.addAttribute("message", "잘못 입력 하셨습니다.");
            return CREATE_LEARNING;
        }

        Learning learning = learningService.saveLearning(learningForm, account_1);

        model.addAttribute("account", account_1);
        model.addAttribute("message", learning.getTitle() + " 강의가 추가되었습니다");
        return CREATE_LEARNING;
    }

    @GetMapping("/profile/learning/list")
    public String viewLearningList(@CurrentAccount Account account, Model model) {
        Optional<Account> accountById = accountRepository.findById(account.getId());
        Account newAccount = accountById.orElseThrow();
        model.addAttribute("account", newAccount);

        return "profile/learning_list";
    }

    @GetMapping("/profile/learning/upload/{id}")
    public String viewVideoUpdate(@CurrentAccount Account account, Model model, @PathVariable Long id) throws JsonProcessingException {
        Optional<Learning> learning = learningRepository.findById(id);
        List<String> tagList = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());

        model.addAttribute(account);
        model.addAttribute("learning", learning.orElseThrow());
        model.addAttribute("tags", learning.orElseThrow().getTags().stream().map(Tag::getTitle).collect(Collectors.toList()));
        model.addAttribute("whiteList", objectMapper.writeValueAsString(tagList));

        return "profile/learning";
    }

    @PostMapping("/profile/learning/upload/{id}/add")
    @ResponseBody
    public ResponseEntity learningAddTags(@CurrentAccount Account account, @PathVariable Long id, @RequestBody TagForm tagForm) {
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
    public ResponseEntity learningRemoveTags(@CurrentAccount Account account, @PathVariable Long id, @RequestBody TagForm tagForm) {
        Optional<Learning> learning = learningRepository.findById(id);
        Tag tag = tagRepository.findByTitle(tagForm.getTitle());

        if (learning.isEmpty() || tag == null) {
            return ResponseEntity.badRequest().build();
        }

        learningService.removeLearningTags(learning.orElseThrow(), tagForm);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/profile/learning/upload/{id}/video", produces="text/plain;Charset=UTF-8")
    @ResponseBody
    public ResponseEntity videoUpdate(@CurrentAccount Account account, Model model, @PathVariable Long id,
                                    MultipartHttpServletRequest httpServletRequest) {
        List<MultipartFile> videofile = httpServletRequest.getFiles("videofile");
        Optional<Learning> learningById = learningRepository.findById(id);
        Learning learning = learningById.orElseThrow();

        if (videofile.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            learningService.saveVideo(videofile, account, learning);
        }catch (IOException e){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/profile/learning/upload/{id}/banner", produces="text/plain;Charset=UTF-8")
    @ResponseBody
    public ResponseEntity bannerUpdate(@CurrentAccount Account account, Model model, @PathVariable Long id,
                                     @RequestParam("banner") MultipartFile multipartFile) {
        Optional<Learning> learningById = learningRepository.findById(id);
        Learning learning = learningById.orElseThrow();

        String originalFilename = multipartFile.getOriginalFilename();
        int i = originalFilename.indexOf(".");
        String extension = originalFilename.substring(i + 1);

        switch (extension){
            case "jpg":
                break;
            case "png":
                break;
            case "jpeg":
                break;
            default:
                return ResponseEntity.badRequest().build();
        }

        try {
            learningService.saveBanner(multipartFile, account, learning);
        }catch (IOException e){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/learning/{id}")
    public String viewMainLearning(@CurrentAccount Account account, Model model, @PathVariable Long id) {
        Optional<Learning> learningById = learningRepository.findById(id);
        Learning learning = learningById.orElseThrow();

        float rating = learning.getRating();
        int floorRating = (int) Math.floor(rating);
        boolean halfrating = ((rating * 10) - floorRating * 10) >= 5 && Math.floor(rating) <= 5;

        model.addAttribute(account);
        model.addAttribute("listenLearning", learningService.canListenLearning(account, learning));
        model.addAttribute("learningSet", account.getLearningSet().contains(learning));
        model.addAttribute("countVideo", learning.getVideoCount());
        model.addAttribute("learning", learning);
        model.addAttribute("tags", learning.getTags().stream().map(Tag::getTitle).collect(Collectors.toList()));
        model.addAttribute("ratings", floorRating);
        model.addAttribute("halfrating", halfrating);
        model.addAttribute("rating", 5 - floorRating - (halfrating ? 1 : 0));
        model.addAttribute("learningRating", rating);

        return "learning/main_learning";
    }

    @GetMapping("/profile/learning/update/{id}")
    public String viewUpdateMainLearning(@CurrentAccount Account account, Model model, @PathVariable Long id) throws JsonProcessingException {
        Optional<Learning> byId = learningRepository.findById(id);
        Learning learning = byId.orElseThrow();
        List<String> tagList = learning.getTags().stream().map(Tag::getTitle).collect(Collectors.toList());
        LearningForm learningForm = new LearningForm();
        learningForm.setTitle(learning.getTitle());
        learningForm.setLecturerName(learning.getLecturerName());

        model.addAttribute(account);
        model.addAttribute("learning", learning);
        model.addAttribute("learningForm", learningForm);
        model.addAttribute("tags", learning.getTags().stream().map(Tag::getTitle).collect(Collectors.toList()));
        model.addAttribute("whiteList", objectMapper.writeValueAsString(tagList));

        return "profile/update_learning";
    }

    @PostMapping("/profile/learning/update/{id}/script")
    public String updateLearningScript(@CurrentAccount Account account, Model model, @PathVariable Long id,
                                       @Valid LearningForm learningForm, Errors errors,
                                       RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            attributes.addFlashAttribute("message", "값이 잘못되었습니다.");
            return "redirect:/profile/learning/update/" + id;
        }

        learningService.updateLearningScript(learningForm, account, id);

        attributes.addFlashAttribute("message", "수정되었습니다.");
        return "redirect:/profile/learning/update/" + id;
    }

    @GetMapping("/profile/learning/start/{id}")
    public String startLearning(@CurrentAccount Account account, Model model, @PathVariable Long id, RedirectAttributes attributes) {
        learningService.startLearning(id);

        attributes.addFlashAttribute("message", "강의가 오픈되었습니다.");
        return "redirect:/learning/" + id;
    }

    @GetMapping("/profile/learning/close/{id}")
    public String closedLearning(@CurrentAccount Account account, Model model, @PathVariable Long id, RedirectAttributes attributes) {
        learningService.closeLearning(id);

        attributes.addFlashAttribute("message", "강의가 닫혔습니다..");
        return "redirect:/learning/" + id;
    }
}
