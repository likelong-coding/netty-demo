package com.lkl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lkl.protocol.Serializer;

/**
 * 解决Gson 序列化反序列化类对象问题
 * @author likelong
 * @date 2023/2/19
 */
public class TestGson {

    public static void main(String[] args) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new Serializer.ClassCodec()).create();

        // 无法将 类对象 （.class对象） 转换为 json字符串
        System.out.println(gson.toJson(String.class));

    }

}
