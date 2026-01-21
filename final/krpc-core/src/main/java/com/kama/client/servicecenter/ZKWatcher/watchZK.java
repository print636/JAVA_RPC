package com.kama.client.servicecenter.ZKWatcher;

import com.kama.client.cache.ServiceCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;


/**
 * @ClassName watchZK
 * @Description 节点监听
 * 
 * 
 * @Version 1.0.0
 */
@Slf4j
public class watchZK {
    // curator 提供的zookeeper客户�?
    private CuratorFramework client;
    //本地缓存
    ServiceCache cache;


    public watchZK(CuratorFramework client, ServiceCache cache) {
        this.client = client;
        this.cache = cache;
    }

    /**
     * 监听当前节点和子节点�?更新，创建，删除
     *
     * @param path
     */
    public void watchToUpdate(String path) throws InterruptedException {
        CuratorCache curatorCache = CuratorCache.build(client, "/");
        curatorCache.listenable().addListener(new CuratorCacheListener() {
            @Override
            public void event(Type type, ChildData childData, ChildData childData1) {
                // 第一个参数：事件类型（枚举）
                // 第二个参数：节点更新前的状态、数�?
                // 第三个参数：节点更新后的状态、数�?
                // 创建节点时：节点刚被创建，不存在 更新前节�?，所以第二个参数�?null
                // 删除节点时：节点被删除，不存�?更新后节�?，所以第三个参数�?null
                // 节点创建时没有赋予�?create /curator/app1 只创建节点，在这种情况下，更新前节点�?data �?null，获取不到更新前节点的数�?
                switch (type.name()) {
                    case "NODE_CREATED": // 监听器第一次执行时节点存在也会触发次事�?
                        String[] pathList = pasrePath(childData1);
                        if (pathList.length <= 2) break;
                        else {
                            String serviceName = pathList[1];
                            String address = pathList[2];
                            //将新注册的服务加入到本地缓存�?
                            cache.addServiceToCache(serviceName, address);
                            log.info("节点创建：服务名�?{} 地址 {}", serviceName, address);
                        }
                        break;
                    case "NODE_CHANGED": // 节点更新
                        if (childData.getData() != null) {
                            log.debug("修改前的数据: {}", new String(childData.getData()));
                        } else {
                            log.debug("节点第一次赋�?");
                        }
                        String[] oldPathList = pasrePath(childData);
                        String[] newPathList = pasrePath(childData1);
                        cache.replaceServiceAddress(oldPathList[1], oldPathList[2], newPathList[2]);
                        log.info("节点更新：服务名�?{} 地址�?{} 更新�?{}", oldPathList[1], oldPathList[2], newPathList[2]);
                        break;
                    case "NODE_DELETED": // 节点删除
                        String[] pathList_d = pasrePath(childData);
                        if (pathList_d.length <= 2) break;
                        else {
                            String serviceName = pathList_d[1];
                            String address = pathList_d[2];
                            //将新注册的服务加入到本地缓存�?
                            cache.delete(serviceName, address);
                            log.info("节点删除：服务名�?{} 地址 {}", serviceName, address);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        //开启监�?
        curatorCache.start();
    }

    //解析节点对应地址
    public String[] pasrePath(ChildData childData) {
        //获取更新的节点的路径
        String path = new String(childData.getPath());
        log.info("节点路径:{}",path);
        //按照格式 ，读�?
        return path.split("/");
    }
}
