package com.webarity.ace.model.elements.options;

public class OptionImpl<T> implements Option<T> {

    String name;
    T value;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setValue(T val) {
        this.value = val;
    }

}