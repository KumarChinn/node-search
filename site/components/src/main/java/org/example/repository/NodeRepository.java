package org.example.repository;

import org.example.dto.NodeInfo;

import javax.jcr.RepositoryException;
import java.util.List;

/**
 * Created by chinnku on Nov, 2021
 */
public interface NodeRepository {

    NodeInfo getUniqueNodeById(String uuid) throws Exception;

    List<NodeInfo> getNodesContainValue(String path, String propValue) throws Exception;

    List<NodeInfo> getNodesContainProp(String path, String propName, String propValue) throws Exception;

    List<NodeInfo> getNodeByPath(String path) throws RepositoryException;
}
