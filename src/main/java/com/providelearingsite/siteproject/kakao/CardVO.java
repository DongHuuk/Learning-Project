package com.providelearingsite.siteproject.kakao;

/*
purchase_corp	String	매입 카드사 한글명
purchase_corp_code	String	매입 카드사 코드
issuer_corp	String	카드 발급사 한글명
issuer_corp_code	String	카드 발급사 코드
kakaopay_purchase_corp	String	카카오페이 매입사명
kakaopay_purchase_corp_code	String	카카오페이 매입사 코드
kakaopay_issuer_corp	String	카카오페이 발급사명
kakaopay_issuer_corp_code	String	카카오페이 발급사 코드
bin	String	카드 BIN
card_type	String	카드 타입
install_month	String	할부 개월 수
approved_id	String	카드사 승인번호
card_mid	String	카드사 가맹점 번호
interest_free_install	String	무이자할부 여부(Y/N)
card_item_code	String	카드 상품 코드
 */

import lombok.Data;

@Data
public class CardVO {

    private String purchase_corp, purchase_corp_code;
    private String issuer_corp, issuer_corp_code;
    private String bin, card_type, install_month, approved_id, card_mid;
    private String interest_free_install, card_item_code;

}
