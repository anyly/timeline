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
        //console.debug('name: '+plot.name);
        plot.apply(self, data);
    };
    self.then = function (params, fun) {
        if (arguments.length == 0) {
            throw new ReferenceError('Timeline.then not arguments');
        }
        var plot = arguments[arguments.length-1];
        var data = [];
        for (var i=0; i<arguments.length-1; i++) {
            data.push(arguments[i]);
        }

        plots.push(plot);
        datas.push(data);

        if (plots.length == 1) {
            self.next();
        }
        return self;
    };
    self.meanwhile = function (data) {
        var params = [];
        var meanwhiles = [];
        var count = 0;
        for (var i=0; i<arguments.length;i++) {
            var item = arguments[i];
            if (typeof(item) == 'function') {
                meanwhiles.push(item);
            } else {
                params.push(item);
            }
        }

        var asyncFunction = function (callback) {
            var fun = function () {
                if (callback) {
                    callback.apply(this, arguments);
                }
                //console.debug('async back='+plots[0].name);

                if (++count==meanwhiles.length) {
                    plots.shift();
                    datas.shift();
                    self.next();
                }
            };
            return fun;
        };


        var plot = function meanwhile() {
            for (var i=0; i<meanwhiles.length; i++) {
                meanwhiles[i].apply({
                    stop: self.stop,
                    asyncFunction: asyncFunction,
                    next: self.next
                }, params);
            }
        };
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
            //console.debug('async back='+plots[0].name);
            plots.shift();
            datas.shift();
            self.next();
        };
        return fun;
    };
}
