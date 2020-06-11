package com.upload.app.modular.system.model;

import java.math.BigInteger;

public class ScriptTokenLink {

    private String tokenId;

    private String script;

    private BigInteger token;

    private String txid;

    private Integer vout;

    private String fromScript;

    private Integer status;


    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public BigInteger getToken() {
        return token;
    }

    public void setToken(BigInteger token) {
        this.token = token;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public Integer getVout() {
        return vout;
    }

    public void setVout(Integer vout) {
        this.vout = vout;
    }

    public String getFromScript() {
        return fromScript;
    }

    public void setFromScript(String fromScript) {
        this.fromScript = fromScript;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
