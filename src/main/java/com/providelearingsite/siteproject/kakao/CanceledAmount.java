package com.providelearingsite.siteproject.kakao;

/*
total	Integer	전체 취소 금액
tax_free	Integer	취소된 비과세 금액
vat	Integer	취소된 부가세 금액
point	Integer	취소된 포인트 금액
*포인트 금액 필드 반영 일정
- 테스트: 2020년 6월 12일
- 운영: 2020년 7월 27일
discount	Integer	취소된 할인 금액
 */

import lombok.Data;

@Data
public class CanceledAmount {

    private Integer total, tax_free, vat, point, discount;

}
