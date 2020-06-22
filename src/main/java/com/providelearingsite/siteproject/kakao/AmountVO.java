package com.providelearingsite.siteproject.kakao;

/*
total	전체 결제 금액	Integer
tax_free	비과세 금액	Integer
vat	부가세 금액	Integer
point	사용한 포인트 금액	Integer
discount	할인 금액	Integer
 */

import lombok.Data;

@Data
public class AmountVO {

    private Integer total, tax_free, vat, point, discount;

}
