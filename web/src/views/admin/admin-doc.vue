<template>
    <a-layout>
        <a-layout-content
                :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
        >

            <p>
                <a-form layout="inline" :model="param">

                    <a-form-item>
                        <a-button type="primary" @click="add()">新增</a-button>
                    </a-form-item>

                    <a-form-item>
                        <a-button type="primary" @click="handleQuery()">刷新查询
                        </a-button>
                    </a-form-item>

                </a-form>
            </p>


            <a-table
                    :columns="columns"
                    :row-key="record => record.id"
                    :data-source="level1"
                    :loading="loading"
                    :pagination="false"
            >
                <template #cover="{ text: cover }">
                    <img v-if="cover" :src="cover" alt="avatar"/>
                </template>

                <template v-slot:action="{ text, record }">
                    <a-space size="small">

                        <a-button type="primary" @click="edit(record)">
                            编辑
                        </a-button>

                        <a-popconfirm
                                title="删除后不可恢复，确认删除？"
                                ok-text="是"
                                cancel-text="否"
                                @confirm="handleDocDelete(record.id)"
                        >
                            <a-button type="danger">删除</a-button>
                        </a-popconfirm>

                    </a-space>
                </template>
            </a-table>
        </a-layout-content>
    </a-layout>

    <a-modal
            title="文档表单"
            v-model:visible="modalVisible"
            :confirm-loading="modalLoading"
            @ok="handleModalOk"
    >

        <a-form :model="doc" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
            <a-form-item label="名称">
                <a-input v-model:value="doc.name"/>
            </a-form-item>
            <a-form-item label="父文档">
                <a-select
                        ref="select"
                        v-model:value="doc.parent"
                >
                    <a-select-option value="0">无</a-select-option>
                    <!--在属性中使用变量，直接使用就可以了-->
                    <a-select-option v-for="c in level1" :key="c.id" :value="c.id" :disabled="doc.id === c.id">
                        <!--选项和自身一样的情况下不能选-->
                        {{c.name}}  <!--在节点中使用变量，需要使用两个大括号-->
                    </a-select-option>
                </a-select>
            </a-form-item>
            <a-form-item label="顺序">
                <a-input v-model:value="doc.sort"/>
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
        name: 'AdminDoc',
        setup() {

            const param = ref();    //设置响应式变量
            param.value = {};   //初始化给个空对象
            const docs = ref();
            const loading = ref(false);


            const columns = [
                {
                    title: '名称',
                    dataIndex: 'name'
                },
                {
                    title: '父文档',
                    key: 'parent',
                    dataIndex: 'parent'
                },
                {
                    title: '顺序',
                    dataIndex: 'sort'
                },
                {
                    title: 'Action',
                    key: 'action',
                    slots: {customRender: 'action'}
                }
            ];

            /**
             * 一级文档树，children属性就是二级文档
             * [{
             *   id: "",
             *   name: "",
             *   children: [{
             *     id: "",
             *     name: "",
             *   }]
             * }]
             */
            const level1 = ref();

            /**
             * 数据查询
             **/
            const handleQuery = () => {
                loading.value = true;
                axios.get("/doc/all").then((response) => {
                    loading.value = false;
                    const data = response.data;
                    if (data.success) {
                        docs.value = data.content;
                        level1.value = [];
                        level1.value = Tool.array2Tree(docs.value, 0);
                    } else {
                        message.error(data.message);
                    }
                });
            };

            onMounted(() => {
                handleQuery();
            });

            // -------- 表单 ---------
            const doc = ref({});
            const modalVisible = ref(false);
            const modalLoading = ref(false);
            const handleModalOk = () => {
                modalLoading.value = true;  //在保存的时候先显示一个保存的效果

                //在对编辑好的文档信息进行保存,发保存请求
                axios.post("/doc/save", doc.value).then((response) => {
                    modalLoading.value = false; //只要后端有返回，就把效果去掉

                    const data = response.data;//data=CommonResp
                    if (data.success) {
                        modalVisible.value = false; //拿到结果之后才会取消loading效果
                        //重新加载列表
                        handleQuery();
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
                doc.value = Tool.copy(record);   //从record响应式变量中先复制对象再填充获取数据填充到模糊框
            };

            /**
             * 新增
             */
            const add = () => {
                modalVisible.value = true;  //显示模糊框
                doc.value = {};   //将模糊框内部数据清空
            };

            /**
             * 删除
             */
            const handleDocDelete = (id: string) => {   //Long类型对应前端类型为number类型
                axios.delete("/doc/delete/" + id).then((response) => {
                    const data = response.data;//data=CommonResp
                    if (data.success) {
                        //重新加载列表
                        handleQuery();
                    }
                });
            };

            onMounted(() => {
                handleQuery();
            });

            return {
                param,
                level1,
                columns,
                loading,

                edit,
                add,
                handleDocDelete,

                doc,
                modalVisible,
                modalLoading,
                handleModalOk,
                handleQuery
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
