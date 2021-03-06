package org.example.utils.exception;

import javax.jcr.PathNotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by chinnku on Nov, 2021
 */
@Provider
public class NodeExceptionHandler implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception exception) {
        Response response = null;
        ErrorResponse errorResponse;
        if (exception instanceof NonUniqueException) {
            errorResponse = new ErrorResponse(Response.Status.CONFLICT.getStatusCode(), exception.getMessage());
            response = Response.status(Response.Status.CONFLICT.getStatusCode()).entity(errorResponse).type(MediaType.APPLICATION_JSON).build();
        } else if (exception instanceof PathNotFoundException) {
            errorResponse = new ErrorResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), PATH_NOT_FOUND + exception.getMessage());
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(errorResponse).type(MediaType.APPLICATION_JSON).build();
        } else if (exception != null) {
            errorResponse = new ErrorResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), exception.getMessage());
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(errorResponse).type(MediaType.APPLICATION_JSON).build();
        }
        return response;
    }

    private static final String PATH_NOT_FOUND = "The provided path is Invalid, path=";
}
