package org.example.repository;

import org.example.dto.NodeInfo;
import org.example.dto.NodeProperty;
import org.example.utils.exception.NonUniqueException;
import org.hippoecm.hst.site.HstServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chinnku on Nov, 2021
 * NodeSearchRepository
 * to find/search the nodes
 */
public class NodeSearchRepository implements NodeRepository {

    private static Logger LOGGER = LoggerFactory.getLogger(NodeSearchRepository.class);

    /**
     * Returns the unique node by the provided uuid
     *
     * @param uuid - unique uuid of the node
     * @return
     * @throws Exception
     */
    @Override
    public NodeInfo getUniqueNodeById(String uuid) throws Exception {
        Query query = createXpathQuery("//*[@jcr:uuid='" + uuid + "']");
        return getUniqueNode(query);
    }

    /**
     * Returns the list of nodes contains the provided value
     *
     * @param path        - path to search
     * @param containsVal - value to search
     * @return
     * @throws Exception
     */
    @Override
    public List<NodeInfo> getNodesContainValue(String path, String containsVal) throws Exception {
        Query query = createXpathQuery(path + "//*[jcr:contains(., '" + containsVal + "')]");
        return getNodesByQuery(query);
    }

    /**
     * Returns the list of nodes matches provide property name and value
     *
     * @param path      - path to search
     * @param propName  - property name
     * @param propValue - property value
     * @return
     * @throws Exception
     */
    @Override
    public List<NodeInfo> getNodesContainProp(String path, String propName, String propValue) throws Exception {
        String updatedPropName = ".";
        if (!propName.isEmpty()) {
            updatedPropName = propName;
        }
        Query query = createXpathQuery(path + "//*[@" + propName + "='" + propValue + "']");
        return getNodesByQuery(query);
    }

    /**
     * Returns the list of nodes in the provided path
     *
     * @param path - path to get the nodes
     * @return
     * @throws RepositoryException
     */
    @Override
    public List<NodeInfo> getNodeByPath(String path) throws RepositoryException {
        List<NodeInfo> resultList = new ArrayList<>();
        Node rootNode = initiateOrGetSession().getRootNode();
        //1. List Nodes - recursively find all the nodes under /content/documents/ and
        //display the names of descendant nodes.
        Node node = rootNode.getNode(path);
        getNode(node, resultList);
        return resultList;
    }

    /**
     * Recursively outputs the contents of the given node.
     *
     * @param node
     * @param resultList
     * @throws RepositoryException
     */
    private void getNode(Node node, List<NodeInfo> resultList) throws RepositoryException {
        List<NodeProperty> nodeProperties = new ArrayList<>();
        NodeInfo nodeInfo = new NodeInfo(node.getIdentifier(), node.getName(), node.getPath(), node.getPrimaryNodeType().getName(), nodeProperties);
        resultList.add(nodeInfo);
        if (!node.isNodeType("hippofacnav:facetnavigation")) {
            // Then output the properties
            addNodeProperties(node, nodeProperties);

            // Finally output all the child nodes recursively
            NodeIterator nodes = node.getNodes();
            while (nodes.hasNext()) {
                Node childNode = nodes.nextNode();
                getNode(childNode, resultList);
            }
        }
    }

    /**
     * Add Properties of the node
     *
     * @param node
     * @param nodeProperties
     * @throws RepositoryException
     */
    private void addNodeProperties(Node node, List<NodeProperty> nodeProperties) throws RepositoryException {
        PropertyIterator properties = node.getProperties();
        while (properties.hasNext()) {
            Property property = properties.nextProperty();
            List<String> propValues = new ArrayList<>();
            if (property.getDefinition().isMultiple()) {
                // A multi-valued property, print all values
                Value[] values = property.getValues();
                for (int i = 0; i < values.length; i++) {
                    propValues.add(values[i].getString());
                }
            } else {
                // A single-valued property
                propValues.add(property.getString());
            }
            nodeProperties.add(new NodeProperty(property.getName(), property.getDefinition().isMultiple(), propValues, property.getPath()));
        }
    }

    /**
     * get Nodes by given Query
     *
     * @param query
     * @return
     * @throws Exception
     */
    private List<NodeInfo> getNodesByQuery(Query query) throws Exception {
        List<NodeInfo> nodeInfos = new ArrayList<>();
        QueryResult queryResult = query.execute();
        for (NodeIterator i = queryResult.getNodes(); i.hasNext(); ) {
            Node node = i.nextNode();
            List<NodeProperty> nodeProperties = new ArrayList<>();
            addNodeProperties(node, nodeProperties);
            NodeInfo nodeInfo = new NodeInfo(node.getIdentifier(), node.getName(), node.getPath(), node.getPrimaryNodeType().getName(), nodeProperties);
            nodeInfos.add(nodeInfo);
        }
        return nodeInfos;
    }

    /**
     * get Unique node for the given Query
     *
     * @param query
     * @return
     * @throws Exception
     */
    private NodeInfo getUniqueNode(Query query) throws Exception {
        QueryResult queryResult = query.execute();
        if (queryResult.getNodes().getSize() == 1) {
            Node node = queryResult.getNodes().nextNode();
            List<NodeProperty> nodeProperties = new ArrayList<>();
            addNodeProperties(node, nodeProperties);
            return new NodeInfo(node.getIdentifier(), node.getName(), node.getPath(), node.getPrimaryNodeType().getName(), nodeProperties);
        } else if (queryResult.getNodes().getSize() == 0) {
            return null;
        } else {
            throw new NonUniqueException("Non unique result found for the given input.");
        }
    }

    /**
     * Create Xpath query
     *
     * @param query
     * @return
     * @throws RepositoryException
     */
    public Query createXpathQuery(String query) throws RepositoryException {
        return getQueryManager().createQuery(query, Query.XPATH);
    }

    /**
     * get the QueryManaged instance
     *
     * @return
     * @throws RepositoryException
     */
    private QueryManager getQueryManager() throws RepositoryException {
        return initiateOrGetSession().getWorkspace().getQueryManager();
    }

    /**
     * initialize or get a exiting live session
     *
     * @return
     * @throws RepositoryException
     */
    public Session initiateOrGetSession() throws RepositoryException {
        if (getSession() != null) {
            try {
                if (getSession().isLive()) {
                    return _session;
                }
            } catch (IllegalStateException iex) {
                LOGGER.error("The State of the Session is not valid, hence removing the session");
                setSession(null);
            }
        }
        Repository repository = HstServices.getComponentManager().getComponent(Repository.class.getName());
        _session = repository.login(new SimpleCredentials(USER_ID, PASSWORD.toCharArray()));
        return _session;
    }

    private Session getSession() {
        return _session;
    }

    private void setSession(Session session) {
        this._session = session;
    }

    private Session _session = null;

    private static final String USER_ID = "admin";
    private static final String PASSWORD = "admin";

}