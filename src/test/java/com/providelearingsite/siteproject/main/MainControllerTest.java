package com.providelearingsite.siteproject.main;

import com.providelearingsite.siteproject.learning.Learning;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {

    @Test
    public void test(){
        List<String> list = Arrays.asList("1-1dsadwqtes", "1-2dsadwqtes","3-11fdsfaafeㅇ ㅈㅂㅇㅇwefw","3-21개씨발좆같은거존나안되네븅신같은거", "2-1dsadpojwoqdq", "3-1opqjpdowqdwq","11-1epowqjoepwq", "1-11dwqodjpdwq");
        List<String> contentTitle = new ArrayList<>();
        list.stream().map(String::toString).sorted()
                .forEach(s -> {
                    String s1 = s.replaceAll("[a-zA-Z가-힣ㄱ-ㅋㅏ-ㅣ]", "").trim();
                    int i = s1.indexOf("-");

                    String f = s1.substring(0, i); //앞
                    String e = s1.substring(i+1); //뒤
                    String newf = "";
                    String newe = "";

                    if(f.length() != 2){
                        newf = 0 + f;
                    }else {
                        newf = f;
                    }

                    if(e.length() != 2){
                        newe = 0 + e;
                    }else {
                        newe = e;
                    }
                    contentTitle.add(newf + "-" + newe);
                });
        contentTitle.sort(null);
        System.out.println(contentTitle.toString());
    }
}