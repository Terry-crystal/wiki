<template>
    <a-layout>
        <a-layout-sider width="200" style="background: #fff">
            <a-menu
                    mode="inline"
                    :style="{ height: '100%', borderRight: 0 }"
                    @click="handleClick"
            >
                <a-menu-item key="welcome">
                    <router-link :to="'/'">
                        <MailOutlined/>
                        <span>电子书分类栏</span>
                    </router-link>
                </a-menu-item>

                <a-sub-menu v-for="item in level1" :key="item.id">
                    <template v-slot:title>
                        <span>{{item.name}}</span>
                    </template>
                    <a-menu-item v-for="child in item.children" :key="child.id">
                        <TagsOutlined />
                        <span>{{child.name}}</span>
                    </a-menu-item>
                </a-sub-menu>


            </a-menu>
        </a-layout-sider>

        <!--中间显示控件-->
        <a-layout-content
                :style="{ background: '#FFF', padding: '24px', margin: 0, minHeight: '280px' }"
        >

            <a-list item-layout="vertical" size="large"
                    :grid="{ gutter: 20, column: 3 }"
                    :loading="loading"
                    :data-source="ebooks">

                <template #renderItem="{ item }">
                    <a-list-item key="item.name">
                        <template #actions>

          <span v-for="{ type, text } in actions" :key="type">
            <component v-bind:is="type" style="margin-right: 8px"/>
            {{ text }}
          </span>
                        </template>
                        <a-list-item-meta :description="item.description">
                            <template #title>
                                <a :href="item.href">{{ item.name }}</a>
                            </template>
                            <template #avatar>
                                <a-avatar :src="item.cover"/>
                            </template>
                        </a-list-item-meta>
                    </a-list-item>
                </template>
            </a-list>

        </a-layout-content>
    </a-layout>
</template>

<style scoped>
    .ant-avatar {
        width: 50px;
        height: 50px;
        line-height: 50px;
        border-radius: 8%;
        margin: 5px 0;
    }
</style>


<script lang="ts">
    import {defineComponent, onMounted, ref} from 'vue';
    import axios from 'axios';
    import {Tool} from "@/util/tool";
    import {message} from "ant-design-vue";

    export default defineComponent({
        name: 'Home',
        setup() {
            console.log("setup");
            const ebooks = ref();   //这个是一个响应式的数据！


            /**
             * 一级分类树，children属性就是二级分类
             * [{
             *   id: "",
             *   name: "",
             *   children: [{
             *     id: "",
             *     name: "",
             *   }]
             * }]
             */
            const level1 = ref();   //这是响应式变量，是可以给html使用的
            let categorys: any;     //这是一个普通的变量，就是在js中计算使用的
            /**
             * 查询所有的分类
             */
            const handleQueryCategory = () => {
                axios.get("/category/all").then((response) => {
                    const data = response.data;
                    if (data.success) {
                        categorys = data.content;
                        level1.value = [];
                        level1.value = Tool.array2Tree(categorys, 0);
                    } else {
                        message.error(data.message);
                    }
                });
            };

            const handleClick = () => {
                console.log("menu click")
            }

            //生命周期初始化方法
            onMounted(() => {
                handleQueryCategory();
                axios.get("/ebook/list", {
                    params: {
                        page: 1,
                        size: 1000  //一次性将所有的数据全都查出来
                    }
                }).then((response) => {
                    const data = response.data; //此data就是后端中的commonResp
                    ebooks.value = data.content.list;
                });
            });

            const pagination = {
                onChange: (page: number) => {
                    console.log(page);
                },
                pageSize: 3,
            };
            const actions: Record<string, string>[] = [
                {type: 'StarOutlined', text: '156'},
                {type: 'LikeOutlined', text: '156'},
                {type: 'MessageOutlined', text: '2'},
            ];

            return {
                ebooks,
                pagination,
                actions,
                handleClick,
                level1
            }
        }
    });
</script>


