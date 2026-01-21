package com.kama.client.servicecenter.balance.impl;

import com.kama.client.servicecenter.balance.LoadBalance;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @ClassName ConsistencyHashBalance
 * @Description Consistent Hash Load Balancing Implementation
 * @Version 1.0.0
 */
@Slf4j
public class ConsistencyHashBalance implements LoadBalance {

    // 虚拟节点的个�?
    private static final int VIRTUAL_NUM = 5;

    // 虚拟节点分配，key是hash值，value是虚拟节点服务器名称
    private SortedMap<Integer, String> shards = new TreeMap<Integer,String>();

    // 真实节点列表
    private List<String> realNodes = new LinkedList<>();

    // 获取虚拟节点的个�?
    public static int getVirtualNum() {
        return VIRTUAL_NUM;
    }

    // 初始化虚拟节�?
    public void init(List<String> serviceList) {
        for (String server : serviceList) {
            realNodes.add(server);
            log.info("Real node [{}] added", server);
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = server + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.put(hash, virtualNode);
                log.info("Virtual node [{}] hash:{} added", virtualNode, hash);
            }
        }
    }

    /**
     * 获取被分配的节点�?
     *
     * @param node 请求的节点（通常是请求的唯一标识符）
     * @return 负责该请求的真实节点名称
     */
    public String getServer(String node, List<String> serviceList) {
        if (shards.isEmpty()) {
            init(serviceList);  // 初始化，如果shards为空
        }

        int hash = getHash(node);
        Integer key = null;

        SortedMap<Integer, String> subMap = shards.tailMap(hash);
        if (subMap.isEmpty()) {
            key = shards.firstKey();  // 如果没有大于该hash的节点，则返回最小的hash�?
        } else {
            key = subMap.firstKey();
        }

        String virtualNode = shards.get(key);
        return virtualNode.substring(0, virtualNode.indexOf("&&"));
    }

    /**
     * 添加节点
     *
     * @param node 新加入的节点
     */
    public void addNode(String node) {
        if (!realNodes.contains(node)) {
            realNodes.add(node);
            log.info("真实节点[{}] 上线添加", node);
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = node + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.put(hash, virtualNode);
                log.info("Virtual node [{}] hash:{} added", virtualNode, hash);
            }
        }
    }

    /**
     * 删除节点
     *
     * @param node 被移除的节点
     */
    public void delNode(String node) {
        if (realNodes.contains(node)) {
            realNodes.remove(node);
            log.info("真实节点[{}] 下线移除", node);
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = node + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.remove(hash);
                log.info("虚拟节点[{}] hash:{}，被移除", virtualNode, hash);
            }
        }
    }

    /**
     * FNV1_32_HASH算法
     */
    private static int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++)
            hash = (hash ^ str.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        // 如果算出来的值为负数则取其绝对�?
        if (hash < 0)
            hash = Math.abs(hash);
        return hash;
    }

    @Override
    public String balance(List<String> addressList) {
        // 如果 addressList 为空�?null，抛�?IllegalArgumentException
        if (addressList == null || addressList.isEmpty()) {
            throw new IllegalArgumentException("Address list cannot be null or empty");
        }

        // 使用UUID作为请求的唯一标识符来进行一致性哈�?
        String random = UUID.randomUUID().toString();
        return getServer(random, addressList);
    }
    public SortedMap<Integer, String> getShards() {
        return shards;
    }

    public List<String> getRealNodes() {
        return realNodes;
    }
    @Override
    public String toString() {
        return "ConsistencyHash";
    }
}

