package com.upload.app.modular.system.model;

public class GenesisAddress {

    private String txid;

    private String issueAddress;

    private String raiseAddress;

    private Integer issueVout;

    private Integer raiseVout;

    private String raiseTxid;

    public Integer getIssueVout() {
        return issueVout;
    }

    public void setIssueVout(Integer issueVout) {
        this.issueVout = issueVout;
    }

    public Integer getRaiseVout() {
        return raiseVout;
    }

    public void setRaiseVout(Integer raiseVout) {
        this.raiseVout = raiseVout;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getIssueAddress() {
        return issueAddress;
    }

    public void setIssueAddress(String issueAddress) {
        this.issueAddress = issueAddress;
    }

    public String getRaiseAddress() {
        return raiseAddress;
    }

    public void setRaiseAddress(String raiseAddress) {
        this.raiseAddress = raiseAddress;
    }

    public String getRaiseTxid() {
        return raiseTxid;
    }

    public void setRaiseTxid(String raiseTxid) {
        this.raiseTxid = raiseTxid;
    }
}
