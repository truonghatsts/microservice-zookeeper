package com.example.helloworld.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hatruong on 6/17/2015.
 */
public class ZookeeperUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperUtils.class);

    private static RetryPolicy retryPolicy = new RetryOneTime(1);
    private static CuratorFramework client = null;

    public static CuratorFramework getClient(ZookeeperConfiguration configuration) {

        if(client==null) {
            client = CuratorFrameworkFactory.newClient(configuration.getConnection(),retryPolicy);
            client.start();
        }
        return client;
    }

    public static void register(ZookeeperConfiguration configuration) {

        LOGGER.info(">>> Before createing node");
        try {
            getClient(configuration).create().withMode(CreateMode.EPHEMERAL).forPath(
                    configuration.getRootNode() + "/" + configuration.getNodeName(),
                    configuration.getNodeData().getBytes());
        } catch (Exception e) {
            LOGGER.error(">>> Error while registering to zookeeper with the exception: " + e);
        }
        LOGGER.info(">>> After createing node");
    }




}
