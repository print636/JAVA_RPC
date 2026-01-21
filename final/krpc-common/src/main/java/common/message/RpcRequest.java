package common.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName RpcRequest
 * @Description 定义请求消息格式
 * 
 * 
 * @Version 1.0.0
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RpcRequest implements Serializable {
    //v6新增:请求类型
    private RequestType type=RequestType.NORMAL;
    //接口名、方法名、参数列表参数类�?
    private String interfaceName;

    private String methodName;

    private Object[] params;

    private Class<?>[] paramsType;
    public static RpcRequest heartBeat() {
        return RpcRequest.builder().type(RequestType.HEARTBEAT).build();
    }

}
