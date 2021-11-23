package org.example.servlet;

import org.hippoecm.hst.site.HstServices;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by chinnku on November, 2021
 * AssessmentServlet
 * it simply returns a html with the list of nodes and properties
 * base on the provided url params
 * Accepted params:
 * path - provide the path to find the nodes. ex, "site/assessment?path=content/documents"
 * query - provide any value to find the nodes. ex, "site/assessment?query=news"
 */
public class AssessmentServlet extends HttpServlet {

    private static Logger LOGGER = LoggerFactory.getLogger(AssessmentServlet.class);

    /**
     * goGet method for the AssessmentServlet
     *
     * @param req - HttpServletRequest
     * @param res - HttpServletResponse
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        Repository repository =
                HstServices.getComponentManager().getComponent(Repository.class.getName());
        Session session = null;
        StringBuilder responseStr = new StringBuilder();
        try {
            session = repository.login(new SimpleCredentials(USER_ID, PASSWORD.toCharArray()));

            // You can get the root node of the default workspace from the repository.
            Node rootNode = session.getRootNode();
            appendHtmlHead(responseStr);

            String pram = req.getParameter(PATH_PARAM);
            String pramQuery = req.getParameter(QUERY_PARAM);
            if (pram != null) {
                //1. List Nodes - recursively find all the nodes under /content/documents/ and
                //display the names of descendant nodes.
                Node node = rootNode.getNode(pram);
                getNode(node, responseStr);
            } else if (pramQuery != null) {
                //2. Querying - execute queries against the repository for some user entered text
                //and display the names and properties of all the nodes that contain that text.
                QueryManager queryManager = session.getWorkspace().getQueryManager();
                Query query = queryManager.createQuery(XPATH_QUERY_DOC_PATH + "//*[jcr:contains(., '" + pramQuery + "')]", Query.XPATH);
                QueryResult queryResult = query.execute();
                for (NodeIterator i = queryResult.getNodes(); i.hasNext(); ) {
                    Node node = i.nextNode();
                    appendNodeHtml(node, responseStr);
                    addProperties(node, responseStr);
                }
            } else {
                responseStr.append("Error: Please provide valid url with required parameters!");
            }
            //Return response as HTML
            res.setContentType(MediaType.TEXT_HTML);
            PrintWriter out = res.getWriter();
            appendHtmlTail(responseStr);
            out.println(responseStr);
        } catch (Exception e) {
            LOGGER.error("Exception Occurred", e);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }

    /**
     * Recursively outputs the contents of the given node.
     *
     * @param node - Node
     * @param respString - String
     * @throws Exception - Exception
     */
    private void getNode(Node node, StringBuilder respString) throws Exception {
        appendNodeHtml(node, respString);
        if (node.isNodeType(HIPPO_FACT_NAVIGATION)) {
            return;
        }
        addProperties(node, respString);
        NodeIterator nodes = node.getNodes();
        while (nodes.hasNext()) {
            getNode(nodes.nextNode(), respString);
        }
    }

    /**
     * Add the properties of the nodes
     *
     * @param node - Node
     * @param respString - StringBuilder
     * @throws Exception - Exception
     */
    private void addProperties(Node node, StringBuilder respString) throws Exception {
        PropertyIterator properties = node.getProperties();
        respString.append("<ul>");
        while (properties.hasNext()) {
            Property property = properties.nextProperty();
            if (property.getDefinition().isMultiple()) {
                javax.jcr.Value[] values = property.getValues();
                for (Value value : values) {
                    respString.append("<li>Name: " + property.getName() + ", Value: " + value.getString() + "</li>");
                }
            } else {
                respString.append("<li>Name: " + property.getName() + ", Value: " + property.getString() + "</li>");
            }
        }
        appendPropertyTail(respString);
    }

    /**
     * @param responseStr - StringBuilder
     */
    private void appendHtmlHead(StringBuilder responseStr) {
        responseStr.append("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <title>Node Search Results</title>\n" +
                "    <style>\n" +
                "        table {\n" +
                "            border-collapse: collapse;\n" +
                "        }\n" +
                "        table, th, td {\n" +
                "            border: 1px solid black;\n" +
                "        }\n" +
                "        th, td {\n" +
                "            padding: 10px;\n" +
                "        }\n" +
                "        th {\n" +
                "            text-align: left;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h2>Node Search Results</h2>\n" +
                "    <table>\n" +
                "        <tr>\n" +
                "            <th>Node</th>\n" +
                "            <th>Property</th>\n" +
                "        </tr>\n");
    }

    /**
     * @param responseStr - StringBuilder
     */
    private void appendHtmlTail(StringBuilder responseStr) {
        responseStr.append("    </table>\n" +
                "</body>\n" +
                "</html>");
    }

    /**
     * @param node - Node
     * @param responseStr - StringBuilder
     * @throws RepositoryException - RepositoryException
     */
    private void appendNodeHtml(Node node, StringBuilder responseStr) throws RepositoryException {
        responseStr.append("<tr>\n" +
                "            <td>Name: " + node.getName() + ", Path: " + node.getPath() + "</td>\n" +
                "<td>");
    }

    /**
     * @param respString - StringBuilder
     */
    private void appendPropertyTail(StringBuilder respString) {
        respString.append("</ul>");
        respString.append("</td>\n" +
                "        </tr>");
    }

    private static final String USER_ID = "admin";
    private static final String PASSWORD = "admin";
    private static final String PATH_PARAM = "path";
    private static final String QUERY_PARAM = "query";
    private static final String XPATH_QUERY_DOC_PATH = "/jcr:root/content/documents";
    private static final String HIPPO_FACT_NAVIGATION = "hippofacnav:facetnavigation";
}
