# AceJSF Component

JSF component that enables usage of [Ace](https://ace.c9.io/) v1.4.2 editor for JSF applications.

### Features:
* out of the box support for 162 modes
* out of the box support for 38 modes
* key bindings for emacs and vim
* persists Ace session to JSF backing bean:
    * code folds
    * scroll
    * set options
* supports `<f:ajax/>`

### Usage:

In your facelets page import the namespace `https://www.webarity.com/custom-comps/aceui`. For ex:
 
```xml
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="#{siteLocales.getCurrentLocale().getLanguage()}" xmlns="http://www.w3.org/1999/xhtml" xmlns:h="http://xmlns.jcp.org/jsf/html" xmlns:f="http://xmlns.jcp.org/jsf/core" xmlns:ui="http://java.sun.com/jsf/facelets" xmlns:h5a="http://xmlns.jcp.org/jsf/passthrough" xmlns:h5e="http://xmlns.jcp.org/jsf"
xmlns:ccs="https://www.webarity.com/custom-comps/aceui">
...
</html>
```
Above we use the `css` namespace, but that can be arbitrary. Now add the component:

```xml
<h:form>
    <ccs:AceUI theme="textmate" mode="html" minLines="5" maxLines="20" value="#{someBean.code}">
        <f:ajax/>
    </ccs:AceUI>
</h:form>
```
There is support for ajax, but the tag is purely optional. The component essentially behaves like `UIInput` so it must be put inside a `h:form` element.

If using ajax, component will automatically synch with the backing bean 1.5 seconds after last detected input (Ace `change` event). Component will also monitor `changeScrollTop`
`changeScrollLeft`, `changeFold`, `changeBackMarker`, `changeFrontMarker`, `changeWrapMode`, `changeWrapLimit` Ace events and will automatically update after 2.5 seconds after they've been triggered; for ex., a code block has been folded or you've scrolled - after 2.5 seconds client will issue ajax post request to JSF with the new Ace state. If your backing bean is `@SessionScoped`, on a page reload the same state will be observed, along with scroll position, marked selection, folded blocks of code. For description of those events, refer to [Ace Documentation](https://ace.c9.io/#nav=api&api=edit_session).

Component comes with a custom type `com.webarity.ace.model.AceModel`. The backing bean in the example above - `#{someBean.code}`, the `code` field is of type `AceModel`. This is a deserialized view of the Ace Session. The actual code is reachable via `code.content` as a `java.lang.String`. There are no getters/setters for brevity; all fields are public and are directly accessed as of this initial version of the component.

**NOTE:** the field of your backing bean can be String. It will receive a JSON value of `AceModel`. Look below for Serialization/deserialization - which can be integrated with your own converter.

### Serialization:
Provided are JSON serializer/deserializer:
* `com.webarity.ace.model.json.AceModelDeserializer`
* `com.webarity.ace.model.json.AceModelSerializer`

Both of them implement `javax.json.bind.serializer.JsonbSerializer<AceModel>`.

Can be used as follows:
```java
static final Jsonb j = JsonbBuilder.create(new JsonbConfig()
    .withDeserializers(new AceModelDeserializer())
    .withSerializers(new AceModelSerializer())
);
String jsonVal = j.toJson(aceModel);
AceModel aceModel = j.fromJson(jsonVal, AceModel.class);
```

### Attributes:
* `mode`, optional, defaults to `plain_text`. Provided is a `${AceModes}` of type `java.util.HashMap<String, String>` whose keys is a `java.util.Set` of all available modes. A `mode` can be:

>`abap`, `abc`, `actionscript`, `ada`, `apache_conf`, `apex`, `applescript`, `asciidoc`, `asl`, `assembly_x86`, `autohotkey`, `batchfile`, `bro`, `c_cpp`, `c9search`, `cirru`, `clojure`, `cobol`, `coffee`, `coldfusion`, `csharp`, `csound_document`, `csound_orchestra`, `csound_score`, `csp`, `css`, `curly`, `d`, `dart`, `diff`, `django`, `dockerfile`, `dot`, `drools`, `edifact`, `eiffel`, `ejs`, `elixir`, `elm`, `erlang`, `forth`, `fortran`, `fsharp`, `fsl`, `ftl`, `gcode`, `gherkin`, `gitignore`, `glsl`, `gobstones`, `golang`, `graphqlschema`, `groovy`, `haml`, `handlebars`, `haskell`, `haskell_cabal`, `haxe`, `hjson`, `html`, `html_elixir`, `html_ruby`, `ini`, `io`, `jack`, `jade`, `java`, `javascript`, `json`, `jsoniq`, `jsp`, `jssm`, `jsx`, `julia`, `kotlin`, `latex`, `less`, `liquid`, `lisp`, `livescript`, `logiql`, `logtalk`, `lsl`, `lua`, `luapage`, `lucene`, `makefile`, `markdown`, `mask`, `matlab`, `maze`, `mel`, `mixal`, `mushcode`, `mysql`, `nix`, `nsis`, `objectivec`, `ocaml`, `pascal`, `perl`, `perl6`, `pgsql`, `php`, `php_laravel_blade`, `pig`, `plain_text`, `powershell`, `praat`, `prolog`, `properties`, `protobuf`, `puppet`, `python`, `r`, `razor`, `rdoc`, `red`, `redshift`, `rhtml`, `rst`, `ruby`, `rust`, `sass`, `scad`, `scala`, `scheme`, `scss`, `sh`, `sjs`, `slim`, `smarty`, `snippets`, `soy_template`, `space`, `sparql`, `sql`, `sqlserver`, `stylus`, `svg`, `swift`, `tcl`, `terraform`, `tex`, `text`, `textile`, `toml`, `tsx`, `turtle`, `twig`, `typescript`, `vala`, `vbscript`, `velocity`, `verilog`, `vhdl`, `visualforce`, `wollok`, `xml`, `xquery`, `yaml`

* `theme`, optional, defaults to `monokai`. Provided is a `${AceThemes}` of type `java.util.HashMap<String, String>` whose keys is a `java.util.Set` of all available themes. A `theme` can be:

>`ambiance`, `chaos`, `chrome`, `clouds`, `clouds_midnight`, `cobalt`, `crimson_editor`, `dawn`, `dracula`, `dreamweaver`, `eclipse`, `github`, `gob`, `gruvbox`, `idle_fingers`, `iplastic`, `katzenmilch`, `kr_theme`, `kuroir`, `merbivore`, `merbivore_soft`, `mono_industrial`, `monokai`, `pastel_on_dark`, `solarized_dark`, `solarized_light`, `sqlserver`, `terminal`, `textmate`, `tomorrow`, `tomorrow_night`, `tomorrow_night_blue`, `tomorrow_night_bright`, `tomorrow_night_eighties`, `twilight`, `vibrant_ink`, `xcode`

* `keybinding`, optional, defaults to `vim`. Provided is a `${AceKeybindings}` of type `java.util.HashMap<String, String>` whose keys is a `java.util.Set` of all available keybindings. A `keybinding` can be:

>`emacs`, `vim`

* `maxLines`, `java.lang.Integer`, when Ace editor grows to that many lines, scrollbars will appear.

* `minLines`, `java.lang.Integer`, Ace editor will at least span that many lines.


---

## Prerequisites:
* Java EE8
* JDK >= 10
* Maven >= 3.5.x

## Running
* `mvn clean package`
* copy `jar` file to `WEB-INF/lib/` for `.war` deployments
* copy `jar` file to `/lib/` for `.ear` deployments

## Tested on
* WildFly 15 with jre v10.0.2

---

https://www.webarity.com