package com.providelearingsite.siteproject.main;

import com.providelearingsite.siteproject.account.Account;
import com.providelearingsite.siteproject.account.AccountRepository;
import com.providelearingsite.siteproject.account.CurrentAccount;
import com.providelearingsite.siteproject.learning.Learning;
import com.providelearingsite.siteproject.learning.LearningRepository;
import com.providelearingsite.siteproject.learning.LearningService;
import com.providelearingsite.siteproject.review.ReviewForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private MainService mainService;
    @Autowired
    private LearningRepository learningRepository;
    @Autowired private AccountRepository accountRepository;

    @GetMapping("/")
    public String indexGet(@CurrentAccount Account account, Model model) {
        List<Learning> learningList = new ArrayList<>();

        if (account != null) {
            learningList = learningRepository.findTop4ByTagsOrderByRatingDesc(account.getTags());
            model.addAttribute(account);
            model.addAttribute("learningList", learningList);
        } else {
            learningList = mainService.learningOrderByRating();
            model.addAttribute("learningList", learningList);
        }

        List<Learning> learningOrderByDatetime = mainService.learningOrderByCreateLearning();
        model.addAttribute("learningOrderByDatetime", learningOrderByDatetime);

        return "index";
    }

    @PostMapping("/")
    public String indexPost(@CurrentAccount Account account, Model model) {
        if (account != null) {
            model.addAttribute(account);
        }

        return "index";
    }

    @GetMapping("/login")
    public String loginGet() {
        return "login";
    }

    @GetMapping("/login-error")
    public String loginPost(RedirectAttributes attributes) {
        attributes.addFlashAttribute("message", "로그인 정보가 없습니다. 계정을 확인 해주세요.");

        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "index";
    }


}
