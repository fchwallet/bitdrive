package com.upload.app.modular.system.model;

public class ScriptSlpMint {

    private String transactionType;

    private String tokenId;

    private Integer mintBatonVout;

    private String additionalTokenQuantity;

    private String script;

    private Integer status;

    private String minterAddress;

    public String getMinterAddress() {
        return minterAddress;
    }

    public void setMinterAddress(String minterAddress) {
        this.minterAddress = minterAddress;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public Integer getMintBatonVout() {
        return mintBatonVout;
    }

    public void setMintBatonVout(Integer mintBatonVout) {
        this.mintBatonVout = mintBatonVout;
    }

    public String getAdditionalTokenQuantity() {
        return additionalTokenQuantity;
    }

    public void setAdditionalTokenQuantity(String additionalTokenQuantity) {
        this.additionalTokenQuantity = additionalTokenQuantity;
    }
}
