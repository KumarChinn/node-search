package org.example.utils.exception;

/**
 * Created by chinnku on Nov, 2021
 */
public class ErrorResponse {

    private int _error_code;
    private String _error_desc;

    public ErrorResponse(int _error_code, String _error_desc) {
        this._error_code = _error_code;
        this._error_desc = _error_desc;
    }

}
