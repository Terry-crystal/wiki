import {createStore} from 'vuex'

const store = createStore({
    state: {
        user: {}
    },
    mutations: {    //对变量的操作方法 同步
        setUser(state, user) {
            state.user = user;
        }
    },
    actions: {      //对变量的操作方法 异步

    },
    modules: {}
});

export default store;