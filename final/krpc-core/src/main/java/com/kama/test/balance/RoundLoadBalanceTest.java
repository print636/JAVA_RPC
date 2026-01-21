package com.kama.test.balance;

import com.kama.client.servicecenter.balance.impl.RoundLoadBalance;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @ClassName RoundLoadBalanceTest
 * @Description 轮询测试�?
 * 
 * 
 * @Version 1.0.0
 */
public class RoundLoadBalanceTest {

    private RoundLoadBalance loadBalance;

    @Before
    public void setUp() {
        // 在每个测试前初始化负载均衡器
        loadBalance = new RoundLoadBalance();
    }

    @Test
    public void testBalance_WithNonEmptyList() {
        // 准备一个非空的地址列表
        List<String> addressList = Arrays.asList("server1", "server2", "server3");

        // 执行 balance 方法并获取返回的服务�?
        String selectedServer = loadBalance.balance(addressList);

        // 确保选择的服务器在列表中
        assertTrue(addressList.contains(selectedServer));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBalance_WithEmptyList() {
        // 测试空的节点列表，应该抛�?IllegalArgumentException 异常
        loadBalance.balance(Arrays.asList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBalance_WithNullList() {
        // 测试 null 的节点列表，应该抛出 IllegalArgumentException 异常
        loadBalance.balance(null);
    }

    @Test
    public void testAddNode() {
        // 测试添加节点到负载均衡器
        loadBalance.addNode("server4");

        // 确保新添加的节点在负载均衡器�?
        List<String> addressList = Arrays.asList("server1", "server2", "server3", "server4");
        String selectedServer = loadBalance.balance(addressList);
        assertTrue(addressList.contains(selectedServer));
    }

    @Test
    public void testDelNode() {
        // 测试从负载均衡器中移除节�?
        loadBalance.addNode("server4");
        loadBalance.delNode("server4");

        // 确保删除后的节点不再在负载均衡器�?
        List<String> addressList = Arrays.asList("server1", "server2", "server3");
        String selectedServer = loadBalance.balance(addressList);
        assertFalse(addressList.contains("server4"));
    }

    @Test
    public void testBalance_RoundRobin() {
        // 测试负载均衡是否按轮询顺序选择服务�?
        List<String> addressList = Arrays.asList("server1", "server2", "server3");

        // 轮询选择服务�?
        String firstSelection = loadBalance.balance(addressList);
        String secondSelection = loadBalance.balance(addressList);
        String thirdSelection = loadBalance.balance(addressList);
        String fourthSelection = loadBalance.balance(addressList);  // Should loop back to first

        // 确保选择的服务器是轮询顺序的
        assertNotEquals(firstSelection, secondSelection);
        assertNotEquals(secondSelection, thirdSelection);
        assertNotEquals(thirdSelection, fourthSelection);
        assertEquals(firstSelection, fourthSelection);  // Should be back to the first
    }
}
