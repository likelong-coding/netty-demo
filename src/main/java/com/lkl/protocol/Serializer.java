package com.lkl.protocol;

import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * 序列化算法
 *
 * @author likelong
 * @date 2023/2/8
 */
public interface Serializer {

    /**
     * 序列化：将Java对象转成字节数组
     *
     * @param object 被序列化的对象
     * @param <T>    被序列化对象类型
     * @return 序列化后的字节数组
     */
    <T> byte[] serialize(T object);

    /**
     * 反序列化：将字节数组转成Java对象
     *
     * @param clazz 反序列化的目标类的Class对象
     * @param bytes 被反序列化的字节数组
     * @param <T>   反序列化目标类
     * @return 反序列化后的对象
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);

    enum Algorithm implements Serializer {
        // Java序列化和反序列化
        Java {
            @Override
            public <T> byte[] serialize(T object) {
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(object);
                    return bos.toByteArray();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                // 字节数组转成对象
                try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
                    return (T) ois.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }
        },

        // Json 序列化反序列化
        Json {
            @Override
            public <T> byte[] serialize(T object) {
                Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
                return gson.toJson(object).getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
                // 此处的clazz为具体类型的Class对象，而不是父类Message的
                return gson.fromJson(new String(bytes, StandardCharsets.UTF_8), clazz);
            }
        }
    }

    class ClassCodec implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

        @Override
        public Class<?> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

            try {
                return Class.forName(jsonElement.getAsString());
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }
        }

        @Override
        public JsonElement serialize(Class<?> src, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(src.getName());
        }
    }
}
