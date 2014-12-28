package com.smallcat.common.util;

import com.smallcat.common.bean.PaymentBean;

public class PaymentUtil {
	public static String buildHmac(PaymentBean paymentBean, String keyValue){
		StringBuilder sValue = new StringBuilder();
		sValue.append(paymentBean.getP0_Cmd());
		sValue.append(paymentBean.getP1_MerId());
		sValue.append(paymentBean.getP2_Order());
		sValue.append(paymentBean.getP3_Amt());
		sValue.append(paymentBean.getP4_Cur());
		sValue.append(paymentBean.getP5_Pid());
		sValue.append(paymentBean.getP6_Pcat());
		sValue.append(paymentBean.getP7_Pdesc());
		sValue.append(paymentBean.getP8_Url());
		sValue.append(paymentBean.getP9_SAF());
		sValue.append(paymentBean.getPa_MP());
		sValue.append(paymentBean.getPd_FrpId());
		sValue.append(paymentBean.getPr_NeedResponse());
		
		return sValue.toString();
	}
}
