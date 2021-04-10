package com.alemonice.pchat.enums;

/**
 * Created by HB on 2021/4/8 10:53.
 */
public enum DataType {
    /**
     * 用户.
     */
    User(1),
    /**
     * 文本.
     */
    Text(2),
    /**
     * 图片.
     */
    Img(3);

    private int value;

    DataType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
