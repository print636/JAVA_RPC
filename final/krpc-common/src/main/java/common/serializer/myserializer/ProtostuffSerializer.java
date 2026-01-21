package common.serializer.myserializer;


import com.kama.pojo.User;
import common.exception.SerializeException;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
/**
 * @ClassName ProtostuffSerializer
 * @Description protostuff序列�?
 * 
 * 
 * @Version 1.0.0
 */
public class ProtostuffSerializer implements Serializer {

    @Override
    public byte[] serialize(Object obj) {
        // 检�?null 对象
        if (obj == null) {
            throw new IllegalArgumentException("Cannot serialize null object");
        }
        // 获取对象�?schema
        Schema schema = RuntimeSchema.getSchema(obj.getClass());

        // 使用 LinkedBuffer 来创建缓冲区（默认大�?1024�?
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        // 序列化对象为字节数组
        byte[] bytes;
        try {
            bytes = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }
        return bytes;
    }

    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("Cannot deserialize null or empty byte array");
        }

        // 根据 messageType 来决定反序列化的类，这里假设 `messageType` 是类的标识符
        Class<?> clazz = getClassForMessageType(messageType);

        // 获取对象�?schema
        Schema schema = RuntimeSchema.getSchema(clazz);

        // 创建一个空的对象实�?
        Object obj;
        try {
            obj = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new SerializeException("Deserialization failed due to reflection issues");
        }

        // 反序列化字节数组为对�?
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }

    @Override
    public int getType() {
        return 4;
    }

    // 用于根据 messageType 获取对应的类
    private Class<?> getClassForMessageType(int messageType) {
        if (messageType == 1) {
            return User.class;  // 假设我们在此反序列化�?User �?
        } else {
            throw new SerializeException("Unknown message type: " + messageType);
        }
    }

    @Override
    public String toString() {
        return "Protostuff";
    }
}
