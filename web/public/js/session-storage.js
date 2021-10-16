SessionStorage = {
    get: function (key) {
        var v = sessionStorage.getItem(key);
        if (v && typeof (v) !== "undefined" && v !== "undefined") {
            return JSON.parse(v);
        }
    },
    set: function (key, data) { //h5中本身只能存储string类型的字符串数据，但是如果使用下面的方法就可以普通的object对象转化成字符串来进行存贮
        sessionStorage.setItem(key, JSON.stringify(data));
    },
    remove: function (key) {
        sessionStorage.removeItem(key);
    },
    clearAll: function () {
        sessionStorage.clear();
    }
};
