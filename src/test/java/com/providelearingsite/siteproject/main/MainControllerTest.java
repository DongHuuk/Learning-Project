package com.providelearingsite.siteproject.main;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {

    @Test
    public void test(){
        StringBuilder str = new StringBuilder("4500000000");
        int index = str.length();
        int insertTime = index - 3;
        float i = index / 3;

        for (int j=0; j < i; j++){
            if(index % 3 <= 0){
                i--;
            }
            str.insert(insertTime, ',');
            insertTime -= 3;
        }
        System.out.println(str);
    }
}