package com.upload.app.core.rpc;

public class TxOuputDataDto {
    private String data;

    private String content;

    char[] chars = "0123456789ABCDEF".toCharArray();

    public TxOuputDataDto(String data, String content) {
        this.data = data;
        this.content = content;
    }

    public String data() {
        return data;
    }

    public Object content() {
        if (data.equals("data")) {
            byte[] bs = String.valueOf(content).getBytes();
            int bit;
            StringBuilder builder = new StringBuilder("");
            for (int i = 0; i < bs.length; i++) {
                bit = (bs[i] & 0x0f0) >> 4;
                builder.append(chars[bit]);
                bit = bs[i] & 0x0f;
                builder.append(chars[bit]);
            }
            return builder.toString().trim();
        } else {
            return Double.valueOf(content);
        }

    }

}
