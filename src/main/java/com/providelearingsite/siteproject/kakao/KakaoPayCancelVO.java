package com.providelearingsite.siteproject.kakao;

import lombok.Data;

import java.time.LocalDateTime;

/*
aid	String	요청 고유 번호
tid	String	결제 고유 번호, 10자
cid	String	가맹점 코드, 20자
status	String	결제 상태
partner_order_id	String	가맹점 주문번호, 최대 100자
partner_user_id	String	가맹점 회원 id, 최대 100자
payment_method_type	String	결제 수단, CARD 또는 MONEY 중 하나
amount	Amount	결제 금액 정보
canceled_amount	CanceledAmount	이번 요청으로 취소된 금액
cancel_available_amount	CancelAvailableAmount	남은 취소 가능 금액
item_name	String	상품 이름, 최대 100자
item_code	String	상품 코드, 최대 100자
quantity	Integer	상품 수량
created_at	Datetime	결제 준비 요청 시각
approved_at	Datetime	결제 승인 시각
canceled_at	Datetime	결제 취소 시각
payload	String	취소 요청 시 전달한 값

====== status ======
READY	결제 요청
SEND_TMS	결제 요청 메시지(TMS) 발송 완료
OPEN_PAYMENT	사용자가 카카오페이 결제 화면 진입
SELECT_METHOD	결제 수단 선택, 인증 완료
ARS_WAITING	ARS 인증 진행 중
AUTH_PASSWORD	비밀번호 인증 완료
ISSUED_SID	SID 발급 완료
정기 결제 시 SID만 발급 한 경우
SUCCESS_PAYMENT	결제 완료
PART_CANCEL_PAYMENT	부분 취소
CANCEL_PAYMENT	결제된 금액 모두 취소
부분 취소 여러 번으로 모두 취소된 경우 포함
FAIL_AUTH_PASSWORD	사용자 비밀번호 인증 실패
QUIT_PAYMENT	사용자가 결제 중단
FAIL_PAYMENT	결제 승인 실패
 */

@Data
public class KakaoPayCancelVO {

    private String aid, tid, cid, status, partner_order_id, partner_user_id, payment_method_type
            , item_name, item_code, payload;
    private AmountVO amount;
    private CanceledAmount canceled_amount;
    private CancelAvailableAmount cancel_available_amount;
    private Integer quantity;
    private LocalDateTime created_at, approved_at, canceled_at;
}
