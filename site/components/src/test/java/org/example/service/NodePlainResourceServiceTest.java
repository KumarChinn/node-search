package org.example.service;

import org.example.dto.NodeInfo;
import org.example.dto.NodeProperty;
import org.example.repository.NodeSearchRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriInfo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Created by chinnku on Nov, 2021
 */
public class NodePlainResourceServiceTest {

    @Test
    public void getNodeById() throws Exception {
        //Given
        String uuid = "2c21a29c-a5d1-4e84-aec4-594e5bf93b25";
        NodeInfo nodeInfo = new NodeInfo(uuid, "documents", "hippostd:folder");
        when(nodeSearchRepository.getUniqueNodeById(uuid)).thenReturn(nodeInfo);

        //When
        NodeInfo nodeInfoRes = nodePlainResourceServiceSpy.getNodeById(httpServletRequest, httpServletResponse, uriInfo, uuid);

        //Then
        assertEquals(nodeInfo.getUuid(), nodeInfoRes.getUuid());
        assertFalse(nodeInfo.getLinks().isEmpty());
    }

    @Test
    public void searchNodesByQuery() throws Exception {
        //Given
        String searchQueryVal = "news";
        when(nodeSearchRepository.getNodesContainValue(anyString(), anyString())).thenReturn(nodeInfoList);

        //When
        List<NodeInfo> nodeInfoListRes = nodePlainResourceServiceSpy.searchNodes(httpServletRequest, httpServletResponse, uriInfo, searchQueryVal, null, null, null);

        //Then
        assertFalse(nodeInfoListRes.isEmpty());
        assertEquals(1, nodeInfoListRes.size());

    }

    @Test
    public void searchNodesByPath() throws Exception {
        //Given
        String searchPath = "content/documents";

        when(nodeSearchRepository.getNodeByPath(anyString())).thenReturn(nodeInfoList);
        //When
        List<NodeInfo> nodeInfoListRes = nodePlainResourceServiceSpy.searchNodes(httpServletRequest, httpServletResponse, uriInfo, null, searchPath, null, null);
        //Then
        assertFalse(nodeInfoListRes.isEmpty());
        assertEquals(1, nodeInfoListRes.size());
    }

    @Test
    public void searchNodesByParam() throws Exception {
        //Given
        String paramName = "jcr:primaryType";
        String paramValue = "hippostd:folder";
        when(nodeSearchRepository.getNodesContainProp(anyString(), anyString(), anyString())).thenReturn(nodeInfoList);
        //When
        List<NodeInfo> nodeInfoListRes = nodePlainResourceServiceSpy.searchNodes(httpServletRequest, httpServletResponse, uriInfo, null, null, paramName, paramValue);
        //Then
        assertFalse(nodeInfoListRes.isEmpty());
        assertEquals(1, nodeInfoListRes.size());
    }

    @Test
    public void getNodeByIdCheckImageLink() throws Exception {
        //Given
        String uuid = "2c21a29c-a5d1-4e84-aec4-594e5bf93b25";
        NodeInfo nodeInfo = new NodeInfo(uuid, "documents", "hippofacnav:facetnavigation");
        nodeInfo.setNodePath("/image");
        NodeProperty nodeProperty = new NodeProperty("hippo:docbase", false, Arrays.asList("594e5bf93b25"), "documents/image");
        List<NodeProperty> propertyList = new ArrayList<>();
        propertyList.add(nodeProperty);
        nodeInfo.setNodeProperties(propertyList);
        when(nodeSearchRepository.getUniqueNodeById(uuid)).thenReturn(nodeInfo);
        when(nodeSearchRepository.getUniqueNodeById("594e5bf93b25")).thenReturn(nodeInfo);

        //When
        NodeInfo nodeInfoRes = nodePlainResourceServiceSpy.getNodeById(httpServletRequest, httpServletResponse, uriInfo, uuid);

        //Then
        assertEquals(nodeInfo.getUuid(), nodeInfoRes.getUuid());
        assertFalse(nodeInfo.getLinks().isEmpty());
    }

    @Test
    public void searchAllNodes() throws Exception {
        //Given
        when(nodeSearchRepository.getNodeByPath(anyString())).thenReturn(nodeInfoList);
        //When
        List<NodeInfo> nodeInfoListRes = nodePlainResourceServiceSpy.searchNodes(httpServletRequest, httpServletResponse, uriInfo, null, null, null, null);
        //Then
        assertFalse(nodeInfoListRes.isEmpty());
        assertEquals(1, nodeInfoListRes.size());
    }

    private NodePlainResourceService nodePlainResourceService = null;
    private NodeSearchRepository nodeSearchRepository = null;
    private HttpServletRequest httpServletRequest = null;
    private HttpServletResponse httpServletResponse = null;
    private UriInfo uriInfo = null;
    NodePlainResourceService nodePlainResourceServiceSpy = null;
    List<NodeInfo> nodeInfoList = new ArrayList<>();

    @Before
    public void setUp() throws URISyntaxException {
        nodePlainResourceService = new NodePlainResourceService();
        nodePlainResourceService = new NodePlainResourceService();
        httpServletRequest = mock(HttpServletRequest.class);
        httpServletResponse = mock(HttpServletResponse.class);
        uriInfo = mock(UriInfo.class);
        nodeSearchRepository = mock(NodeSearchRepository.class);
        nodePlainResourceServiceSpy = spy(nodePlainResourceService);
        doReturn(nodeSearchRepository).when(nodePlainResourceServiceSpy).getNodeSearchRepository();
        URI uri = new URI("https://localhost:8080/site/restservice/v1/nodes");
        when(uriInfo.getAbsolutePath()).thenReturn(uri);
        when(uriInfo.getBaseUri()).thenReturn(uri);
        nodeInfoList.add(new NodeInfo("2c21a29c-a5d1-4e84-aec4-594e5bf93b25", "documents", "hippostd:folder"));

    }

    @After
    public void tearDown() {
        nodePlainResourceService = null;
        httpServletRequest = null;
        httpServletResponse = null;
        uriInfo = null;
        nodeSearchRepository = null;
        nodePlainResourceServiceSpy = null;
        nodeInfoList.clear();
    }
}