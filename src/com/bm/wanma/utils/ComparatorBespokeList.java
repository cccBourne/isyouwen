package com.bm.wanma.utils;

import java.util.Comparator;

import com.bm.wanma.entity.MyBespokeOrderBean;
import com.bm.wanma.entity.PileHead;

/**
 * @author cm
 *  预约列表排序
 */
public class ComparatorBespokeList implements Comparator {
	
	private int flag = 0;

	public ComparatorBespokeList() {
	}

	@Override
	public int compare(Object lhs, Object rhs) {
		
		MyBespokeOrderBean bean1 = (MyBespokeOrderBean) lhs;
		MyBespokeOrderBean bean2 = (MyBespokeOrderBean) rhs;
		String state1 = bean1.getBesp_OrderType();//预约单状态 0未支付 1已支付
		String state2 = bean2.getBesp_OrderType();
		try {
			flag = Integer.valueOf(state1).compareTo(
					Integer.valueOf(state2));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return flag;
	}

}
