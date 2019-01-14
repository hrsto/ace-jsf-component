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