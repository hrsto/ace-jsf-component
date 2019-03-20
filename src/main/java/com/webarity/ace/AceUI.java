package com.webarity.ace;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.faces.application.Application;
import javax.faces.component.UIInput;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;

public class AceUI extends UIInput implements ClientBehaviorHolder {

    private static final String DEFAULT_KEYBINDING = "vim";
    private static final String DEFAULT_THEME = "monokai";
    private static final String DEFAULT_MODE = "plain_text";
    private static final List<String> SUPPORTED_EVENTS = Arrays.asList("change");

    public static enum Props {
        maxLines, minLines, mode, keybinding, theme, worker;
    }

    public Integer getMaxLines() { return (Integer)getStateHelper().get(Props.maxLines); }
    
    public void setMaxLines(Integer maxLines) {
        getStateHelper().put(Props.maxLines, maxLines);
    }
    public Integer getMinLines() { return (Integer)getStateHelper().get(Props.minLines); }
    public void setMinLines(Integer minLines) {
        getStateHelper().put(Props.minLines, minLines);
    }
    public String getMode() {
        return Optional.ofNullable((String)getStateHelper().get(Props.mode)).orElse(DEFAULT_MODE);
    }
    public void setMode(String mode) {
        Optional.ofNullable(evalExrp("#{AceModes}", HashMap.class).get(mode)).ifPresentOrElse(m -> getStateHelper().put(Props.mode, mode), () -> getStateHelper().put(Props.mode, DEFAULT_MODE));

        Optional.ofNullable(evalExrp("#{AceWorkers}", HashMap.class).get(mode)).ifPresentOrElse(m -> getStateHelper().put(Props.worker, mode), () -> getStateHelper().put(Props.worker, null));
    }
    public String getTheme() {
        return Optional.ofNullable((String)getStateHelper().get(Props.theme)).orElse(DEFAULT_THEME);
    }
    public void setTheme(String theme) {
        Optional.ofNullable(evalExrp("#{AceThemes}", HashMap.class).get(theme)).ifPresentOrElse(t -> getStateHelper().put(Props.theme, theme), () -> getStateHelper().put(Props.theme, DEFAULT_THEME));
    }
    public String getWorker() { return (String)getStateHelper().get(Props.worker); }
    public void setWorker(String worker) {
        //set alongside with ace mode. If there's no worker for the mode, then worker is null...
    }
    public String getKeybinding() {
        return Optional.ofNullable((String)getStateHelper().get(Props.keybinding)).orElse(DEFAULT_KEYBINDING);
    }
    public void setKeybinding(String keybinding) {
        getStateHelper().put(Props.keybinding, Optional.ofNullable(evalExrp("#{AceKeybindings}", HashMap.class).get(keybinding)).orElse(DEFAULT_KEYBINDING));
    }


    @Override
    public void addClientBehavior(String eventName, ClientBehavior behavior) {
        super.addClientBehavior(eventName, behavior);
    }

    @Override
    public Map<String, List<ClientBehavior>> getClientBehaviors() {
        return super.getClientBehaviors();
    }

    @Override
    public Collection<String> getEventNames() {
        return SUPPORTED_EVENTS;
    }

    @Override
    public String getDefaultEventName() {
        return "change";
    }

    @Override
    public String getFamily() {
        return "CodeEditors";
    }

    /**
     * <p>Since various collections are set as named beans directly in config xml, only way to access them is via EL. This method takes an EL expression and returns the managed bean</p>
     * @param expr An EL expression. For ex "#{AceModes}"
     * @param c Expected return type. For ex HashMap.class
     * @return Returns the result of the EL expression typed to the `c` parameter.
     */
    protected static <T> T evalExrp(String expr, Class<T> c) {
        FacesContext tempExtCtx = FacesContext.getCurrentInstance();
        Application tempApp = tempExtCtx.getApplication();
        return tempApp.evaluateExpressionGet(tempExtCtx, expr, c);
    }
}