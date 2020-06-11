package com.upload.app.modular.system.model;

public class ScriptSlpSend {

    private String txid;

    private String tokenId;

    private String tokenOutputQuantity;

    private Integer vout;

    private Integer precition;

    private String script;

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getTokenOutputQuantity() {
        return tokenOutputQuantity;
    }

    public void setTokenOutputQuantity(String tokenOutputQuantity) {
        this.tokenOutputQuantity = tokenOutputQuantity;
    }

    public Integer getVout() {
        return vout;
    }

    public void setVout(Integer vout) {
        this.vout = vout;
    }

    public Integer getPrecition() {
        return precition;
    }

    public void setPrecition(Integer precition) {
        this.precition = precition;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
