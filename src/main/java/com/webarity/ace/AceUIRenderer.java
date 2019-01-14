package com.webarity.ace;

import java.io.IOException;

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

    @Override
    public void encodeEnd(FacesContext ctx, UIComponent comp) throws IOException {
        if (ctx == null || comp == null) throw new NullPointerException();
        AceUI c = (AceUI)comp;
        
        try (
            ResponseWriter resp = ctx.getResponseWriter();
            ) {
                resp.startElement("div", c);
                resp.writeAttribute("id", "ace-ed", null);
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
                    resp.writeAttribute("value", j.toJson(c.getValue()), "value");
                }
                resp.endElement("input");
                
                renderScriptInitializer(resp, c);
            } catch (IOException ex) {
                throw ex;
            }
        }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        return j.fromJson((String)submittedValue, AceModel.class);

        // return super.getConvertedValue(context, component, submittedValue);
    }

    @Override
    public void decode(FacesContext ctx, UIComponent comp) {
        AceUI c = (AceUI)comp;
        c.setSubmittedValue(ctx.getExternalContext().getRequestParameterMap().get(c.getClientId()));
    }
    
    /**
     * <p>Takes care of Ace configuration, instantiates it on the client</p>
     * <p>More importantly, uses {@code ace.config.setModeUrl(...)} to adapt the URLs Ace would use to load its module, theme, keybind, worker, etc. Check {@link <a href="https://ace.c9.io/#nav=howto&api=edit_session">Configure dynamic loading of modes and themes</a>}</p>
     * @param resp Used to write the actual markup. Passed from the {@link #encodeEnd(FacesContext, UIComponent) encodeEnd} method
     * @param c The Ace custom component
     * @throws IOException
     */
    private void renderScriptInitializer(ResponseWriter resp, AceUI c) throws IOException {
        resp.startElement("script", c);
        
        resp.write(String.format("ace.config.setModuleUrl('ace/theme/%s', '/javax.faces.resource/theme-%s.js.xhtml?ln=ace');",  c.getTheme(), c.getTheme()));
        resp.write(String.format("ace.config.setModuleUrl('ace/keybindings/%s', '/javax.faces.resource/keybinding-%s.js.xhtml?ln=ace');",  c.getKeybinding(), c.getKeybinding()));
        resp.write(String.format("ace.config.setModuleUrl('ace/mode/%s', '/javax.faces.resource/mode-%s.js.xhtml?ln=ace');",  c.getMode(), c.getMode()));
        resp.write(String.format("ace.config.setModuleUrl('ace/mode/%s_worker', '/javax.faces.resource/worker-%s.js.xhtml?ln=ace');",  c.getMode(), c.getMode()));

        resp.write(String.format("var editor = ace.edit('%s', {maxLines:%d, minLines:%d, wrap:%b, autoScrollEditorIntoView:%b, mode: 'ace/mode/%s'});", "ace-ed", c.getMaxLines(), c.getMinLines(), true, true, c.getMode()));
        
        resp.write(String.format("editor.setTheme('ace/theme/%s');", c.getTheme()));
        resp.write(String.format("editor.setKeyboardHandler('ace/keybindings/%s');", c.getKeybinding()));

        resp.write(String.format("WebarityAceEditor.updater('%s');", c.getClientId()));

        if (c.getValue() != null) {
            resp.write(String.format("WebarityAceEditor.jsonToSession(editor, document.getElementById('%s').value)", c.getClientId()));
        }
        resp.endElement("script");
    }
}