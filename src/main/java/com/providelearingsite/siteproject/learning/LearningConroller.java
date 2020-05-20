package com.providelearingsite.siteproject.learning;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LearningConroller {

    @GetMapping("/learning/program")
    public String viewProgramming(Model model){

        return "learning/program";
    }

    @GetMapping("/learning/algorithm")
    public String viewSelf(Model model){

        return "learning/program";
    }
}
