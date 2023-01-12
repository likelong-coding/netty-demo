package com.lkl.message;

/**
 * @author likelong
 */
public class PingMessage extends Message {
    @Override
    public int getMessageType() {
        return PingMessage;
    }
}
