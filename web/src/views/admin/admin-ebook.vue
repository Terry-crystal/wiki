<template>
    <a-layout>
        <a-layout-content
                :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
        >
            <p>
                <a-button type="primary" @click="add()" size="large">
                    新增
                </a-button>
            </p>

            <a-table
                    :columns="columns"
                    :row-key="record => record.id"
                    :data-source="ebooks"
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
                                @confirm="handleDelete(record.id)"
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
            <a-form-item label="分类一">
                <a-input v-model:value="ebook.category1Id"/>
            </a-form-item>
            <a-form-item label="分类二">
                <a-input v-model:value="ebook.category2Id"/>
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

    export default defineComponent({
        name: 'AdminEbook',
        setup() {
            const ebooks = ref();
            const pagination = ref({
                current: 1,
                pageSize: 4,
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
                    title: '分类一',
                    key: 'category1Id',
                    dataIndex: 'category1Id'
                },
                {
                    title: '分类二',
                    dataIndex: 'category2Id'
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
                axios.get("/ebook/list", {
                    params: {
                        page: params.page,
                        size: params.size
                    }
                }).then((response) => {
                    loading.value = false;
                    const data = response.data;
                    ebooks.value = data.content.list;

                    // 重置分页按钮
                    pagination.value.current = params.page;
                    pagination.value.total = data.content.total;
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
            const ebook = ref({});
            const modalVisible = ref(false);
            const modalLoading = ref(false);
            const handleModalOk = () => {
                modalLoading.value = true;  //在保存的时候先显示一个保存的效果

                //在对编辑好的电子书信息进行保存,发保存请求
                axios.post("/ebook/save", ebook.value).then((response) => {
                    const data = response.data;//data=CommonResp
                    if (data.success) {
                        modalLoading.value = false; //拿到结果之后去掉model框
                        modalVisible.value = false; //拿到结果之后才会取消loading效果

                        //重新加载列表
                        handleQuery({
                            page: pagination.value.current, //重新查询当前这个分页组件所在的页码
                            size: pagination.value.pageSize,
                        });
                    }
                });
            };

            /**
             * 编辑
             */
            const edit = (record: any) => {
                modalVisible.value = true;  //显示模糊框
                ebook.value = record;   //从record响应式变量中获取数据填充到模糊框
                console.log("这是我在测试数据：" + record.id)
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
            const handleDelete = (id: number) => {   //Long类型对应前端类型为number类型
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

            onMounted(() => {
                handleQuery({
                    page: 1,
                    size: pagination.value.pageSize,
                });
            });

            return {
                ebooks,
                pagination,
                columns,
                loading,
                handleTableChange,

                edit,
                add,

                ebook,
                modalVisible,
                modalLoading,
                handleModalOk,

                handleDelete
            }
        }
    });
</script>

<style scoped>
    img {
        width: 50px;
        height: 50px;
    }
</style>
