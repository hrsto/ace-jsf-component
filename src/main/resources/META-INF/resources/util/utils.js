var WebarityAceEditor = (() => {

    var tid = null;
    var syntheticChangeEvent = document.createEvent("Event");
    syntheticChangeEvent.initEvent("change", true, true);

    var proc = (id, timeout) => {
        clearTimeout(tid);
        tid = setTimeout(() => {
            var elem = document.getElementById(id);
            elem.value = JSON.stringify(WebarityAceEditor.sessionToJson(editor));
            elem.dispatchEvent(syntheticChangeEvent);
        }, timeout);
    }

    return {
        jsonToSession: (editor, state) => {
            var st = JSON.parse(state);
            editor.session.setValue(st.content);
            editor.selection.fromJSON(st.selection);
            editor.setOptions(st.options);
            editor.session.setMode(st.mode);
            editor.session.setScrollTop(st.scrollTop);
            editor.session.setScrollLeft(st.scrollLeft);
            try {
                st.folds.forEach((fold) => {
                    editor.session.addFold(fold.placeholder, ace.Range.fromPoints(fold.start, fold.end));
                });
            } catch(e) { console.error(`Fold exception: ${e}`); }
        },
        sessionToJson: (editor) => {
            return {
                content: editor.getSession().getValue(),
                selection: editor.getSelection().toJSON(),
                options: editor.getOptions(),
                mode: editor.session.getMode().$id,
                scrollTop: editor.session.getScrollTop(),
                scrollLeft: editor.session.getScrollLeft(),
                folds: editor.session.getAllFolds().map(function(fold) {
                    return {
                        start       : fold.start,
                        end         : fold.end,
                        placeholder : fold.placeholder
                    };
                })
            }
        },
        updater: (id) => {
            editor.getSession().on('change', evt => proc(id, 1500));
            editor.getSession().on('changeScrollTop', scrollTop => proc(id, 2500));
            editor.getSession().on('changeScrollLeft', scrollLeft => proc(id, 2500));
            editor.getSession().on('changeFold', () => proc(id, 2500));
            editor.getSession().on('changeBackMarker', () => proc(id, 2500));
            editor.getSession().on('changeFrontMarker', () => proc(id, 2500));
            editor.getSession().on('changeWrapMode', () => proc(id, 2500));
            editor.getSession().on('changeWrapLimit', () => proc(id, 2500));
        }
    };
})();

function WebarityAceJS(editorId, inputElementId, theme, kbdHandler, initSession, opts) {
    this.inputElementId = inputElementId;
    this.timeoutId;

    this.editor = ace.edit(editorId, opts);

    this.editor.setTheme(theme);
    this.editor.setKeyboardHandler(kbdHandler);

    this.updater();

    if (initSession) {
        this.jsonToSession();
    }
}

WebarityAceJS.prototype = {
    constructor: WebarityAceJS,

    updater: function() {
        this.editor.getSession().on('change', evt => this.proc(this.inputElementId, 1500));
        this.editor.getSession().on('changeScrollTop', scrollTop => this.proc(this.inputElementId, 2500));
        this.editor.getSession().on('changeScrollLeft', scrollLeft => this.proc(this.inputElementId, 2500));
        this.editor.getSession().on('changeFold', () => this.proc(this.inputElementId, 2500));
        this.editor.getSession().on('changeBackMarker', () => this.proc(this.inputElementId, 2500));
        this.editor.getSession().on('changeFrontMarker', () => this.proc(this.inputElementId, 2500));
        this.editor.getSession().on('changeWrapMode', () => this.proc(this.inputElementId, 2500));
        this.editor.getSession().on('changeWrapLimit', () => this.proc(this.inputElementId, 2500));
    },

    jsonToSession: function() {
        var st = JSON.parse(document.getElementById(this.inputElementId).value);
        this.editor.session.setValue(st.content);
        this.editor.selection.fromJSON(st.selection);
        this.editor.setOptions(st.options);
        this.editor.session.setMode(st.mode);
        this.editor.session.setScrollTop(st.scrollTop);
        this.editor.session.setScrollLeft(st.scrollLeft);
        try {
            st.folds.forEach((fold) => {
                this.editor.session.addFold(fold.placeholder, ace.Range.fromPoints(fold.start, fold.end));
            });
        } catch(e) { console.error(`Fold exception: ${e}`); }
    },
    
    sessionToJson: function() {
        return {
            content: this.editor.getSession().getValue(),
            selection: this.editor.getSelection().toJSON(),
            options: this.editor.getOptions(),
            mode: this.editor.session.getMode().$id,
            scrollTop: this.editor.session.getScrollTop(),
            scrollLeft: this.editor.session.getScrollLeft(),
            folds: this.editor.session.getAllFolds().map(function(fold) {
                return {
                    start       : fold.start,
                    end         : fold.end,
                    placeholder : fold.placeholder
                };
            })
        }
    },

    syntheticChangeEvent: (function() {
        var temp = document.createEvent("Event");
        temp.initEvent("change", true, true);
        return temp;
    })(),

    proc: function(id, timeout) {
        clearTimeout(this.tid);
        this.tid = setTimeout(() => {
            var elem = document.getElementById(id);
            if (!elem) return;
            elem.value = JSON.stringify(this.sessionToJson());
            elem.dispatchEvent(this.syntheticChangeEvent);
        }, timeout);
    }
}
