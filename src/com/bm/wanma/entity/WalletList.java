package com.bm.wanma.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 我的钱包列表
 * cm
 */
public class WalletList implements Serializable {

	/* 余额 */
	private String balance;
	/* 消费 */
	private ArrayList<WalletBean> consumeRecord;
	/* 收益 */
	private ArrayList<WalletBean> earningsRecord;

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public ArrayList<WalletBean> getConsumeRecord() {
		return consumeRecord;
	}

	public void setConsumeRecord(ArrayList<WalletBean> consumeRecord) {
		this.consumeRecord = consumeRecord;
	}

	public ArrayList<WalletBean> getEarningsRecord() {
		return earningsRecord;
	}

	public void setEarningsRecord(ArrayList<WalletBean> earningsRecord) {
		this.earningsRecord = earningsRecord;
	}

}
