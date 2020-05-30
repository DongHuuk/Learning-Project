package com.providelearingsite.siteproject.learning;

import com.providelearingsite.siteproject.account.Account;
import com.providelearingsite.siteproject.account.CurrentAccount;
import com.providelearingsite.siteproject.learning.form.LearningForm;
import com.providelearingsite.siteproject.learning.validator.LearningValidator;
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
public class LearningController {

    @Autowired private LearningService learningService;
    @Autowired private LearningValidator learningValidator;

    @InitBinder("learningForm")
    private void initVideoForm(WebDataBinder webDataBinder){
        webDataBinder.addValidators(learningValidator);
    }

    @GetMapping("/profile/create_learning")
    public String viewUpload(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new LearningForm());
        return "profile/create_learning";
    }

    @PostMapping("/profile/learning/create")
    public String updateLearning(@CurrentAccount Account account, Model model,
                                 @Valid LearningForm learningForm, Errors errors, RedirectAttributes attributes){
        if(errors.hasErrors()){
            model.addAttribute(account);
            model.addAttribute(new LearningForm());
            model.addAttribute("message", "잘못 입력 하셨습니다.");
            return "profile/create_learning";
        }

        Learning learning = learningService.saveLearning(learningForm, account);

        model.addAttribute(account);
        attributes.addFlashAttribute("message", learning.getTitle() + " 강의가 추가되었습니다");
        return "redirect:/profile/create_learning";
    }

}
