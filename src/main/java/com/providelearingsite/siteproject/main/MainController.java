package com.providelearingsite.siteproject.main;

import com.providelearingsite.siteproject.account.Account;
import com.providelearingsite.siteproject.account.CurrentAccount;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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

    @PostMapping("/login")
    public String loginPost(Model model){

        model.addAttribute("message", "로그인 정보가 없습니다. 계정을 확인 해주세요.");

        return "login";
    }

    @GetMapping("/logout")
    public String logout(){
        return "index";
    }

    @GetMapping("/learning/program")
    public String viewProgramming(@CurrentAccount Account account, Model model){
        if(account != null){
            model.addAttribute(account);
        }

        return "learning/program";
    }

    @GetMapping("/learning/algorithm")
    public String viewSelf(@CurrentAccount Account account, Model model){
        if(account != null){
            model.addAttribute(account);
        }

        return "learning/program";
    }
}
