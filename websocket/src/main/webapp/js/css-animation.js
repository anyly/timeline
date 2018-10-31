(function () {
    window.CSSEditor = {
        id : 'css-animation'
    };
    CSSEditor.styleSheet = function () {
        var sheet = document.querySelector('#'+this.id);
        if (!sheet) {
            sheet = document.createElement('style');
            sheet.setAttribute('id', this.id);
            var head = document.head || document.getElementsByTagName('head')[0];
            head.appendChild(sheet);
        }
        return sheet.sheet;
    };
    CSSEditor.getStyle = function(selectorText) {
        var sheet = this.styleSheet();
        var list = sheet.rules || sheet.cssRules;
        for (var rule in list) {
            if (rule.selectorText = selectorText) {
                return rule;
            }
        }
        return null;
    };
    CSSEditor.addStyle = function(selectorText, cssText) {
        if (!cssText) {
            cssText = '';
        }
        var sheet = this.styleSheet();
        var list = sheet.rules || sheet.cssRules;
        for (var rule in list) {
            if (rule.selectorText = selectorText) {
                rule.cssText = cssText;
                return;
            }
        }
        sheet.insertRule(selectorText+'{'+cssText+'}', 0);
    };
    CSSEditor.removeStyle = function (selectorText) {
        var sheet = this.styleSheet();
        var list = sheet.rules || sheet.cssRules;

        for (var i = 0; i<list.length; i++) {
            var rule = list[i];
            if (rule.selectorText = selectorText) {
                list.deleteRule(i);
                return;
            }
        }
    };
    CSSEditor.getFrame = function () {
        if (!cssText) {
            cssText = '';
        }
        var sheet = this.styleSheet();
        var list = sheet.rules || sheet.cssRules;
        for (var rule in list) {
            if (rule.name == name && rule.type == CSSRule.KEYFRAMES_RULE) {
                return rule;
            }
        }
        return null
    };
    CSSEditor.addFrame = function(name, cssText) {
        if (!cssText) {
            cssText = '';
        }
        var sheet = this.styleSheet();
        var list = sheet.rules || sheet.cssRules;
        for (var i=0; i<list.length; i++) {
            var rule = list[i];
            if (rule.name == name && rule.type == CSSRule.KEYFRAMES_RULE) {
                rule.cssText = cssText;
                return;
            }
        }
        sheet.insertRule('@-webkit-keyframes '+name+'{'+cssText+'}', 0);
    };
    CSSEditor.removeFrame = function(name) {
        var sheet = this.styleSheet();
        var list = sheet.rules || sheet.cssRules;

        for (var i = 0; i<list.length; i++) {
            var rule = list[i];
            if (rule.name == name && rule.type == CSSRule.KEYFRAMES_RULE) {
                sheet.deleteRule(i);
                return;
            }
        }
    };
})();

(function () {
    var genId = 0;
    window.CSSAnimation = {};
    CSSAnimation.swap = function (a, b) {
        if (typeof(a) == 'string') {
            a = document.querySelector(a);
        }
        if (typeof(b) == 'string') {
            b = document.querySelector(b);
        }
        //////
        var x1 = a.offsetLeft;
        var y1 = a.offsetTop;
        var x2 = b.offsetLeft;
        var y2 = b.offsetTop;

        var x = x2-x1;
        var y = y2-y1;

        var id = 'css-animation-'+(genId++);
        var cssText = '';
        //cssText += '0%{}\n';
        cssText += '100% { transform: translate('+x+'px,'+y+'px); }';
        CSSEditor.addFrame(id, cssText);
        a.style.animation = id + ' 1s ease';

        a.addEventListener('animationend', function () {
            if (callback) {
                callback.apply(this, arguments);
            }
            a.style.animation = '';
            CSSEditor.removeFrame(id);
        });


    };
    CSSAnimation.move = function (a, b, callback) {
        if (typeof(a) == 'string') {
            a = document.querySelector(a);
        }
        if (typeof(b) == 'string') {
            b = document.querySelector(b);
        }
        //////
        var x1 = a.offsetLeft;
        var y1 = a.offsetTop;
        var x2 = b.offsetLeft;
        var y2 = b.offsetTop;

        var x = x2-x1;
        var y = y2-y1;

        var id = 'css-animation-'+(genId++);
        var cssText = '';
        //cssText += '0%{}\n';
        cssText += '100% { transform: translate('+x+'px,'+y+'px); }';
        CSSEditor.addFrame(id, cssText);
        a.style.animation = id + ' 1s ease';
        a.addEventListener('animationend', function () {
            if (callback) {
                callback.apply(this, arguments);
            }
            a.style.animation = '';
            CSSEditor.removeFrame(id);
        });
    };
})();