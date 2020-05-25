package com.upload.app.core.rpc;

import java.math.BigDecimal;

public class CommonTxOputDto {

    private String[] addresss;

    private String address;

    private BigDecimal amount;

    private String data;

    private String script;

    private Integer type;

    private String metadata;

    char[] chars = "0123456789ABCDEF".toCharArray();

    public CommonTxOputDto(String[] addresss, BigDecimal amount, String metadata, Integer type) {
        this.addresss = addresss;
        this.amount = amount;
        this.metadata = metadata;
        this.type = type;
    }

    public CommonTxOputDto(String[] addresss, BigDecimal amount, Integer type) {
        this.addresss = addresss;
        this.amount = amount;
        this.type = type;
    }

    public CommonTxOputDto(String data, Integer type) {
        this.data = data;
        this.type = type;
    }


    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String[] getAddresss() {
        return addresss;
    }

    public void setAddresss(String[] address) {
        this.addresss = address;
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
