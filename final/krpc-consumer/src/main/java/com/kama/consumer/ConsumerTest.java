package com.kama.consumer;

import com.kama.client.proxy.ClientProxy;
import com.kama.pojo.User;
import com.kama.service.UserService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassName ConsumerExample
 * @Description RPC Consumer Test Application
 * @Version 1.0.0
 */
@Slf4j
public class ConsumerTest {

    private static final int THREAD_POOL_SIZE = 5;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    public static void main(String[] args) throws InterruptedException {
        ClientProxy clientProxy = new ClientProxy();
        UserService proxy = clientProxy.getProxy(UserService.class);
        for (int i = 0; i < 150; i++) {
            final Integer i1 = i;
            if (i % 50 == 0) {
                // Simulate delay for every 6 requests to demonstrate load balancing
                Thread.sleep(1000);
            }

            // Submit tasks to executor service (thread pool)
            executorService.submit(() -> {
                try {
                    User user = proxy.getUserByUserId(i1);
                    if (user != null) {
                        log.info("Received user from server: {}", user);
                    } else {
                        log.warn("Received null user for userId={}", i1);
                    }

                    Integer id = proxy.insertUserId(User.builder()
                            .id(i1)
                            .userName("User" + i1)
                            .gender(true)
                            .build());

                    if (id != null) {
                        log.info("Inserted user with id={}", id);
                    } else {
                        log.warn("Insert failed, returned null id for userId={}", i1);
                    }
                } catch (Exception e) {
                    log.error("Exception occurred while calling service, userId={}", i1, e);
                }
            });
        }

        // Gracefully shutdown the executor service
        executorService.shutdown();
        clientProxy.close();
    }

}
