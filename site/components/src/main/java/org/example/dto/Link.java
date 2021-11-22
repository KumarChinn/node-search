package org.example.dto;

/**
 * Created by chinnku on Nov, 2021
 */
public class Link {
    private String href;
    private String rel;
    private String method;
    private String type;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Link(String href, String rel, String method, String type) {
        this.href = href;
        this.rel = rel;
        this.method = method;
        this.type = type;
    }
}
