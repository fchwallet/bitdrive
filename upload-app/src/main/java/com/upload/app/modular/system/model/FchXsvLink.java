package com.upload.app.modular.system.model;

public class FchXsvLink {

    private String fchAddress;

    private String xsvAddress;

    private String addressHash;

    private Integer type;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getFchAddress() {
        return fchAddress;
    }

    public void setFchAddress(String fchAddress) {
        this.fchAddress = fchAddress;
    }

    public String getXsvAddress() {
        return xsvAddress;
    }

    public void setXsvAddress(String xsvAddress) {
        this.xsvAddress = xsvAddress;
    }

    public String getAddressHash() {
        return addressHash;
    }

    public void setAddressHash(String addressHash) {
        this.addressHash = addressHash;
    }
}
