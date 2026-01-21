package com.kama.test.serializer;


import common.exception.SerializeException;
import common.serializer.myserializer.HessianSerializer;
import org.junit.Test;

import static org.junit.Assert.*;

public class HessianSerializerTest {

    private HessianSerializer serializer = new HessianSerializer();

    @Test
    public void testSerializeAndDeserialize() {
        // 创建一个测试对�?
        String original = "Hello, Hessian!";

        // 序列�?
        byte[] serialized = serializer.serialize(original);
        assertNotNull("序列化结果不应为 null", serialized);

        // 反序列化
        Object deserialized = serializer.deserialize(serialized, 3);
        assertNotNull("反序列化结果不应�?null", deserialized);

        // 校验反序列化的结�?
        assertEquals("反序列化的对象应该与原对象相�?, original, deserialized);
    }

    @Test
    public void testDeserializeWithInvalidData() {
        byte[] invalidData = new byte[]{1, 2, 3}; // 假数�?

        // 测试无效数据反序列化
        try {
            serializer.deserialize(invalidData, 3);
            fail("反序列化时应抛出异常");
        } catch (SerializeException e) {
            assertEquals("Deserialization failed", e.getMessage());
        }
    }
}

