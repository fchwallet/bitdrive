package com.xyz.upload.app.modular.system.model;

public class Create {

    private Integer id;

    private String fchAddress;

    private String txid;

    private String driveId;

    private Integer encrypt;

    private String encryptedPwd;

    private String data;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFchAddress() {
        return fchAddress;
    }

    public void setFchAddress(String fchAddress) {
        this.fchAddress = fchAddress;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(String driveId) {
        this.driveId = driveId;
    }

    public Integer getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(Integer encrypt) {
        this.encrypt = encrypt;
    }

    public String getEncryptedPwd() {
        return encryptedPwd;
    }

    public void setEncryptedPwd(String encryptedPwd) {
        this.encryptedPwd = encryptedPwd;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
