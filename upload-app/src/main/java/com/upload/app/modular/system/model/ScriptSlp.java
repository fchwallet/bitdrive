package com.upload.app.modular.system.model;

public class ScriptSlp {

    private String transactionType;

    private String tokenTicker;

    private String tokenName;

    private String tokenDocumentUrl;

    private String tokenDocumentHash;

    private Integer tokenDecimal;

    private Integer mintBatonVout;

    private String initialTokenMintQuantity;

    private String txid;

    private String originalScrpit;                     // 发行脚本

    private String initIssueAddress;                    // 增发权限地址

    public String getOriginalScrpit() {
        return originalScrpit;
    }

    public void setOriginalScrpit(String originalScrpit) {
        this.originalScrpit = originalScrpit;
    }

    public String getInitIssueAddress() {
        return initIssueAddress;
    }

    public void setInitIssueAddress(String initIssueAddress) {
        this.initIssueAddress = initIssueAddress;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTokenTicker() {
        return tokenTicker;
    }

    public void setTokenTicker(String tokenTicker) {
        this.tokenTicker = tokenTicker;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public String getTokenDocumentHash() {
        return tokenDocumentHash;
    }

    public void setTokenDocumentHash(String tokenDocumentHash) {
        this.tokenDocumentHash = tokenDocumentHash;
    }

    public Integer getTokenDecimal() {
        return tokenDecimal;
    }

    public void setTokenDecimal(Integer tokenDecimal) {
        this.tokenDecimal = tokenDecimal;
    }

    public Integer getMintBatonVout() {
        return mintBatonVout;
    }

    public void setMintBatonVout(Integer mintBatonVout) {
        this.mintBatonVout = mintBatonVout;
    }

    public String getTokenDocumentUrl() {
        return tokenDocumentUrl;
    }

    public void setTokenDocumentUrl(String tokenDocumentUrl) {
        this.tokenDocumentUrl = tokenDocumentUrl;
    }

    public String getInitialTokenMintQuantity() {
        return initialTokenMintQuantity;
    }

    public void setInitialTokenMintQuantity(String initialTokenMintQuantity) {
        this.initialTokenMintQuantity = initialTokenMintQuantity;
    }
}
