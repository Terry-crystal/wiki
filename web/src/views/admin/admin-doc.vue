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
                <!--replaceFileds 替换treeNode中title，value，key，children字段为treeData中的字段-->
                <a-tree-select
                        v-model:value="doc.parent"
                        style="width: 100%"
                        :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                        :tree-data="treeSelectData"
                        placeholder="请选择父文档"
                        tree-default-expand-all
                        :replaceFields="{title:'name',key:'id',value:'id'}"
                >
                </a-tree-select>
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
    import {useRoute} from "vue-router";

    export default defineComponent({
        name: 'AdminDoc',
        setup() {

            const route = useRoute(); //使用内置方法可以得到route并且使用其得到路由的各种信息
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
                level1.value = [];  //把表格数据清空
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

            //因为树选择组件的属性状态，会随当前编辑的节点而变化，所以单独声明一个响应式变量
            const treeSelectData = ref();
            treeSelectData.value = [];

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
             * 将某节点及其子孙节点全部置为disabled
             */
            const setDisable = (treeSelectData: any, id: any) => {
                // console.log(treeSelectData, id);
                // 遍历数组，即遍历某一层节点
                for (let i = 0; i < treeSelectData.length; i++) {
                    const node = treeSelectData[i];
                    if (node.id === id) {
                        // 如果当前节点就是目标节点
                        console.log("disabled", node);
                        // 将目标节点设置为disabled
                        node.disabled = true;

                        // 遍历所有子节点，将所有子节点全部都加上disabled
                        const children = node.children;
                        if (Tool.isNotEmpty(children)) {
                            for (let j = 0; j < children.length; j++) {
                                setDisable(children, children[j].id)
                            }
                        }
                    } else {
                        // 如果当前节点不是目标节点，则到其子节点再找找看。
                        const children = node.children;
                        if (Tool.isNotEmpty(children)) {
                            setDisable(children, id);
                        }
                    }
                }
            };


            const ids: Array<string> = [];
            /**
             * 查找整根树枝
             */
            const getDeleteIds = (treeSelectData: any, id: any) => {
                // console.log(treeSelectData, id);
                // 遍历数组，即遍历某一层节点
                for (let i = 0; i < treeSelectData.length; i++) {
                    const node = treeSelectData[i];
                    if (node.id === id) {
                        // 如果当前节点就是目标节点
                        console.log("delete", node);
                        // 将目标节点放入结果集
                        ids.push(id);

                        // 遍历所有子节点
                        const children = node.children;
                        if (Tool.isNotEmpty(children)) {
                            for (let j = 0; j < children.length; j++) {
                                getDeleteIds(children, children[j].id)
                            }
                        }
                    } else {
                        // 如果当前节点不是目标节点，则到其子节点再找找看。
                        const children = node.children;
                        if (Tool.isNotEmpty(children)) {
                            getDeleteIds(children, id);
                        }
                    }
                }
            };


            /**
             * 编辑
             */
            const edit = (record: any) => {
                modalVisible.value = true;  //显示模糊框
                doc.value = Tool.copy(record);   //从record响应式变量中先复制对象再填充获取数据填充到模糊框

                // 不能选择当前节点及其所有子孙节点，作为父节点，会使树断开
                treeSelectData.value = Tool.copy(level1.value);
                setDisable(treeSelectData.value, record.id);    //将对应节点下属性隐藏，达到不可编辑的状态

                // 为选择树添加一个"无"
                treeSelectData.value.unshift({id: 0, name: '无'});   //往数组的前面添加一个这样的节点
            };

            /**
             * 新增
             */
            const add = () => {
                modalVisible.value = true;  //显示模糊框
                doc.value = {
                    ebookId: route.query.ebookId
                }; //获取前面页面传递过来的参数并且将其赋值到表单的数据源中

                treeSelectData.value = Tool.copy(level1.value);
                // 为选择树添加一个"无"
                treeSelectData.value.unshift({id: 0, name: '无'});
            };

            /**
             * 删除s
             */
            const handleDocDelete = (id: string) => {
                //Long类型对应前端类型为number类型
                getDeleteIds(level1.value, id);
                axios.delete("/doc/delete/" + ids.join(",")).then((response) => {
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
                handleQuery,
                treeSelectData
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
