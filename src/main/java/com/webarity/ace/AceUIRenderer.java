package com.webarity.ace;

import java.io.IOException;
import java.util.Optional;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;
import javax.faces.render.Renderer;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import com.webarity.ace.model.AceModel;
import com.webarity.ace.model.json.AceModelDeserializer;
import com.webarity.ace.model.json.AceModelSerializer;

@ResourceDependencies({
    @ResourceDependency(library = "ace", name = "ace.js", target = "head"),
    @ResourceDependency(library = "util", name = "utils.js", target = "head")
})
public class AceUIRenderer extends Renderer {

    public static final Jsonb j = JsonbBuilder.create(new JsonbConfig()
        .withDeserializers(new AceModelDeserializer())
        .withSerializers(new AceModelSerializer())
    );

    @SuppressWarnings({"unchecked"})
    @Override
    public void encodeEnd(FacesContext ctx, UIComponent comp) throws IOException {
        if (ctx == null || comp == null) throw new NullPointerException();
        AceUI c = (AceUI)comp;

        String editorId = String.format("ace-ed_%s", c.getClientId());

        ResponseWriter resp = ctx.getResponseWriter();
        resp.startElement("div", c);
        resp.writeAttribute("id", editorId, "clientId");
        resp.endElement("div");
        
        resp.startElement("input", c);
        resp.writeAttribute("name", c.getClientId(), "name");
        resp.writeAttribute("id", c.getClientId(), "id");
        resp.writeAttribute("hidden", true, "clientId");

        c.getClientBehaviors().entrySet().stream()
            .filter(entry -> c.getEventNames().contains(entry.getKey()))
            .forEach(entry -> {
                ClientBehaviorContext cbCtx = ClientBehaviorContext.createClientBehaviorContext(ctx, c, entry.getKey(), c.getClientId(), null);
                entry.getValue().stream().forEach(cb -> {
                    try {
                        resp.writeAttribute(String.format("on%s", entry.getKey()), cb.getScript(cbCtx), null);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
            });
            
        if (c.getValue() != null) {
            if (AceModel.class.isInstance(c.getValue())) {
                if (c.getConverter() != null) {
                    resp.writeAttribute("value",  c.getConverter().getAsString(ctx, c, c.getValue()), "value");
                } else {
                    resp.writeAttribute("value", j.toJson(c.getValue()), "value");
                }
            } else {
                resp.writeAttribute("value", c.getValue(), "value");
            }
        }
        resp.endElement("input");
        
        renderScriptInitializer(resp, c, editorId);
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        AceUI c = (AceUI)component;
        if (c.getConverter() == null) return super.getConvertedValue(context, component, submittedValue);
        return c.getConverter().getAsObject(context, c, (String)submittedValue);
    }

    @Override
    public void decode(FacesContext ctx, UIComponent comp) {
        AceUI c = (AceUI)comp;
        c.getClientBehaviors().forEach((event, behavior) -> behavior.forEach(b -> b.decode(ctx, c)));
        c.setSubmittedValue(ctx.getExternalContext().getRequestParameterMap().get(c.getClientId()));
    }
    
    /**
     * <p>Takes care of Ace configuration, instantiates it on the client</p>
     * <p>More importantly, uses {@code ace.config.setModeUrl(...)} to adapt the URLs Ace would use to load its module, theme, keybind, worker, etc. Check {@link <a href="https://ace.c9.io/#nav=howto&api=edit_session">Configure dynamic loading of modes and themes</a>}</p>
     * @param resp Used to write the actual markup. Passed from the {@link #encodeEnd(FacesContext, UIComponent) encodeEnd} method
     * @param c The Ace custom component
     * @throws IOException
     */
    private void renderScriptInitializer(ResponseWriter resp, AceUI c, String editorId) throws IOException {
        resp.startElement("script", c);
        
        resp.write(String.format("ace.config.setModuleUrl('ace/theme/%s', '/javax.faces.resource/theme-%s.js.xhtml?ln=ace');",  c.getTheme(), c.getTheme()));
        resp.write(String.format("ace.config.setModuleUrl('ace/keybindings/%s', '/javax.faces.resource/keybinding-%s.js.xhtml?ln=ace');",  c.getKeybinding(), c.getKeybinding()));
        resp.write(String.format("ace.config.setModuleUrl('ace/mode/%s', '/javax.faces.resource/mode-%s.js.xhtml?ln=ace');",  c.getMode(), c.getMode()));
        resp.write(String.format("ace.config.setModuleUrl('ace/mode/%s_worker', '/javax.faces.resource/worker-%s.js.xhtml?ln=ace');",  c.getMode(), c.getMode()));

        StringBuilder ops = new StringBuilder();
        ops.append("{");
        Optional.ofNullable(c.getMaxLines()).ifPresent(maxLines -> ops.append(String.format("maxLines: %d,", maxLines)));
        Optional.ofNullable(c.getMinLines()).ifPresent(maxLines -> ops.append(String.format("minLines: %d,", maxLines)));
        ops.append(String.format("wrap: %b,", true));
        ops.append(String.format("autoScrollEditorIntoView: %b,", true));
        ops.append(String.format("mode: 'ace/mode/%s',", c.getMode()));
        ops.append("}");

        resp.write(String.format("new WebarityAceJS('%s', '%s', 'ace/theme/%s', 'ace/keybindings/%s', %b, %s)", editorId, c.getClientId(), c.getTheme(), c.getKeybinding(), c.getValue() != null, ops));

        resp.endElement("script");
    }
}