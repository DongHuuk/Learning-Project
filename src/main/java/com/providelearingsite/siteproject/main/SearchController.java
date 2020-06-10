package com.providelearingsite.siteproject.main;

import com.providelearingsite.siteproject.account.Account;
import com.providelearingsite.siteproject.account.CurrentAccount;
import com.providelearingsite.siteproject.learning.Learning;
import com.providelearingsite.siteproject.learning.LearningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SearchController {

    @Autowired
    private LearningRepository learningRepository;

    //모든 강의의
   @GetMapping("/all")
    public String viewAll(@CurrentAccount Account account, Model model){
       if(account != null){
           model.addAttribute(account);
       }

       List<Learning> learningList = learningRepository.findAllByStartingLearning(true);

       model.addAttribute("pathLink", "모든 강의");
       model.addAttribute("learningList", learningList);

       return "learning/program";
    }
    //웹 All
    @GetMapping("/web/all")
    public String viewWebAll(@CurrentAccount Account account, Model model){
        if(account != null){
            model.addAttribute(account);
        }

        return "learning/program";
    }
    //웹 Java
    @GetMapping("/web/java")
    public String viewWebJava(@CurrentAccount Account account, Model model){
        if(account != null){
            model.addAttribute(account);
        }

        return "learning/program";
    }
    //웹 javascript
    @GetMapping("/web/javascript")
    public String viewWebJavascript(@CurrentAccount Account account, Model model){
        if(account != null){
            model.addAttribute(account);
        }

        return "learning/program";
    }
    //웹 CSS_HTML
    @GetMapping("/web/html")
    public String viewWebCSS_HTML(@CurrentAccount Account account, Model model){
        if(account != null){
            model.addAttribute(account);
        }

        return "learning/program";
    }
    //알고리즘 ALL
    @GetMapping("/algorithm/all")
    public String viewAlgorithmAll(@CurrentAccount Account account, Model model){
        if(account != null){
            model.addAttribute(account);
        }

        return "learning/program";
    }
    //알고리즘 GOF
    @GetMapping("/algorithm/gof")
    public String viewAlgorithmGOF(@CurrentAccount Account account, Model model){
        if(account != null){
            model.addAttribute(account);
        }

        return "learning/program";
    }
    //알고리즘 알고리즘
    @GetMapping("/algorithm/algorithm")
    public String viewAlgorithm(@CurrentAccount Account account, Model model){
        if(account != null){
            model.addAttribute(account);
        }

        return "learning/program";
    }

    @GetMapping("/search/learning")
    public String searchLearning(@CurrentAccount Account account, String keyword, Model model){
        if(account != null){
            model.addAttribute(account);
        }

        List<Learning> learningList = learningRepository.findByKeyword(keyword);
        model.addAttribute(learningList);
        model.addAttribute(keyword);

        return "learning/program";
    }

}
//
//@Builder
//class PathValue{
//    private String name;
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//}
