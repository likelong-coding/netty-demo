package com.lkl.server.session;

/**
 * @author Mr.Li
 */
public abstract class SessionFactory {

    private static Session session = new SessionMemoryImpl();

    public static Session getSession() {
        return session;
    }
}
