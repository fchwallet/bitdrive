package com.upload.app.modular.system.service;

public interface BlockchainPaymentService {

    Boolean payment(String fchAddress, Integer type, String methodName) throws Exception;

}
