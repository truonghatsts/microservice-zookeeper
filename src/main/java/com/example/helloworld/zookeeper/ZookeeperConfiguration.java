package com.example.helloworld.zookeeper;

/**
 * Created by hatruong on 6/17/2015.
 */
public class ZookeeperConfiguration {

    private String connection;
    private String rootNode;
    private String nodeName;
    private String nodeData;

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public String getRootNode() {
        return rootNode;
    }

    public void setRootNode(String rootNode) {
        this.rootNode = rootNode;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeData() {
        return nodeData;
    }

    public void setNodeData(String nodeData) {
        this.nodeData = nodeData;
    }
}
