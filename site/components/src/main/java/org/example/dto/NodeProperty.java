package org.example.dto;


import java.util.List;

/**
 * Created by chinnku on Nov, 2021
 */
public class NodeProperty {
    public NodeProperty(String propertyName, boolean isMultipleValue, List<String> propertyValues, String propertyPath) {
        this.propertyName = propertyName;
        this.isMultipleValue = isMultipleValue;
        this.propertyValues = propertyValues;
        this.propertyPath = propertyPath;
    }

    private String propertyName;
    private boolean isMultipleValue;
    private List<String> propertyValues;
    private String propertyPath;

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public boolean isMultipleValue() {
        return isMultipleValue;
    }

    public void setMultipleValue(boolean multipleValue) {
        isMultipleValue = multipleValue;
    }

    public List<String> getPropertyValues() {
        return propertyValues;
    }

    public void setPropertyValues(List<String> propertyValues) {
        this.propertyValues = propertyValues;
    }

    public String getPropertyPath() {
        return propertyPath;
    }

    public void setPropertyPath(String propertyPath) {
        this.propertyPath = propertyPath;
    }
}
