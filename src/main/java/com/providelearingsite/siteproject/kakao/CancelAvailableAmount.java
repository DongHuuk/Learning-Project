package com.providelearingsite.siteproject.kakao;

/*
total	Integer	전체 취소 가능 금액
tax_free	Integer	취소 가능한 비과세 금액
vat	Integer	취소 가능한 부가세 금액
point	Integer	취소 가능한 포인트 금액
*포인트 금액 필드 반영 일정
- 테스트: 2020년 6월 12일
- 운영: 2020년 7월 27일
discount	Integer	취소 가능한 할인 금액
 */

import lombok.Data;

@Data
public class CancelAvailableAmount {

    private Integer total, tax_free, vat, point, discount;

}
