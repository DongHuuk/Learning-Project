package com.providelearingsite.siteproject.main;

import com.providelearingsite.siteproject.learning.Learning;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {

    @Test
    public void test(){
        String keyword = "ewqjpe%#@!opjwqpe";
        String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z]";
        keyword = keyword.replaceAll(match, "");
        System.out.println(keyword);
    }
}