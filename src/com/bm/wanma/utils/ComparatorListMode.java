package com.bm.wanma.utils;

import java.util.Comparator;

import com.bm.wanma.entity.ListModeBean;

public class ComparatorListMode implements Comparator {
	private String type;
	private int flag = 0;

	// electricDistance距离 serviceCharge服务费 commentStart评价
	public ComparatorListMode(String which) {
		this.type = which;
	}

	@Override
	public int compare(Object lhs, Object rhs) {
		ListModeBean bean1 = (ListModeBean) lhs;
		ListModeBean bean2 = (ListModeBean) rhs;
		String distance1 = bean1.getElectricDistance();
		String distance2 = bean2.getElectricDistance();
		String price1 = bean1.getServiceCharge();
		String price2 = bean2.getServiceCharge();
		String score1 = bean1.getCommentStart();
		String score2 = bean2.getCommentStart();
		
		if(Tools.isEmptyString(price1)){
			price1 = "0.00";
		}
		if(Tools.isEmptyString(price2)){
			price2 = "0.00";
		}
		if(Tools.isEmptyString(score1)){
			score1 = "0.00";
		}
		if(Tools.isEmptyString(score2)){
			score2 = "0.00";
		}
	/*	BigDecimal b_distance1 = new BigDecimal(distance1);
		BigDecimal b_distance2 = new BigDecimal(distance2);
		BigDecimal b_price1 = new BigDecimal(price1);
		BigDecimal b_price2 = new BigDecimal(price2);
		BigDecimal b_score1 = new BigDecimal(score1);
		BigDecimal b_score2 = new BigDecimal(score2);*/
		//距离升序，价格升序，评价降序
		if (type.equals("distance")) {
			// 距离相等时，再按评价--价格排序
			flag = Float.valueOf(distance1).compareTo(
					Float.valueOf(distance2));
			if(0 == flag){
				flag = Float.valueOf(score2).compareTo(
						Float.valueOf(score1));
				if(0 == flag){
					flag = Float.valueOf(price1).compareTo(
							Float.valueOf(price2));
				}
			}
		}else if (type.equals("price")) {
			// 价格相等时，再按距离--评价排序
			flag = Float.valueOf(price1).compareTo(
					Float.valueOf(price2));
			if(0 == flag){
				flag = Float.valueOf(distance1).compareTo(
						Float.valueOf(distance2));
				if(0 == flag){
					flag = Float.valueOf(score2).compareTo(
							Float.valueOf(score1));
				}
			}
		}else if (type.equals("score")) {
			// 评分相等时，再按距离--价格排序
			flag = Float.valueOf(score2).compareTo(
					Float.valueOf(score1));
			if(0 == flag){
				flag = Float.valueOf(distance1).compareTo(
						Float.valueOf(distance2));
				if(0 == flag){
					flag = Float.valueOf(price1).compareTo(
							Float.valueOf(price2));
				}
			}
		}
		/*//距离升序，价格升序，评价降序
		if (type.equals("distance")) {
			// 距离相等时，再按评价--价格排序
			flag = b_distance1.compareTo(
					b_distance2);
		}else if (type.equals("price")) {
			// 价格相等时，再按距离--评价排序
			flag = b_price1.compareTo(
					b_price2);
			
		}else if (type.equals("score")) {
			// 评分相等时，再按距离--价格排序
			flag = b_score2.compareTo(
					b_score1);
			
		}*/
		
		return flag;
	}

}
