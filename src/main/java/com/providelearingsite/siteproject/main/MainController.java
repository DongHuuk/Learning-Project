package com.providelearingsite.siteproject.main;

import com.providelearingsite.siteproject.account.Account;
import com.providelearingsite.siteproject.account.CurrentAccount;
import com.providelearingsite.siteproject.learning.Learning;
import com.providelearingsite.siteproject.learning.LearningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class MainController {

    @GetMapping("/")
    public String indexGet(@CurrentAccount Account account, Model model){
        if(account != null){
            model.addAttribute(account);
        }

        return "index";
    }

    @PostMapping("/")
    public String indexPost(@CurrentAccount Account account, Model model){
        if(account != null){
            model.addAttribute(account);
        }

        return "index";
    }

    @GetMapping("/login")
    public String loginGet(){
        return "login";
    }

    @GetMapping("/login-error")
    public String loginPost(RedirectAttributes attributes){
        attributes.addFlashAttribute("message", "로그인 정보가 없습니다. 계정을 확인 해주세요.");

        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(){
        return "index";
    }
}
