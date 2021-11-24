package org.example.dto;

import java.util.List;

/**
 * Created by chinnku on Nov, 2021
 */
public class NodeInfo {

    public NodeInfo(String uuid, String nodeName, String nodePath, String primaryType, List<NodeProperty> nodeProperties) {
        this.uuid = uuid;
        this.nodeName = nodeName;
        this.nodePath = nodePath;
        this.primaryType = primaryType;
        this.nodeProperties = nodeProperties;
    }

    private String uuid;
    private String nodeName;
    private String nodePath;
    private String primaryType;
    private List<NodeProperty> nodeProperties;
    private List<Link> links;

    public NodeInfo(String uuid, String nodeName, String primaryType) {
        this.uuid = uuid;
        this.nodeName = nodeName;
        this.primaryType = primaryType;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodePath() {
        return nodePath;
    }

    public void setNodePath(String nodePath) {
        this.nodePath = nodePath;
    }

    public String getPrimaryType() {
        return primaryType;
    }

    public void setPrimaryType(String primaryType) {
        this.primaryType = primaryType;
    }

    public List<NodeProperty> getNodeProperties() {
        return nodeProperties;
    }

    public void setNodeProperties(List<NodeProperty> nodeProperties) {
        this.nodeProperties = nodeProperties;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
