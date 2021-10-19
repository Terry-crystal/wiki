<template>
    <div>
        <a-row>
            <a-col :span="24">
                <a-card>
                    <a-row>
                        <a-col :span="8">
                            <a-statistic title="总阅读量" :value="statistic.viewCount"/>
                            <template #suffix>
                                <UserOutlined/>
                            </template>
                        </a-col>
                        <a-col :span="8">
                            <a-statistic title="总点赞数" :value="statistic.voteCount"/>
                            <template #suffix>
                                <like-outlined/>
                            </template>
                        </a-col>
                        <a-col :span="8">
                            <a-statistic title="点赞率"
                                         :precision="2"
                                         :value="statistic.voteCount/statistic.viewCount*100"
                                         suffix="%"
                                         :value-style="{color:'#cf1322'}"
                            />
                            <template #suffix>
                                <UserOutlined/>
                            </template>
                        </a-col>
                    </a-row>
                </a-card>
            </a-col>
        </a-row>
        <br>
        <a-row :gutter="16">
            <a-col :span="12">
                <a-card>
                    <a-row>
                        <a-col :span="12">
                            <a-statistic
                                    title="今日阅读"
                                    :value="statistic.todayViewCount"
                                    style="margin-right: 50px"
                            >
                                <template #suffix>
                                    <UserOutlined/>
                                </template>
                            </a-statistic>
                        </a-col>
                        <a-col :span="12">
                            <a-statistic
                                    title="今日点赞"
                                    :value="statistic.todayVoteCount"
                            >
                                <template #suffix>
                                    <like-outlined/>
                                </template>
                            </a-statistic>
                        </a-col>
                    </a-row>
                </a-card>
            </a-col>
            <a-col :span="12">
                <a-card>
                    <a-row>
                        <a-col :span="12">
                            <a-statistic
                                    title="预计今日阅读"
                                    :value="statistic.todayViewIncrease"
                                    :value-style="{ color: '#0000ff' }"
                            >
                                <template #suffix>
                                    <UserOutlined/>
                                </template>
                            </a-statistic>
                        </a-col>
                        <a-col :span="12">
                            <a-statistic
                                    title="预计今日阅读增长"
                                    :value="statistic.todayViewIncreaseRateAbs"
                                    :precision="2"
                                    suffix="%"
                                    class="demo-class"
                                    :value-style="statistic.todayViewIncreaseRate < 0 ? { color: '#3f8600' } : { color: '#cf1322' }"
                            >
                                <!--根据预计增长率变换不同的箭头-->
                                <template #prefix>
                                    <arrow-down-outlined v-if="statistic.todayViewIncreaseRate < 0"/>
                                    <arrow-up-outlined v-if="statistic.todayViewIncreaseRate >= 0"/>
                                </template>
                            </a-statistic>
                        </a-col>
                    </a-row>
                </a-card>
            </a-col>
        </a-row>
        <br>
        <a-row>
            <a-col :span="24">
                <div id="main" style="width: 100%;height:300px;"></div>
            </a-col>
        </a-row>
    </div>
</template>


<script lang="ts">
    import {defineComponent, onMounted, ref} from 'vue';
    import axios from 'axios';

    declare let echarts: any;    //因为这个是vue外的，所以需要声明

    /*给组件起了一个名字*/
    export default defineComponent({
        name: 'the-welcome',

        setup() {

            const statistic = ref();
            statistic.value = {};   //定义响应式变量

            //获取统计报表的数据，发请求
            const getStatistic = () => {
                axios.get('/ebook-snapshot/get-statistic').then((response) => {
                    const data = response.data;
                    if (data.success) {
                        const statisticResp = data.content; //接口返回的数据是数组，使用一个临时变量存储，接口返回的数据是【昨天，今天】的数据
                        statistic.value.viewCount = statisticResp[1].viewCount; //到今天为止的总阅读数
                        statistic.value.voteCount = statisticResp[1].voteCount; //到今天为止的总投票数
                        statistic.value.todayViewCount = statisticResp[1].viewIncrease; //今天的阅读数
                        statistic.value.todayVoteCount = statisticResp[1].voteIncrease; //今天的投票数

                        // 按分钟计算当前时间点，占一天的百分比
                        const now = new Date();
                        const nowRate = (now.getHours() * 60 + now.getMinutes()) / (60 * 24);   //计算今天已经过去了百分之多少
                        statistic.value.todayViewIncrease = parseInt(String(statisticResp[1].viewIncrease / nowRate));  //使用今天的阅读数除于今天的一天百分比得到 预计今天的阅读数

                        // todayViewIncreaseRate：今日预计增长率 =（今天预计-昨天阅读数）/昨天阅读数
                        statistic.value.todayViewIncreaseRate = (statistic.value.todayViewIncrease - statisticResp[0].viewIncrease) / statisticResp[0].viewIncrease * 100;
                        statistic.value.todayViewIncreaseRateAbs = Math.abs(statistic.value.todayViewIncreaseRate);//todayViewIncreaseRateAbs的值是绝对值
                    }
                })
            };


            /**
             * 渲染30天趋势图表数据
             * @param list
             */
            const init30DayEcharts = (list: any) => {
                // 基于准备好的dom，初始化echarts实例
                const myChart = echarts.init(document.getElementById('main'));

                const xAxis = [];   //横轴数据
                const seriesView = [];  //纵轴数据
                const seriesVote = [];  //纵轴数据

                for (let i = 0; i < list.length; i++) {
                    const record = list[i];
                    xAxis.push(record.date);
                    seriesView.push(record.viewIncrease);
                    seriesVote.push(record.voteIncrease);
                }

                // 指定图表的配置项和数据
                const option = {
                    title: {
                        text: '30天趋势图'
                    },
                    tooltip: {
                        trigger: 'axis'
                    },
                    legend: {
                        data: ['总阅读量', '总点赞数']
                    },
                    grid: {
                        left: '1%',
                        right: '3%',
                        bottom: '3%',
                        containLabel: true

                    },
                    toolbox: {
                        feature: {
                            saveImage: {}
                        }
                    },
                    xAxis: {
                        type: 'category',
                        boundaryGap: false,
                        data: xAxis //x轴放这个变量
                    },
                    yAxis: {
                        type: 'value'
                    },
                    series: [
                        {
                            name: '总阅读数',
                            type: 'line',
                            data: seriesView,
                            smooth: true
                        },
                        {
                            name: '总点赞量',
                            type: 'line',
                            data: seriesVote,
                            smooth: true
                        }
                    ]
                };

                // 使用刚指定的配置项和数据显示图表。
                myChart.setOption(option);
            };

            /**
             * 发网络请求获取30天数据并且进行初始化渲染图表
             */
            const get30DayStatistic = () => {
                axios.get('ebook-snapshot/get-30-statistic').then((response) => {
                    const data = response.data;
                    if (data.success) {
                        const statisticList = data.content;

                        init30DayEcharts(statisticList);    //拿到数据之后开始渲染图表
                    }
                })
            };

            onMounted(() => {
                getStatistic();
                get30DayStatistic();
            });

            return {
                statistic
            }

        }

    });
</script>

