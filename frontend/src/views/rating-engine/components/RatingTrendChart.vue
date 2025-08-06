<template>
  <div class="rating-trend-chart">
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
  // 趋势数据
  data: {
    type: Array,
    default: () => []
  },
  // 时间周期 (7d/30d/90d)
  period: {
    type: String,
    default: '30d',
    validator: (value) => ['7d', '30d', '90d'].includes(value)
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
  // 显示类型 (count/score/both)
  displayType: {
    type: String,
    default: 'count',
    validator: (value) => ['count', 'score', 'both'].includes(value)
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
  () => [props.data, props.period, props.displayType],
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

  const option = getChartOption()
  chartInstance.value.setOption(option, true)
}

/**
 * 获取图表配置
 */
const getChartOption = () => {
  const colors = {
    A: '#52c41a',
    B: '#1890ff',
    C: '#faad14',
    D: '#ff4d4f'
  }

  // 处理数据
  const dates = [...new Set(props.data.map(item => item.date))].sort()
  const ratings = ['A', 'B', 'C', 'D']
  
  const series = []
  const legend = []

  if (props.displayType === 'count' || props.displayType === 'both') {
    // 添加数量趋势线
    ratings.forEach(rating => {
      const data = dates.map(date => {
        const item = props.data.find(d => d.date === date && d.rating === rating)
        return item ? item.count : 0
      })

      series.push({
        name: `${rating}级数量`,
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: {
          width: 3
        },
        itemStyle: {
          color: colors[rating]
        },
        areaStyle: {
          opacity: 0.1,
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: colors[rating] },
            { offset: 1, color: 'transparent' }
          ])
        },
        data: data,
        yAxisIndex: 0
      })
      
      legend.push(`${rating}级数量`)
    })
  }

  if (props.displayType === 'score' || props.displayType === 'both') {
    // 添加平均分趋势线
    const avgScoreData = dates.map(date => {
      const dayData = props.data.filter(d => d.date === date)
      if (dayData.length === 0) return 0
      
      const totalScore = dayData.reduce((sum, item) => sum + (item.averageScore || 0), 0)
      return (totalScore / dayData.length).toFixed(1)
    })

    series.push({
      name: '平均评分',
      type: 'line',
      smooth: true,
      symbol: 'diamond',
      symbolSize: 8,
      lineStyle: {
        width: 4,
        type: 'dashed'
      },
      itemStyle: {
        color: '#722ed1'
      },
      data: avgScoreData,
      yAxisIndex: props.displayType === 'both' ? 1 : 0
    })
    
    legend.push('平均评分')
  }

  // 构建配置
  const option = {
    title: {
      text: `评级趋势 (${getPeriodText()})`，
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
        type: 'cross',
        label: {
          backgroundColor: '#6a7985'
        }
      },
      formatter: function(params) {
        let result = `<div style="font-weight: bold; margin-bottom: 5px;">${params[0].axisValue}</div>`
        params.forEach(param => {
          const color = param.color
          const name = param.seriesName
          const value = param.value
          const unit = name.includes('数量') ? '个' : '分'
          result += `<div style="margin: 2px 0;">
            <span style="display: inline-block; width: 10px; height: 10px; background: ${color}; border-radius: 50%; margin-right: 5px;"></span>
            ${name}: <span style="font-weight: bold;">${value}${unit}</span>
          </div>`
        })
        return result
      }
    },
    legend: {
      data: legend,
      bottom: 10,
      type: 'scroll'
    },
    grid: {
      left: '3%',
      right: props.displayType === 'both' ? '8%' : '4%',
      bottom: '15%',
      top: '20%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates,
      axisLabel: {
        formatter: function(value) {
          return echarts.format.formatTime('MM-dd', value)
        }
      }
    },
    yAxis: getYAxisConfig(),
    series: series,
    animation: true,
    animationDuration: 1000,
    animationEasing: 'cubicOut'
  }

  return option
}

/**
 * 获取Y轴配置
 */
const getYAxisConfig = () => {
  if (props.displayType === 'both') {
    return [
      {
        type: 'value',
        name: '数量(个)',
        position: 'left',
        axisLabel: {
          formatter: '{value}个'
        }
      },
      {
        type: 'value',
        name: '评分',
        position: 'right',
        axisLabel: {
          formatter: '{value}分'
        },
        min: 0,
        max: 100
      }
    ]
  } else if (props.displayType === 'score') {
    return {
      type: 'value',
      name: '评分',
      axisLabel: {
        formatter: '{value}分'
      },
      min: 0,
      max: 100
    }
  } else {
    return {
      type: 'value',
      name: '数量(个)',
      axisLabel: {
        formatter: '{value}个'
      }
    }
  }
}

/**
 * 获取周期文本
 */
const getPeriodText = () => {
  const periodMap = {
    '7d': '近7天',
    '30d': '近30天',
    '90d': '近90天'
  }
  return periodMap[props.period] || '近30天'
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
.rating-trend-chart {
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
  .rating-trend-chart {
    height: 250px;
  }
}
</style>