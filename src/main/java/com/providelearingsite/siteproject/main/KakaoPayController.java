package com.providelearingsite.siteproject.main;

import com.providelearingsite.siteproject.kakao.KakaoPay;
import com.providelearingsite.siteproject.kakao.KakaoPayApprovalVO;
import com.providelearingsite.siteproject.kakao.KakaoPayForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;

@Slf4j
@Controller
public class KakaoPayController {

    @Autowired private KakaoPay kakaoPay;

    @GetMapping("/kakaoPay")
    public void kakaoPay_Get(){

    }

    @PostMapping("/kakaoPay")
    public String kakaoPay_Post(RedirectAttributes attributes, KakaoPayForm kakaoPayForm){
        log.info("kakaoPay Post --------------");

        //TODO Form이 Set을 못받아옴 얘를 ajax로 보내던가 해야함

        return "redirect:" + kakaoPay.kakaoPayReady();
    }

    @GetMapping("/kakaoPaySuccess")
    public String kakaoPaySuccess(@RequestParam("pg_token") String pg_token, Model model) {
        KakaoPayApprovalVO kakaoPayApprovalVO = kakaoPay.kakaoPayInfo(pg_token);

        model.addAttribute("info", kakaoPayApprovalVO);
        return "shop/paysuccess";
    }

    @GetMapping("/kakaoPayCancel")
    public String kakaoPayCancel(Model model){

        model.addAttribute("info", kakaoPay.kakaoPayCancel());
        return "shop/cancel";
    }

}
