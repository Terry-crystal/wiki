import {createApp} from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';
import * as Icons from '@ant-design/icons-vue';
import axios from 'axios';

axios.defaults.baseURL = process.env.VUE_APP_SERVER;    //改axios的baseUrl为读取系统配置文件，此baseUrl不是vue的URL！

const app = createApp(App);
app.use(store).use(router).use(Antd).mount('#app');

// 全局使用图标
const icons: any = Icons;
for (const i in icons) {
    app.component(i, icons[i]);
}

console.log('环境:', process.env.NODE_ENV);    //使用process.env.xxx读环境变量
console.log('服务端 :', process.env.VUE_APP_SERVER);
