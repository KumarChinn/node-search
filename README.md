
# Bloomreach - Node Search REST API

## Tasks
### 2.3 List nodes
Improve AssessmentServlet to recursively find all the nodes under /content/documents/ and
display the names of descendant nodes.

### Solution
Use the following Servlet Request to see the results, The result will be displayed in the form of Html.
http://localhost:8080/site/assessment?path=content/documents

### 2.4 Querying
Improve AssessmentServlet to execute queries against the repository for some user entered text
and display the names and properties of all the nodes that contain that text.

### Solution
Use the following Servlet Request to see the results, The result will be displayed in the form of Html.
http://localhost:8080/site/assessment?query=news

### 2.5 REST API Endpoint
Add a RESTful API endpoint by either adding a new servlet or improving the AssessmentServlet to
provide searching and listing of nodes and showing the detail of the node specified by part of URI
path.
### Solution
Full suit of REST service is developed and available, Please refer the swagger for API documentation,
#### Swagger JSON: http://localhost:8080/site/restservices/swagger.json
#### Some sample REST APIs,
1. get all the nodes -> http://localhost:8080/site/restservices/nodes
2. get a unique node by Id -> http://localhost:8080/site/restservices/nodes/2c21a29c-a5d1-4e84-aec4-594e5bf93b25
3. Query nodes by any given value -> http://localhost:8080/site/restservices/nodes?query=news
4. Query nodes by path -> http://localhost:8080/site/restservices/nodes?path=news=content/documents
5. Query nodes by property name and value -> http://localhost:8080/site/restservices/nodes?prop-name=jcr:primaryType&prop-value=hippostd:folder

### Observation listener
(Optional) Register a listener that prints out details about events that occur on nodes under
/content/ in the tree. You can trigger such events by creating documents and folders in the cms
interface.
### Solution
The listerns are added, refer the site.log for the details of the events.
#### Path: ..\myproject\target\tomcat9x\logs\site.log
