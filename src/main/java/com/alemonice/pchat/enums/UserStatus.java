package com.alemonice.pchat.enums;

/**
 * Created by HB on 2021/4/10 11:31.
 */
public enum UserStatus {
    /**
     * 在线.
     */
    online(1),
    /**
     * 离线.
     */
    reave(2);

    private int value;

    UserStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
