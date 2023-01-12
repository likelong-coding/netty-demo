package com.lkl.message;

/**
 * @author likelong
 */
public class PongMessage extends Message {
    @Override
    public int getMessageType() {
        return PongMessage;
    }
}
