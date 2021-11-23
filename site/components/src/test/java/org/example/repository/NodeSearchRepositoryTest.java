package org.example.repository;

import junit.framework.TestCase;
import org.example.dto.NodeInfo;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.*;

import javax.jcr.*;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import java.util.List;

/**
 * Created by chinnku on Nov, 2021
 */
public class NodeSearchRepositoryTest {

    NodeSearchRepository nodeSearchRepository;

    NodeType nodeType;
    Node contentNode;
    Node documentNode;
    NodeIterator nodeIterator;
    Property property;
    PropertyDefinition propertyDefinition;
    PropertyIterator nodePropertyIterator;
    Query queryMock;
    QueryResult queryResultMock;
    Session sessionMock;


    @Test
    public void testGetUniqueNodeById() throws Exception {
        //Given
        NodeSearchRepository nodeSearchReopSpy = spy(nodeSearchRepository);
        doReturn(queryMock).when(nodeSearchReopSpy).createXpathQuery(anyString());

        //When
        NodeInfo nodeInfo = nodeSearchReopSpy.getUniqueNodeById("836487324");

        //Then
        Assert.assertNotNull(nodeInfo);
        Assert.assertEquals(nodeInfo.getNodeName(), contentNode.getName());
        Assert.assertEquals(nodeInfo.getNodePath(), contentNode.getPath());
        Assert.assertEquals(nodeInfo.getUuid(), contentNode.getIdentifier());
        Assert.assertEquals(nodeInfo.getPrimaryType(), contentNode.getPrimaryNodeType().getName());
        Assert.assertNotNull(nodeInfo.getNodeProperties());
        Assert.assertFalse(nodeInfo.getNodeProperties().isEmpty());
        Assert.assertEquals(nodeInfo.getNodeProperties().size(), 1);

    }

    @Test
    public void testGetNodesContainValue() throws Exception {
        NodeSearchRepository nodeSearchReopSpy = spy(nodeSearchRepository);
        doReturn(queryMock).when(nodeSearchReopSpy).createXpathQuery(anyString());

        //When
        List<NodeInfo> nodeInfo = nodeSearchReopSpy.getNodesContainValue("123213", "836487324");

        //Then
        Assert.assertNotNull(nodeInfo);
        Assert.assertFalse(nodeInfo.isEmpty());
    }

    @Test
    public void testGetNodesContainProp() throws Exception {
        NodeSearchRepository nodeSearchReopSpy = spy(nodeSearchRepository);
        doReturn(queryMock).when(nodeSearchReopSpy).createXpathQuery(anyString());

        //When
        List<NodeInfo> nodeInfo = nodeSearchReopSpy.getNodesContainProp("2342", "123213", "836487324");

        //Then
        Assert.assertNotNull(nodeInfo);
        Assert.assertFalse(nodeInfo.isEmpty());
    }

    @Test
    public void testGetNodeByPath() throws Exception {
        NodeSearchRepository nodeSearchRepository1 = spy(nodeSearchRepository);
        doReturn(sessionMock).when(nodeSearchRepository1).initiateOrGetSession();
        when(sessionMock.getRootNode()).thenReturn(contentNode);
        when(contentNode.getNode(anyString())).thenReturn(contentNode);

        //When
        List<NodeInfo> nodeInfo = nodeSearchRepository1.getNodeByPath("23432");

        //Then
        Assert.assertNotNull(nodeInfo);
        Assert.assertFalse(nodeInfo.isEmpty());
    }

    @Before
    public void setUp() throws Exception {
        nodeSearchRepository = new NodeSearchRepository();

        //Test Node setup for testing
        nodeType = mock(NodeType.class);
        contentNode = mock(Node.class);
        documentNode = mock(Node.class);
        nodeIterator = mock(NodeIterator.class);
        property = mock(Property.class);
        propertyDefinition = mock(PropertyDefinition.class);
        nodePropertyIterator = mock(PropertyIterator.class);
        queryMock = mock(Query.class);
        queryResultMock = mock(QueryResult.class);
        sessionMock = mock(Session.class);

        when(nodeIterator.hasNext()).thenReturn(true).thenReturn(false);
        when(nodeIterator.nextNode()).thenReturn(contentNode);
        when(contentNode.getNodes()).thenReturn(nodeIterator);

        when(contentNode.getName()).thenReturn("content");
        when(contentNode.getIdentifier()).thenReturn("2c21a29c-a5d1-4e84-aec4-594e5bf93b25");
        when(contentNode.getPath()).thenReturn("/content");
        when(contentNode.getPrimaryNodeType()).thenReturn(nodeType);
        when(nodeType.getName()).thenReturn("hippostd:folder");

        when(contentNode.getProperties()).thenReturn(nodePropertyIterator);
        when(nodePropertyIterator.hasNext()).thenReturn(true).thenReturn(false);
        when(nodePropertyIterator.nextProperty()).thenReturn(property);
        when(property.getDefinition()).thenReturn(propertyDefinition);
        when(property.getName()).thenReturn("jcr:uuid");
        when(property.getString()).thenReturn("2c21a29c-a5d1-4e84-aec4-594e5bf93b25");
        when(property.getPath()).thenReturn("/content/jcr:uuid");
        when(propertyDefinition.isMultiple()).thenReturn(false);

        when(queryMock.execute()).thenReturn(queryResultMock);
        when(queryResultMock.getNodes()).thenReturn(nodeIterator);
        when(nodeIterator.getSize()).thenReturn(1L);

    }

    @After
    public void tearDown() {
        nodeSearchRepository = null;
        nodeType = null;
        contentNode = null;
        documentNode = null;
        nodeIterator = null;
        property = null;
        propertyDefinition = null;
        nodePropertyIterator = null;
        queryMock = null;
        queryResultMock = null;
        sessionMock = null;
    }
}