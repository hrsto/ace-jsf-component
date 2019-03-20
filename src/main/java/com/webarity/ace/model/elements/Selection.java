package com.webarity.ace.model.elements;

import java.io.Serializable;

public class Selection implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public Boolean isBackwards;
    public Range end;
    public Range start;
}