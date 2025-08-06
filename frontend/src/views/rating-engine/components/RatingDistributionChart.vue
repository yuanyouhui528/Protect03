<template>
  <div class="rating-distribution-chart">
    <div ref="chartRef" class="chart-container"></div>
    <div v-if="loading" class="chart-loading">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>加载中...</span>
    </div>
    <div v-if="!loading && (!data || data.length === 0)" class="chart-empty">
      <el-icon><DocumentRemove /></el-icon>
      <span>暂无数据</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { Loading, DocumentRemove } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

// 组件属性
const props = defineProps({
  // 分布数据
  data: {
    type: Array,
    default: () => []
  },
  // 是否加载中
  loading: {
    type: Boolean,
    default: false
  },
  // 图表高度
  height: {
    type: String,
    default: '300px'
  },
  // 图表类型 (pie/bar)
  chartType: {
    type: String,
    default: 'pie',
    validator: (value) => ['pie', 'bar'].includes(value)
  }
})

// 响应式数据
const chartRef = ref(null)
const chartInstance = ref(null)

// 生命周期
onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (chartInstance.value) {
    chartInstance.value.dispose()
  }
  window.removeEventListener('resize', handleResize)
})

// 监听数据变化
watch(
  () => [props.data, props.chartType],
  () => {
    updateChart()
  },
  { deep: true }
)

// 方法定义

/**
 * 初始化图表
 */
const initChart = async () => {
  await nextTick()
  if (!chartRef.value) return

  chartInstance.value = echarts.init(chartRef.value)
  updateChart()
}

/**
 * 更新图表
 */
const updateChart = () => {
  if (!chartInstance.value || props.loading) return

  const option = props.chartType === 'pie' ? getPieOption() : getBarOption()
  chartInstance.value.setOption(option, true)
}

/**
 * 获取饼图配置
 */
const getPieOption = () => {
  const colors = {
    A: '#52c41a',
    B: '#1890ff',
    C: '#faad14',
    D: '#ff4d4f'
  }

  const seriesData = props.data.map(item => ({
    name: `${item.rating}级`,
    value: item.count,
    itemStyle: {
      color: colors[item.rating] || '#d9d9d9'
    },
    label: {
      formatter: '{b}\n{c}个\n({d}%)'
    }
  }))

  return {
    title: {
      text: '评级分布',
      left: 'center',
      top: 20,
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold',
        color: '#2c3e50'
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c}个 ({d}%)'
    },
    legend: {
      orient: 'horizontal',
      bottom: 10,
      data: props.data.map(item => `${item.rating}级`)
    },
    series: [
      {
        name: '评级分布',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['50%', '55%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          position: 'outside',
          fontSize: 12,
          fontWeight: 'bold'
        },
        labelLine: {
          show: true
        },
        data: seriesData,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        animationType: 'scale',
        animationEasing: 'elasticOut',
        animationDelay: function (idx) {
          return Math.random() * 200
        }
      }
    ]
  }
}

/**
 * 获取柱状图配置
 */
const getBarOption = () => {
  const colors = {
    A: '#52c41a',
    B: '#1890ff',
    C: '#faad14',
    D: '#ff4d4f'
  }

  const xAxisData = props.data.map(item => `${item.rating}级`)
  const seriesData = props.data.map(item => ({
    value: item.count,
    itemStyle: {
      color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
        { offset: 0, color: colors[item.rating] || '#d9d9d9' },
        { offset: 1, color: echarts.color.modifyAlpha(colors[item.rating] || '#d9d9d9', 0.6) }
      ])
    }
  }))

  return {
    title: {
      text: '评级分布',
      left: 'center',
      top: 20,
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold',
        color: '#2c3e50'
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: '{a} <br/>{b}: {c}个'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: xAxisData,
      axisTick: {
        alignWithLabel: true
      },
      axisLabel: {
        fontSize: 12,
        fontWeight: 'bold'
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}个'
      }
    },
    series: [
      {
        name: '线索数量',
        type: 'bar',
        barWidth: '60%',
        data: seriesData,
        label: {
          show: true,
          position: 'top',
          fontSize: 12,
          fontWeight: 'bold',
          formatter: '{c}个'
        },
        animationDelay: function (idx) {
          return idx * 100
        }
      }
    ],
    animationEasing: 'elasticOut',
    animationDelayUpdate: function (idx) {
      return idx * 5
    }
  }
}

/**
 * 处理窗口大小变化
 */
const handleResize = () => {
  if (chartInstance.value) {
    chartInstance.value.resize()
  }
}

// 暴露方法给父组件
defineExpose({
  resize: handleResize,
  getInstance: () => chartInstance.value
})
</script>

<style scoped>
.rating-distribution-chart {
  position: relative;
  width: 100%;
  height: v-bind(height);
}

.chart-container {
  width: 100%;
  height: 100%;
}

.chart-loading,
.chart-empty {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 14px;
}

.chart-loading .el-icon,
.chart-empty .el-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.chart-loading .el-icon {
  color: #409eff;
}

.chart-empty .el-icon {
  color: #c0c4cc;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .rating-distribution-chart {
    height: 250px;
  }
}
</style>