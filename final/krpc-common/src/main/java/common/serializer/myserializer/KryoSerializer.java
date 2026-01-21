package common.serializer.myserializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.kama.pojo.User;
import common.exception.SerializeException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


/**
 * @ClassName KryoSerializer
 * @Description kryo序列�?
 * 
 * 
 * @Version 1.0.0
 */

public class KryoSerializer implements Serializer {
    private Kryo kryo;

    public KryoSerializer() {
        this.kryo = new Kryo();
    }

    @Override
    public byte[] serialize(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Cannot serialize null object");
        }

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)) {

            kryo.writeObject(output, obj); // 使用 Kryo 写入对象
            return output.toBytes(); // 返回字节数组

        } catch (Exception e) {
            throw new SerializeException("Serialization failed");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("Cannot deserialize null or empty byte array");
        }

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {

            // 根据 messageType 来反序列化不同的�?
            Class<?> clazz = getClassForMessageType(messageType);
            return kryo.readObject(input, clazz); // 使用 Kryo 反序列化对象

        } catch (Exception e) {
            throw new SerializeException("Deserialization failed");
        }
    }

    @Override
    public int getType() {
        return 2;
    }

    private Class<?> getClassForMessageType(int messageType) {
        if (messageType == 1) {
            return User.class;  // 假设我们在此反序列化�?User �?
        } else {
            throw new SerializeException("Unknown message type: " + messageType);
        }
    }

    @Override
    public String toString() {
        return "Kryo";
    }
}
