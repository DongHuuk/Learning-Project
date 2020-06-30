package com.providelearingsite.siteproject.main;

import com.providelearingsite.siteproject.learning.Learning;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Transactional
@SpringBootTest
class MainControllerTest {

    @Test
    public void test(){
        List<String> list = Arrays.asList("1-1_dsadwqtes.mp3", "1-2dsadwqtes.mp4","3-11fdsfaafeㅇ_ㅈㅂㅇㅇwefw",
                "3-2fdsaffewfqf.mp3", "2-1dsadpojwoqdq",
                "3-1opqjpdowqdwq","11-1epowqjoepwq", "1-11dwqodjpdwq");
        List<String> contentTitle = new ArrayList<>();
        String regExp = "[a-zA-Zㄱ-ㅎ가-힣ㅏ-ㅣ\\s_](.mp3|.mp4|mkv)";
        String regExpNot = "[0-9]+(.)[0-9]+";
        list.stream().map(String::toString).sorted()
                .forEach(s -> {
                    String number = s.replaceAll(regExp, "").trim();
                    String notNumber = s.replaceAll(regExpNot, "").trim();

                    int i = number.indexOf("-");
                    int strIndex = number.indexOf(notNumber.charAt(1));

                    log.info(notNumber);
                    log.info(strIndex + "");

                    String f = number.substring(0, i); //앞
                    String e = number.substring(i+1, strIndex); //뒤
                    String newf = "";
                    String newe = "";

                    if(f.length() <= 1){
                        newf = 0 + f;
                    }else {
                        newf = f;
                    }

                    if(e.length() <= 1){
                        newe = 0 + e;
                    }else {
                        newe = e;
                    }

                    String newStr = newf + "-" + newe;

                    System.out.println("=======");
                    System.out.println(s);
                    System.out.println(newStr);
                    System.out.println(newStr+notNumber);
                });

        contentTitle.sort(null);

    }

    @Test
    public void testFile(){
        File file = new File("");
        System.out.println(file.getAbsolutePath());
        File newFile = new File(file.getAbsolutePath() + "/src/main/resources/static/video");
    }

    @Test
    public void testString(){
        LocalDateTime dateTime = LocalDateTime.now().minusDays(4);
        System.out.println(dateTime);
    }

}