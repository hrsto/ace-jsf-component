package com.webarity.ace.model.json;

import java.util.Optional;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

import com.webarity.ace.model.AceModel;

public class AceModelSerializer implements JsonbSerializer<AceModel> {

    @Override
    public void serialize(AceModel obj, JsonGenerator generator, SerializationContext ctx) {
        generator.writeStartObject();

        generator.write("content",  Optional.ofNullable(obj.content).orElse(""));
        generator.write("scrollLeft", Optional.ofNullable(obj.scrollLeft).orElse(0));
        generator.write("scrollTop", Optional.ofNullable(obj.scrollTop).orElse(0));
        generator.write("mode", obj.mode);

        generator.writeStartObject("options");
        if (obj.options != null && obj.options.size() > 0) {
            obj.options.forEach(opt -> {
                if (opt.getValue() instanceof Integer) {
                    generator.write(opt.getName(), (Integer)opt.getValue());
                } else if (opt.getValue() instanceof String) {
                    generator.write(opt.getName(), (String)opt.getValue());
                } else if (opt.getValue() instanceof Boolean) {
                    generator.write(opt.getName(), (Boolean)opt.getValue());
                }
            });
        }
        generator.writeEnd();

        generator.writeStartArray("folds");
        if (obj.folds != null) {
            obj.folds.forEach(fold -> {
                generator.writeStartObject();
    
                generator.write("placeholder", fold.placeholder);
    
                generator.writeStartObject("start");
                generator.write("column", fold.start.column);
                generator.write("row", fold.start.row);
                generator.writeEnd();
    
                generator.writeStartObject("end");
                generator.write("column", fold.end.column);
                generator.write("row", fold.end.row);
                generator.writeEnd();
    
                generator.writeEnd();
            });
        }
        generator.writeEnd();

        generator.writeStartObject("selection");
        if (obj.selection != null) {
    
            generator.write("isBackwards", obj.selection.isBackwards);
    
            generator.writeStartObject("start");
            generator.write("column", obj.selection.start.column);
            generator.write("row", obj.selection.start.row);
            generator.writeEnd();
    
            generator.writeStartObject("end");
            generator.write("column", obj.selection.end.column);
            generator.write("row", obj.selection.end.row);
            generator.writeEnd();
    
        }
        generator.writeEnd();
        
        generator.writeEnd();
	}

}