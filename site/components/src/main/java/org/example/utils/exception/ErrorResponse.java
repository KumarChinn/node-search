package org.example.utils.exception;

/**
 * Created by chinnku on Nov, 2021
 */
public class ErrorResponse {

    public int get_error_code() {
        return _error_code;
    }

    public void set_error_code(int _error_code) {
        this._error_code = _error_code;
    }

    public String get_error_desc() {
        return _error_desc;
    }

    public void set_error_desc(String _error_desc) {
        this._error_desc = _error_desc;
    }

    private int _error_code;
    private String _error_desc;

    public ErrorResponse(int _error_code, String _error_desc) {
        this._error_code = _error_code;
        this._error_desc = _error_desc;
    }

}
