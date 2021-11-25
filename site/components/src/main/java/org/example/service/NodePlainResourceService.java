package org.example.service;

import io.swagger.annotations.*;
import org.example.dto.Link;
import org.example.dto.NodeInfo;
import org.example.repository.NodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by chinnku on Nov, 2021
 */
@Path("v1/nodes")
@Api("v1/nodes")
@SwaggerDefinition(tags = {@Tag(name = "Node Search Service", description = "To Search Nodes")})
public class NodePlainResourceService extends org.hippoecm.hst.jaxrs.services.AbstractResource {

    private static Logger LOGGER = LoggerFactory.getLogger(NodePlainResourceService.class);

    /**
     * REST method to get the Node by ID
     *
     * @param servletRequest
     * @param servletResponse
     * @param uriInfo
     * @param uuid
     * @return
     * @throws Exception
     */
    @GET
    @Path("/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Get Node by unique uuid",
            notes = "Provide the unique uuid (node identifier) of the node to get the node details"
    )
    public NodeInfo getNodeById(
            @Context HttpServletRequest servletRequest,
            @Context HttpServletResponse servletResponse,
            @Context UriInfo uriInfo,
            @ApiParam(value = "Unique Node Identifier") @PathParam(PARAM_UUID) String uuid) throws Exception {
        LOGGER.info("Start of getNodeById(), uuid=" + uuid);
        NodeInfo nodeInfo = addLinksToResponse(getNodeSearchRepository().getUniqueNodeById(uuid), uriInfo);
        if (nodeInfo == null) {
            LOGGER.error("Node not found for the given uuid");
            throw new NotFoundException("Node not found for the given uuid");
        }
        LOGGER.info("End of getNodeById(), nodeInfo=" + nodeInfo);
        return nodeInfo;
    }

    /**
     * Serves to multiple combinations of params
     * Possible params:
     * 1. query
     * 2. path
     * 3. prop-name, prop-value
     *
     * @param servletRequest
     * @param servletResponse
     * @param uriInfo
     * @param query
     * @param path
     * @param propertyName
     * @param propertyValue
     * @return
     * @throws Exception
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Search Node(s)",
            notes = "Searches Nodes with multiple combinations of params\n" +
                    "     Possible params combinations:\n" +
                    "     * 1. query - ex, /nodes?query={value}\n" +
                    "     * 2. path - ex, /nodes?path={value}\n" +
                    "     * 3. prop-name, prop-value - ex, /nodes?prop-name={value}&prop-value={value}")
    public List<NodeInfo> searchNodes(
            @Context HttpServletRequest servletRequest,
            @Context HttpServletResponse servletResponse,
            @Context UriInfo uriInfo,
            @ApiParam(value = "Any value to search nodes") @QueryParam(PARAM_QUERY) String query,
            @ApiParam(value = "Any path value to search nodes") @QueryParam(PARAM_PATH) String path,
            @ApiParam(value = "Valid name of the node property") @QueryParam(PARAM_PROP_NAME) String propertyName,
            @ApiParam(value = "value of the property") @QueryParam(PARAM_PROP_VAL) String propertyValue) throws Exception {
        LOGGER.info("Start of searchNodes(), query={}, path={}, prop-name={}, prop-name={}", query, path, propertyName, propertyValue);
        List<NodeInfo> nodeList;
        if (query != null) {
            regExIfParamNotNull(path);
            regExIfParamNotNull(propertyName);
            regExIfParamNotNull(propertyValue);
            nodeList = addLinksToResponse(getNodeSearchRepository().getNodesContainValue(JCR_SEARCH_PATH, query), uriInfo);
        } else if (propertyName != null && propertyValue != null) {
            regExIfParamNotNull(path);
            nodeList = addLinksToResponse(getNodeSearchRepository().getNodesContainProp(JCR_SEARCH_PATH, propertyName, propertyValue), uriInfo);
        } else if (path != null) {
            regExIfParamNotNull(propertyName);
            regExIfParamNotNull(propertyValue);
            nodeList = addLinksToResponse(getNodeSearchRepository().getNodeByPath(path), uriInfo);
        } else {
            nodeList = addLinksToResponse(getNodeSearchRepository().getNodeByPath(DOCUMENTS_PATH), uriInfo);
        }
        if (nodeList.isEmpty()) {
            LOGGER.error("There is no Node(s) found for the given input.");
            throw new NotFoundException("There is no Node(s) found for the given input.");
        }
        LOGGER.info("End of searchNodes(), nodeList.size={}", nodeList.size());
        return nodeList;
    }

    /**
     * Decorator method to add Link object the response
     * This adds the binary link in case of image node.
     *
     * @param nodeInfo
     * @param uriInfo
     * @return
     * @throws Exception
     */
    private NodeInfo addLinksToResponse(NodeInfo nodeInfo, UriInfo uriInfo) throws Exception {
        if (nodeInfo.getPrimaryType().equals(HIPPO_FACT_NAVIGATION)) {
            Optional<String> uuid = nodeInfo.getNodeProperties()
                    .stream()
                    .filter(nodeProperty -> nodeProperty.getPropertyName().equals(HIPPO_DOCBASE_TYPE) && !nodeProperty.isMultipleValue())
                    .flatMap(nodeProperty -> nodeProperty.getPropertyValues().stream()).findAny();
            if (uuid.isPresent()) {
                NodeInfo imageNodeInfo = getNodeSearchRepository().getUniqueNodeById(uuid.get());
                imageNodeInfo.getNodePath();
                List<Link> links = new ArrayList<>();
                links.add(new Link(buildBinaryBaseUri(uriInfo) + imageNodeInfo.getNodePath(), LINK_REL, LINK_METHOD_TYPE, LINK_DATA_TYPE));
                nodeInfo.setLinks(links);
            }
        } else {
            List<Link> links = new ArrayList<>();
            links.add(new Link(uriInfo.getAbsolutePath().toString() + "/" + nodeInfo.getUuid(), LINK_REL, LINK_METHOD_TYPE, MediaType.APPLICATION_JSON));
            nodeInfo.setLinks(links);
        }
        return nodeInfo;
    }

    /**
     * Decorator method to add Link to response
     *
     * @param nodeInfos
     * @param uriInfo
     * @return
     */
    private List<NodeInfo> addLinksToResponse(List<NodeInfo> nodeInfos, UriInfo uriInfo) {
        for (NodeInfo nodeInfo : nodeInfos) {
            try {
                addLinksToResponse(nodeInfo, uriInfo);
            } catch (Exception e) {
                LOGGER.error("Exception in adding Links to the Nodes", e);
            }
        }
        return nodeInfos;
    }

    /**
     * Build Binary base URI
     *
     * @param uriInfo
     * @return
     */
    private String buildBinaryBaseUri(UriInfo uriInfo) {
        return uriInfo.getBaseUri().getScheme() + "://" + uriInfo.getBaseUri().getAuthority() + "/site/binaries";
    }

    private void regExIfParamNotNull(String param) {
        if (param != null) {
            throw new BadRequestException("The pram '" + param + "' is not expected.");
        }
    }

    public void setNodeSearchRepository(NodeRepository nodeSearchRepository) {
        this.nodeSearchRepository = nodeSearchRepository;
    }

    public NodeRepository getNodeSearchRepository() {
        return nodeSearchRepository;
    }

    private NodeRepository nodeSearchRepository;

    private static final String PARAM_UUID = "uuid";
    private static final String PARAM_PATH = "path";
    private static final String PARAM_PROP_NAME = "prop-name";
    private static final String PARAM_PROP_VAL = "prop-value";
    private static final String PARAM_QUERY = "query";
    private static final String JCR_SEARCH_PATH = "/jcr:root/content/documents";
    private static final String DOCUMENTS_PATH = "content/documents/";
    private static final String HIPPO_FACT_NAVIGATION = "hippofacnav:facetnavigation";
    private static final String HIPPO_DOCBASE_TYPE = "hippo:docbase";

    private static final String LINK_REL = "self";
    private static final String LINK_METHOD_TYPE = "GET";
    private static final String LINK_DATA_TYPE = "binary";
}

