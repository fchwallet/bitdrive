package com.upload.app.core.exception;

public class ParamException {


    public static class ParamLengthException extends BaseException {
        private static final long serialVersionUID = 355571115375155302L;
        public ParamLengthException(Integer code, String msg) {
            super(code, msg);
        }
    }

}
