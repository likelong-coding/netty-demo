package com.lkl.server.service;

/**
 * @author likelong
 * @date 2023/2/19
 */
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String name) {

        int i = 1 / 0;
        return "hello, " + name;
    }
}
