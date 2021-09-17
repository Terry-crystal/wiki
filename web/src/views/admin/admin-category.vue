<template>
    <a-layout>
        <a-layout-content
                :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
        >

            <p>
                <a-form layout="inline" :model="param">
                    <a-form-item>
                        <a-input v-model:value="param.name" placeholder="名称"></a-input>
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
                    :row-key="record => record.id"
                    :data-source="categorys"
                    :pagination="pagination"
                    :loading="loading"
                    @change="handleTableChange"
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
                                @confirm="handleCategoryDelete(record.id)"
                        >
                            <a-button type="danger">删除</a-button>
                        </a-popconfirm>

                    </a-space>
                </template>
            </a-table>
        </a-layout-content>
    </a-layout>

    <a-modal
            title="分类表单"
            v-model:visible="modalVisible"
            :confirm-loading="modalLoading"
            @ok="handleModalOk"
    >

        <a-form :model="category" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
            <a-form-item label="名称">
                <a-input v-model:value="category.name"/>
            </a-form-item>
            <a-form-item label="父分类">
                <a-input v-model:value="category.parent"/>
            </a-form-item>
            <a-form-item label="顺序">
                <a-input v-model:value="category.sort"/>
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
        name: 'AdminCategory',
        setup() {

            const param = ref();    //设置响应式变量
            param.value = {};   //初始化给个空对象
            const categorys = ref();
            const pagination = ref({
                current: 1,
                pageSize: 10,
                total: 0
            });
            const loading = ref(false);

            const columns = [
                {
                    title: '名称',
                    dataIndex: 'name'
                },
                {
                    title: '父分类',
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
             * 数据查询
             **/
            const handleQuery = (params: any) => {
                loading.value = true;
                axios.get("/category/list", {
                    params: {
                        page: params.page,
                        size: params.size,
                        name: param.value.name
                    }
                }).then((response) => {
                    loading.value = false;
                    const data = response.data;
                    if (data.success) {
                        categorys.value = data.content.list;

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

            onMounted(() => {
                handleQuery({
                    page: 1,
                    size: pagination.value.pageSize
                });
            });

            // -------- 表单 ---------
            const category = ref({});
            const modalVisible = ref(false);
            const modalLoading = ref(false);
            const handleModalOk = () => {
                modalLoading.value = true;  //在保存的时候先显示一个保存的效果

                //在对编辑好的分类信息进行保存,发保存请求
                axios.post("/category/save", category.value).then((response) => {
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
                category.value = Tool.copy(record);   //从record响应式变量中先复制对象再填充获取数据填充到模糊框
            };

            /**
             * 新增
             */
            const add = () => {
                modalVisible.value = true;  //显示模糊框
                category.value = {};   //将模糊框内部数据清空
            };

            /**
             * 删除
             */
            const handleCategoryDelete = (id: string) => {   //Long类型对应前端类型为number类型
                axios.delete("/category/delete/" + id).then((response) => {
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

            onMounted(() => {
                handleQuery({
                    page: 1,
                    size: pagination.value.pageSize,
                });
            });

            return {
                param,
                categorys,
                pagination,
                columns,
                loading,
                handleTableChange,

                edit,
                add,
                handleCategoryDelete,

                category,
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
