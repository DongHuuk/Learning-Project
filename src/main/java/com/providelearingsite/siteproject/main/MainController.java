package com.providelearingsite.siteproject.main;

import com.providelearingsite.siteproject.account.Account;
import com.providelearingsite.siteproject.account.CurrentAccount;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String index(@CurrentAccount Account account, Model model){
        if(account != null){
            model.addAttribute(account);
        }

        return "index";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/logout")
    public String logout(){
        return "/";
    }

}
