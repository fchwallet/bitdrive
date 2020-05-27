package com.upload.app.core.exception;

public class XsvException {

    public static class InvalidBitcoinAddressException extends BaseException {
        private static final long serialVersionUID = 3555714415375055302L;
        public InvalidBitcoinAddressException(Integer code, String msg) {
            super(code, msg);
        }
    }


}
