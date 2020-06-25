package com.providelearingsite.siteproject.kakao;

import com.providelearingsite.siteproject.account.Account;
import com.providelearingsite.siteproject.account.AccountService;
import com.providelearingsite.siteproject.account.CurrentAccount;
import com.providelearingsite.siteproject.learning.Learning;
import com.providelearingsite.siteproject.learning.LearningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@Controller
public class KakaoPayController {

    @Autowired private KakaoPay kakaoPay;
    @Autowired private LearningService learningService;
    @Autowired private AccountService accountService;

    @GetMapping("/kakaoPay")
    public void kakaoPay_Get(){

    }

    @PostMapping("/kakaoPay")
    public String kakaoPay_Post(@CurrentAccount Account account, KakaoPayForm kakaoPayForm, HttpServletRequest request){
        HttpSession session = request.getSession();
        KakaoPayReadyVO kakaoPayReadyVO = kakaoPay.kakaoPayReady();

        session.setAttribute(account.getId() + "_id_str", kakaoPayForm.getId());
        session.setAttribute(account.getId() + "_lecture_str", kakaoPayForm.getLecture());
        session.setAttribute(account.getId() + "_tid_str", kakaoPayReadyVO.getTid());

        return "redirect:" + kakaoPayReadyVO.getNext_redirect_pc_url();
    }

    @GetMapping("/kakaoPaySuccess")
    public String kakaoPaySuccess(@CurrentAccount Account account, @RequestParam("pg_token") String pg_token, Model model, HttpServletRequest request) {
        KakaoPayApprovalVO kakaoPayApprovalVO = kakaoPay.kakaoPayInfo(pg_token);

        HttpSession session = request.getSession();

        List<Learning> learningList = getLearningByIdAndLecture((String) session.getAttribute(account.getId() + "_id_str"),
                (String) session.getAttribute(account.getId() + "_lecture_str"));

        Account newAccount = accountService.setListenLearningAndRemoveCartList(account, learningList);

        model.addAttribute("account", newAccount);
        model.addAttribute("info", kakaoPayApprovalVO);
        model.addAttribute("learningList", learningList);

        return "shop/paysuccess";
    }

    @GetMapping("/kakaoPayCancel")
    public String kakaoPayCancel(@CurrentAccount Account account, Model model, HttpServletRequest request){

        HttpSession session = request.getSession();
        String tid = (String) session.getAttribute(account.getId() + "_tid_str");

        List<Learning> learningList = getLearningByIdAndLecture((String) session.getAttribute(account.getId() + "_id_str"),
                (String) session.getAttribute(account.getId() + "_lecture_str"));

        model.addAttribute("info", kakaoPay.kakaoPayCancel(tid));

        Account newAccount = accountService.removeListenLearning(account, learningList);

        model.addAttribute("account", newAccount);
        model.addAttribute("learningList", learningList);

        return "shop/cancel";
    }

    private List<Learning> getLearningByIdAndLecture(String id, String lecture) {
        List<String> id_split = List.of(id.split(","));
        List<String> lecture_split = List.of(lecture.split(","));

        if(id_split.size() != lecture_split.size()){
            throw new DuplicateKeyException(id);
        }

        return learningService.findLearningByIdAndLecture(id_split, lecture_split);
    }

}
