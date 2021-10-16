<template>
    <a-layout-footer style="text-align: center">
        伞兵电子书管理系统<span v-show="user.id">欢迎您：{{user.name}}</span>
    </a-layout-footer>
</template>

<script lang="ts">
    import {computed, defineComponent} from 'vue';
    import store from "@/store";

    /*给组件起了一个名字*/
    export default defineComponent({
        name: 'the-footer',

        setup() {

            //computed 是监听响应式变量有变化之后执行操作的方法
            const user = computed(() => store.state.user);

            return {
                user
            }
        }

    });
</script>