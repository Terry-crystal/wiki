<template>
    <a-layout-header class="header">

        <div class="logo"/>

        <a class="login-menu" v-show="user.id">
            <span>您好：{{user.name}}</span>
        </a>

        <a class="login-menu" v-show="!user.id" @click="showLoginModal">
            <span>登录</span>
        </a>

        <a-menu
                theme="dark"
                mode="horizontal"
                :style="{ lineHeight: '64px'}"
        >
            <a-menu-item key="/">
                <router-link to="/">首页</router-link>
            </a-menu-item>
            <a-menu-item key="/admin/ebook">
                <router-link to="/admin/ebook">电子书管理</router-link>
            </a-menu-item>
            <a-menu-item key="/admin/category">
                <router-link to="/admin/category">分类管理</router-link>
            </a-menu-item>
            <a-menu-item key="/admin/user">
                <router-link to="/admin/user">用户管理</router-link>
            </a-menu-item>
            <a-menu-item key="/about">
                <router-link to="/about">关于我们</router-link>
            </a-menu-item>

        </a-menu>

        <a-modal
                title="登录"
                v-model:visible="loginModalVisible"
                :confirm-loading="loginModalLoading"
                @ok="login"
        >
            <a-form :model="loginUser" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
                <a-form-item label="登录名">
                    <a-input v-model:value="loginUser.loginName"/>
                </a-form-item>
                <a-form-item label="密码">      <!--如果是新增，则显示密码框，如果是修改，则不显示密码框-->
                    <a-input v-model:value="loginUser.password" type="password"/>
                </a-form-item>
            </a-form>
        </a-modal>

        <a class="login-menu" @click="showLoginModal">
            <span>登录</span>
        </a>

    </a-layout-header>


</template>

<script lang="ts">
    import {defineComponent, ref} from 'vue';
    import axios from 'axios';
    import {message} from "ant-design-vue";
    import store from "@/store";

    declare let hexMd5: any;
    declare let KEY: any;

    /*给组件起了一个名字*/
    export default defineComponent({
        name: 'the-header',

        setup() {

            //这个是用来登录的用户信息
            const loginUser = ref({
                loginName: "test",
                password: "test"
            });

            //登录后保存用户信息
            const user = ref();
            user.value = {};  //初始化给个空对象，可以避免空指针异常

            const loginModalVisible = ref(false);
            const loginModalLoading = ref(false);
            //点击事件调用模态框
            const showLoginModal = () => {
                loginModalVisible.value = true;
            };

            //登录请求操作
            const login = () => {
                loginModalLoading.value = true;   //开始显示loading效果
                loginUser.value.password = hexMd5(loginUser.value.password + KEY);    //在前端对密码进行一次md5加密
                axios.post("/user/login", loginUser.value).then((response) => {
                    loginModalLoading.value = false;  //设置通知等待
                    const data = response.data;
                    if (data.success) {
                        loginModalVisible.value = false;
                        user.value = data.content;
                        store.commit("setUser", user);  //将登录信息放入到全局变量中
                    } else {
                        message.error(data.message);
                    }
                })
            };

            return {
                loginModalVisible,
                loginModalLoading,
                showLoginModal,
                loginUser,
                login,
                user
            }

        }

    });
</script>

<style>
    .login-menu {
        float: right;
        color: white;
    }
</style>
