import {createStore} from 'vuex'

declare let SessionStorage: any;    //在vuex中封装session-storage.js提供给外界使用

const USER = "USER";

const store = createStore({
    state: {
        user: SessionStorage.get(USER) || {}    //去SessionStorage中获取user，如果获取不到，则为空对象，可以避免空指针异常
    },
    mutations: {    //对变量的操作方法 同步
        setUser(state, user) {
            state.user = user;
            SessionStorage.set(USER, user);   //给SessionStorage中的user赋值,其实就是将这个对象放到缓存SessionStorage中
        }
    },
    actions: {      //对变量的操作方法 异步

    },
    modules: {}
});

export default store;