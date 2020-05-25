package com.upload.app.core.rpc;

import java.math.BigDecimal;

public class ContractTxOputDto {

    private String[] address;

    private BigDecimal amount;

    private String data;

    private String script;

    private Integer type;

    char[] chars = "0123456789ABCDEF".toCharArray();


    public ContractTxOputDto(String[] address, BigDecimal amount, String script, Integer type) {
        this.address = address;
        this.amount = amount;
        this.script = script;
        this.type = type;
    }

    public ContractTxOputDto(String[] address, BigDecimal amount, Integer type) {
        this.address = address;
        this.amount = amount;
        this.type = type;
    }

    public ContractTxOputDto(String data, Integer type) {
        this.data = data;
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String[] getAddress() {
        return address;
    }

    public void setAddress(String[] address) {
        this.address = address;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public char[] getChars() {
        return chars;
    }

    public void setChars(char[] chars) {
        this.chars = chars;
    }
}
