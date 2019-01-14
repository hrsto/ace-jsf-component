package com.webarity.ace.model.elements.options;

public interface Option<T> {
    
    public String getName();
    public T getValue();
    public void setName(String name);
    public void setValue(T val);
}