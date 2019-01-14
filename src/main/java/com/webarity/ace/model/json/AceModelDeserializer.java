package com.webarity.ace.model.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

import com.webarity.ace.model.AceModel;
import com.webarity.ace.model.elements.Fold;
import com.webarity.ace.model.elements.Range;
import com.webarity.ace.model.elements.Selection;
import com.webarity.ace.model.elements.options.Option;
import com.webarity.ace.model.elements.options.OptionImpl;

public class AceModelDeserializer implements JsonbDeserializer<AceModel> {

	@Override
	public AceModel deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {

		AceModel m = new AceModel();

		String key = null;
		JsonArray arr = null;
		JsonObject obj = null;

		JsonValue val = null;

        while (parser.hasNext()) {
			JsonParser.Event e = parser.next();

			switch (e) {
				case VALUE_FALSE:
				case VALUE_TRUE:
				case VALUE_NULL:
				case VALUE_NUMBER: 
				case VALUE_STRING:
					val = parser.getValue();
					break;
				case START_ARRAY:
					arr = parser.getArray();
					break;
				case START_OBJECT:
					obj = parser.getObject();
					break;
				case KEY_NAME:
					key = parser.getString();
					continue;
				case END_ARRAY:
				case END_OBJECT:
				default:
			}

			if (key != null && key.compareTo("folds") == 0 && arr != null && arr.size() > 0) {
				m.folds = new ArrayList<Fold>(arr.size());
				arr.stream().map(elem -> elem.asJsonObject()).forEach(elem -> {
					Fold f = new Fold();
					f.placeholder = elem.getString("placeholder");

					f.start = new Range();
					f.end = new Range();

					f.start.row = Integer.parseInt(elem.get("start").asJsonObject().get("row").toString());
					f.start.column = Integer.parseInt(elem.get("start").asJsonObject().get("column").toString());
					f.end.row = Integer.parseInt(elem.get("end").asJsonObject().get("row").toString());
					f.end.column = Integer.parseInt(elem.get("end").asJsonObject().get("column").toString());

					m.folds.add(f);
				});
				arr = null;
				key = null;
			} else if (key != null && key.compareTo("options") == 0 && obj != null) {
				m.options = new LinkedList<Option<?>>();
				obj.entrySet().stream().forEach(entry -> {
					switch (entry.getValue().getValueType()) {
						case ARRAY:
						case OBJECT:
						case NULL:
							break;
						case FALSE:
						case TRUE:
							Option<Boolean> boolOP = new OptionImpl<>();
							boolOP.setName(entry.getKey());
							boolOP.setValue(Boolean.parseBoolean(entry.getValue().toString()));
							m.options.add(boolOP);
							break;
						case NUMBER:
							Option<Integer> numOP = new OptionImpl<>();
							numOP.setName(entry.getKey());
							numOP.setValue(Integer.parseInt(entry.getValue().toString()));
							m.options.add(numOP);
							break;
						case STRING:
							Option<String> stringOP = new OptionImpl<>();
							stringOP.setName(entry.getKey());
							stringOP.setValue(((JsonString)entry.getValue()).getString());
							m.options.add(stringOP);
							break;
					}
				});
				obj = null;
				key = null;
			} else if (key != null && key.compareTo("selection") == 0 && obj != null) {
				Selection s = new Selection();

				s.isBackwards = Boolean.parseBoolean(obj.get("isBackwards").toString());

				s.start = new Range();
				s.end = new Range();

				s.start.row = Integer.parseInt(obj.get("start").asJsonObject().get("row").toString());
				s.start.column = Integer.parseInt(obj.get("start").asJsonObject().get("column").toString());
				s.end.row = Integer.parseInt(obj.get("end").asJsonObject().get("row").toString());
				s.end.column = Integer.parseInt(obj.get("end").asJsonObject().get("column").toString());

				m.selection = s;

				obj = null;
				key = null;
			} else if (key != null && val != null) {
				switch (key) {
					case "content": m.content = parser.getString(); break;
					case "scrollLeft": m.scrollLeft = parser.getInt(); break;
					case "scrollTop": m.scrollTop = parser.getInt(); break;
					case "mode": m.mode = parser.getString(); break;
				}
				val = null;
				key = null;
			}
		}
		return m;
	}


}