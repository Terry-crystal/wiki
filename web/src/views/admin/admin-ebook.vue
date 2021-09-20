<template>
    <a-layout>
        <a-layout-content
                :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
        >

            <p>
                <a-form layout="inline" :model="param">
                    <a-form-item>
                        <a-input v-model:value="param.name" placeholder="输入要查询的书籍"></a-input>
                    </a-form-item>

                    <a-form-item>
                        <a-button type="primary" @click="handleQuery({
                            page:1,
                            size:pagination.pageSize
                        })">查询
                        </a-button>
                    </a-form-item>

                    <a-form-item>
                        <a-button type="primary" @click="add()">新增</a-button>
                    </a-form-item>

                </a-form>
            </p>


            <a-table
                    :columns="columns"
                    :row-key=" record=> record.id"
                    :data-source="ebooks"
                    :pagination="pagination"
                    :loading="loading"
                    @change="handleTableChange"
            >
                <template #cover="{ text: cover }">
                    <img v-if="cover" :src="cover" alt="avatar"/>
                </template>

                <!--新增一个渲染 写一个方法，通过id获得名称-->
                <template v-slot:category="{ text, record }">

                    <span>{{ getCategoryName(record.category1Id) }} / {{ getCategoryName(record.category2Id) }}</span>

                </template>

                <template v-slot:action="{ text, record }">
                    <a-space size="small">

                        <router-link :to="'/admin/doc?ebookId='+record.id">
                            <a-button type="primary">
                                文档管理
                            </a-button>
                        </router-link>

                        <a-button type="primary" @click="edit(record)">
                            编辑
                        </a-button>

                        <a-popconfirm
                                title="删除后不可恢复，确认删除？"
                                ok-text="是"
                                cancel-text="否"
                                @confirm="handleEbookDelete(record.id)"
                        >
                            <a-button type="danger">删除</a-button>
                        </a-popconfirm>

                    </a-space>
                </template>
            </a-table>
        </a-layout-content>
    </a-layout>

    <a-modal
            title="电子书表单"
            v-model:visible="modalVisible"
            :confirm-loading="modalLoading"
            @ok="handleModalOk"
    >

        <a-form :model="ebook" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
            <a-form-item label="封面">
                <a-input v-model:value="ebook.cover"/>
            </a-form-item>
            <a-form-item label="名称">
                <a-input v-model:value="ebook.name"/>
            </a-form-item>
            <a-form-item label="分类">
                <!--v-model:value 绑定一个value，而且需要绑定的时响应式变量-->
                <!--field-names label 显示的是哪个字段  value是我实际要取的值 id，name，children就是level1的属性-->
                <!--options 中就是树分支数组-->
                <a-cascader
                        v-model:value="categoryIds"
                        :field-names="{label:'name', value:'id', children:'children' }"
                        :options="level1"
                />
            </a-form-item>
            <a-form-item label="描述">
                <a-input v-model:value="ebook.description" type="textarea"/>
            </a-form-item>
        </a-form>

    </a-modal>

</template>

<script lang="ts">
    import {defineComponent, onMounted, ref} from 'vue';
    import axios from 'axios';
    import {message} from "ant-design-vue";
    import {Tool} from "@/util/tool";

    export default defineComponent({
        name: 'AdminEbook',
        setup() {

            const param = ref();    //设置响应式变量
            param.value = {};   //初始化给个空对象
            const ebooks = ref();
            const pagination = ref({
                current: 1,
                pageSize: 10,
                total: 0
            });
            const loading = ref(false);

            const columns = [
                {
                    title: '封面',
                    dataIndex: 'cover',
                    slots: {customRender: 'cover'}
                },
                {
                    title: '名称',
                    dataIndex: 'name'
                },
                {
                    title: '分类',
                    slots: {customRender: 'category'}   //带一个渲染，就是上面的渲染
                },
                {
                    title: '文档数',
                    dataIndex: 'docCount'
                },
                {
                    title: '阅读数',
                    dataIndex: 'viewCount'
                },
                {
                    title: '点赞数',
                    dataIndex: 'voteCount'
                },
                {
                    title: 'Action',
                    key: 'action',
                    slots: {customRender: 'action'}
                }
            ];

            /**
             * 数据查询
             **/
            const handleQuery = (params: any) => {
                loading.value = true;
                ebooks.value = [];    //清空现有数据，就不会出现编辑之后还是原来数据的清空
                axios.get("/ebook/list", {
                    params: {
                        page: params.page,
                        size: params.size,
                        name: param.value.name
                    }
                }).then((response) => {
                    loading.value = false;
                    const data = response.data;
                    if (data.success) {
                        ebooks.value = data.content.list;

                        // 重置分页按钮
                        pagination.value.current = params.page;
                        pagination.value.total = data.content.total;
                    } else {
                        message.error(data.message);
                    }
                });
            };

            /**
             * 表格点击页码时触发
             */
            const handleTableChange = (pagination: any) => {
                console.log("看看自带的分页参数都有啥：" + pagination);
                handleQuery({
                    page: pagination.current,
                    size: pagination.pageSize
                });
            };

            // -------- 表单 ---------
            const categoryIds = ref();    //数组【100，101对应：前端开发/vue 这样的数据】
            const ebook = ref();
            const modalVisible = ref(false);
            const modalLoading = ref(false);
            const handleModalOk = () => {
                modalLoading.value = true;  //在保存的时候先显示一个保存的效果

                ebook.value.category1Id = categoryIds.value[0];
                ebook.value.category2Id = categoryIds.value[1];

                //在对编辑好的电子书信息进行保存,发保存请求
                axios.post("/ebook/save", ebook.value).then((response) => {
                    modalLoading.value = false; //只要后端有返回，就把效果去掉

                    const data = response.data;//data=CommonResp
                    if (data.success) {
                        modalVisible.value = false; //拿到结果之后才会取消loading效果

                        //重新加载列表
                        handleQuery({
                            page: pagination.value.current, //重新查询当前这个分页组件所在的页码
                            size: pagination.value.pageSize,
                        });
                    } else {
                        message.error(data.message);
                    }
                });
            };

            /**
             * 编辑
             */
            const edit = (record: any) => {
                modalVisible.value = true;  //显示模糊框
                ebook.value = Tool.copy(record);   //从record响应式变量中先复制对象再填充获取数据填充到模糊框
                categoryIds.value = [ebook.value.category1Id, ebook.value.category2Id]  //将ebook中的第一和第二分类组合起来给前面渲染，显示出一级和二级分类
            };

            /**
             * 新增
             */
            const add = () => {
                modalVisible.value = true;  //显示模糊框
                ebook.value = {};   //将模糊框内部数据清空
            };

            /**
             * 删除
             */
            const handleEbookDelete = (id: string) => {   //Long类型对应前端类型为number类型
                axios.delete("/ebook/delete/" + id).then((response) => {
                    const data = response.data;//data=CommonResp
                    if (data.success) {
                        //重新加载列表
                        handleQuery({
                            page: pagination.value.current, //重新查询当前这个分页组件所在的页码
                            size: pagination.value.pageSize,
                        });
                    }
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
                loading.value = true;
                axios.get("/category/all").then((response) => {
                    loading.value = false;
                    const data = response.data;
                    if (data.success) {
                        categorys = data.content;
                        level1.value = [];
                        level1.value = Tool.array2Tree(categorys, 0);

                        //记载分类后，再加载电子书，否则如果分类树加载很慢，则电子书渲染会报错
                        handleQuery({
                            page: 1,
                            size: pagination.value.pageSize,
                        });
                    } else {
                        message.error(data.message);
                    }
                });
            };

            /**
             * 获取分类名字，使用逻辑循环对比得到分类id名称
             * @param cid   分类id
             * result 返回名称
             */
            const getCategoryName = (cid: number) => {
                // console.log("这是："+cid)
                let result = "";
                categorys.forEach((item: any) => {
                    if (item.id === cid) {
                        // return item.name; // 注意，这里直接return不起作用
                        result = item.name;
                    }
                });
                return result;
            };

            onMounted(() => {
                handleQueryCategory();  //在初始化的时候把所有的分类查出来
            });

            return {
                param,
                ebooks,
                pagination,
                columns,
                loading,
                handleTableChange,
                handleQuery,
                getCategoryName,

                edit,
                add,

                ebook,
                modalVisible,
                modalLoading,
                handleModalOk,
                categoryIds,
                level1,

                handleEbookDelete,
            };
        }
    });
</script>

<style scoped>
    img {
        width: 50px;
        height: 50px;
    }
</style>
