package com.lkl.server.session;

/**
 * @author Mr.Li
 */
public abstract class GroupSessionFactory {

    private static GroupSession session = new GroupSessionMemoryImpl();

    public static GroupSession getGroupSession() {
        return session;
    }
}
