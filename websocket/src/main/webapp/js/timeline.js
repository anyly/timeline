/*var timeline = new Timeline()
    .then(a, b, function () {
        a;
        this.a;
    })//串行
    .meanwhile()//并行
;

function Timeline() {
    var self = this;
    var plots = [];
    var datas = [];
    this.next = function() {
        var plot = plots.shift();
        var data = datas.shift();
        if (!plot) {
            return;
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

        plots.push(plot);
        datas.push(params);
        if (plots.length == 1) {
            self.next();
        }
    };
    self.returnFunction = function () {
        return {};
    };
    returnFunction;
}
*/