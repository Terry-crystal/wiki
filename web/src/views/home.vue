<template>
    <a-layout>
        <a-layout-sider width="200" style="background: #fff">
            <a-menu
                    mode="inline"
                    :style="{ height: '100%', borderRight: 0 }"
                    @click="handleClick"
            >
                <a-menu-item key="welcome">
                    <BankOutlined/>
                    <span>欢迎</span>
                </a-menu-item>

                <a-sub-menu v-for="item in level1" :key="item.id">
                    <template v-slot:title>
                        <span>{{item.name}}</span>
                    </template>
                    <a-menu-item v-for="child in item.children" :key="child.id">
                        <TagsOutlined/>
                        <span>{{child.name}}</span>
                    </a-menu-item>
                </a-sub-menu>


            </a-menu>
        </a-layout-sider>

        <!--中间显示控件-->
        <a-layout-content
                :style="{ background: '#FFF', padding: '24px', margin: 0, minHeight: '280px' }"
        >

            <div class="welcome" v-show="isShowWelcome">
                <h1>欢迎使用伞兵知识库</h1>
            </div>

            <a-list item-layout="vertical" size="large" v-show="!isShowWelcome"
                    :grid="{ gutter: 20, column: 3 }"
                    :loading="loading"
                    :data-source="ebooks">

                <template #renderItem="{ item }">
                    <a-list-item key="item.name">
                        <template #actions>
                            <span>
                                <component v-bind:is="'FileOutlined'" style="margin-right: 8px"/>{{ item.docCount }}
                              </span>
                            <span>
                                <component v-bind:is="'UserOutlined'" style="margin-right: 8px"/>{{ item.viewCount }}
                              </span>
                            <span>
                                <component v-bind:is="'LikeOutlined'" style="margin-right: 8px"/>{{ item.voteCount }}
                              </span>
                        </template>
                        <a-list-item-meta :description="item.description">
                            <template #title>
                                <router-link :to="'/doc?ebookId=' + item.id">
                                    {{item.name}}
                                </router-link>
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
    import {message} from "ant-design-vue";
    import {Tool} from "@/util/tool";

    export default defineComponent({
        name: 'Home',
        setup() {
            console.log("setup");
            const ebooks = ref();   //这个是一个响应式的数据！
            const isShowWelcome = ref(true);  //用于判断显示欢迎页面还是列表展示页面,初始默认为true，welcome界面
            let categoryId2 = 0;   //用于获取二级分类类型便于发送分类查询请求的中间介质变量

            const handleQueryebook = () => {
                axios.get("/ebook/list", {
                    params: {
                        page: 1,
                        size: 1000, //一次性将所有的数据全都查出来
                        categoryId2: categoryId2
                    }
                }).then((response) => {
                    const data = response.data; //此data就是后端中的commonResp
                    ebooks.value = data.content.list;
                });
            };

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

            const handleClick = (value: any) => {
                if (value.key === 'welcome') {
                    isShowWelcome.value = true;
                } else {
                    categoryId2 = value.key;
                    isShowWelcome.value = false;
                    handleQueryebook();
                }
            };

            //生命周期初始化方法
            onMounted(() => {
                handleQueryCategory();
            });

            const pagination = {
                onChange: (page: number) => {
                    console.log(page);
                },
                pageSize: 3,
            };

            return {
                ebooks,
                pagination,
                handleClick,
                level1,
                isShowWelcome
            }
        }
    });
</script>


