package com.upload.app.core.rpc;

import java.math.BigDecimal;

public class TxOutputDto {

	private String address;

	private BigDecimal amount;

	private String data;

	private String content;

	private String opreturn;

	char[] chars = "0123456789ABCDEF".toCharArray();

	public TxOutputDto(String data, String content) {
		this.data = data;
		this.content = content;
	}

	public TxOutputDto(String opreturn) {
		this.opreturn = opreturn;
	}

	public TxOutputDto(String address, BigDecimal amount) {
		this.address = address;
		this.amount = amount;
	}

	public String data() {
		return data;
	}

	public Object content() {
		if (data.equals("data")) {
			byte[] bs = String.valueOf(content).getBytes();
			int bit;
			StringBuilder builder = new StringBuilder("");
			for (int i = 0; i < bs.length; i++) {
				bit = (bs[i] & 0x0f0) >> 4;
				builder.append(chars[bit]);
				bit = bs[i] & 0x0f;
				builder.append(chars[bit]);
			}
			return builder.toString().trim();
		} else {
			return Double.valueOf(content);
		}

	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getOpreturn() {
		return opreturn;
	}

	public void setOpreturn(String opreturn) {
		this.opreturn = opreturn;
	}
}
