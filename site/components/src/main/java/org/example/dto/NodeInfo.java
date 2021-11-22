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

    public String getUuid() {
        return uuid;
    }

    private String uuid;
    private String nodeName;
    private String nodePath;
    private String primaryType;
    private List<NodeProperty> nodeProperties;
    private List<Link> links;

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public String getPrimaryType() {
        return primaryType;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getNodePath() {
        return nodePath;
    }

    public List<NodeProperty> getNodeProperties() {
        return nodeProperties;
    }
}
