package com.webarity.ace.model;

import java.io.Serializable;
import java.util.List;

import com.webarity.ace.model.elements.options.Option;
import com.webarity.ace.model.elements.Fold;
import com.webarity.ace.model.elements.Selection;

public class AceModel implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public String content;
    public List<Fold> folds;
    public String mode;
    public List<Option<?>> options;
    public Integer scrollLeft;
    public Integer scrollTop;
    public Selection selection;
}