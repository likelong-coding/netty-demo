package com.lkl.message;

import lombok.Data;
import lombok.ToString;

/**
 * @author likelong
 */
@Data
@ToString(callSuper = true)
public class LoginRequestMessage extends Message {
    private String username;
    private String password;
    private String nickName;

    public LoginRequestMessage() {
    }

    public LoginRequestMessage(String username, String password, String nickName) {
        this.username = username;
        this.password = password;
        this.nickName = nickName;
    }

    @Override
    public int getMessageType() {
        return LoginRequestMessage;
    }
}
