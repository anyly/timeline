function Timeline() {
    var self = this;
    var plots = [];
    var datas = [];

    self.stop = function() {
        var e = new Error();
        e.name = 'timeline_stop';
        throw new e;
    };
    self.next = function() {
        var plot = plots[0];
        var data = datas[0];
        if (!plot) {
            return self;
        }
        plot.apply(self, data);
    };
    self.then = function (params, plot) {
        if (arguments.length == 0) {
            throw new ReferenceError('Timeline.then not arguments');
        }
        plot = arguments[arguments.length-1];
        params = [];
        for (var i=0; i<arguments.length-1; i++) {
            params.push(arguments[i]);
        }

        if (plots.length == 0) {
            plot.apply(self, params);
        }
        plots.push(plot);
        datas.push(params);
        return self;
    };
    self.asyncFunction = function (callback) {
        var fun = function () {
            try {
                if (callback) {
                    callback.apply(self, arguments);
                }
            } catch (e) {
                if (e.name == 'timeline_stop') {

                } else {
                    throw e;
                }
            }
            plots.shift();
            datas.shift();
            self.next();
        };
        return fun;
    };
}
