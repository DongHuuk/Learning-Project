package com.providelearingsite.siteproject.kakao;

import com.providelearingsite.siteproject.learning.Learning;
import lombok.Data;

import java.util.Set;

@Data
public class KakaoPayForm {

    private Set<Learning> learningList;

}
