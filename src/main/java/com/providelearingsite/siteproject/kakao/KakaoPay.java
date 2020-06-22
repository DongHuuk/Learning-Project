package com.providelearingsite.siteproject.kakao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@Service
public class KakaoPay {

    private static final String KAKAO_HOST = "https://kapi.kakao.com";
    private static final String ADMINKEY = "5f49cfc6efa5808381759f160fe17fbf";
    private static final String HOST = "http://localhost:8080";

    //kakao 응답정보
    private KakaoPayReadyVO kakaoPayReadyVO;
    private KakaoPayApprovalVO kakaoPayApprovalVO;
    private KakaoPayCancelVO kakaoPayCancelVO;

    public String kakaoPayReady(){
        RestTemplate restTemplate = new RestTemplate();

        //서버로 요청할 header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + ADMINKEY);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=utf-8");

        //서버로 요청할 body
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME"); // Test Code
        params.add("partner_order_id", "1001"); // 가맹점 주문번호
        params.add("partner_user_id", "흑우냥이"); //가맹점 회원
        params.add("item_name", "갤럭시S9"); // 상품명
        params.add("quantity", "1"); // 상품 수량
        params.add("total_amount", "2100"); // 상품 총액
        params.add("tax_free_amount", "100"); //상품 비과세 금액
        params.add("approval_url", HOST + "/kakaoPaySuccess"); //성공시 redirect URL
        params.add("cancel_url", HOST + "/kakaoPayCancel"); //취소시 redirect URL
        params.add("fail_url", HOST + "/kakaoPaySuccessFail"); //실패시 redirect URL

        //header와 body를 합치는 부분
        HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<MultiValueMap<String, String>>(params, headers);

        try{
            kakaoPayReadyVO = restTemplate.postForObject(new URI(KAKAO_HOST + "/v1/payment/ready"), body, KakaoPayReadyVO.class);

            log.info("" + kakaoPayReadyVO);

            return kakaoPayReadyVO.getNext_redirect_pc_url();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return "/pay"; //실패시 리턴할 페이지
    }

    public KakaoPayApprovalVO kakaoPayInfo(String pg_token){
        RestTemplate restTemplate = new RestTemplate();

        //서버로 요청할 header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + ADMINKEY);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=utf-8");

        //서버로 요청할 body
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME"); // Test Code
        params.add("tid", kakaoPayReadyVO.getTid());
        params.add("partner_order_id", "1001"); // 가맹점 주문번호
        params.add("partner_user_id", "흑우냥이"); //가맹점 회원
        params.add("pg_token", pg_token);

        HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<MultiValueMap<String, String>>(params, headers);

        try{
            kakaoPayApprovalVO = restTemplate.postForObject(new URI(KAKAO_HOST + "/v1/payment/approve"), body, KakaoPayApprovalVO.class);

            return kakaoPayApprovalVO;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return null; // 실패시 리턴
    }

    public KakaoPayCancelVO kakaoPayCancel(){
        RestTemplate restTemplate = new RestTemplate();

        //서버로 요청할 header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + ADMINKEY);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME"); // Test Code
        params.add("tid", kakaoPayReadyVO.getTid()); // tid
        params.add("cancel_amount", "2100"); // 취소금액
        params.add("cancel_tax_free_amount", "100");//취소금액 세금

        HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<>(params, headers);

        try{
            kakaoPayCancelVO = restTemplate.postForObject(new URI(KAKAO_HOST + "/v1/payment/cancel"), body, KakaoPayCancelVO.class);

            return kakaoPayCancelVO;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return null;
    }

}
