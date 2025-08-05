<template>
  <div class="dashboard">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>数据看板</h2>
      <p class="page-description">实时监控线索流通数据和系统运行状态</p>
    </div>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon leads">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalLeads }}</div>
              <div class="stat-label">总线索数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon exchanges">
              <el-icon><Switch /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalExchanges }}</div>
              <div class="stat-label">交换次数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon users">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.activeUsers }}</div>
              <div class="stat-label">活跃用户</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon revenue">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalRevenue }}</div>
              <div class="stat-label">总收益</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-section">
      <!-- 线索趋势图 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>线索发布趋势</span>
              <el-select v-model="leadTrendPeriod" size="small" style="width: 100px">
                <el-option label="7天" value="7d" />
                <el-option label="30天" value="30d" />
                <el-option label="90天" value="90d" />
              </el-select>
            </div>
          </template>
          <div ref="leadTrendChart" class="chart-container"></div>
        </el-card>
      </el-col>
      
      <!-- 交换成功率图 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span>交换成功率</span>
          </template>
          <div ref="exchangeRateChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="charts-section">
      <!-- 线索评级分布 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span>线索评级分布</span>
          </template>
          <div ref="ratingDistChart" class="chart-container"></div>
        </el-card>
      </el-col>
      
      <!-- 行业分布 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span>行业分布</span>
          </template>
          <div ref="industryChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 最新动态 -->
    <el-row :gutter="20" class="activity-section">
      <el-col :span="24">
        <el-card class="activity-card">
          <template #header>
            <span>最新动态</span>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="activity in recentActivities"
              :key="activity.id"
              :timestamp="activity.time"
              :type="activity.type"
            >
              {{ activity.content }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { Document, Switch, User, Money } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import type { ECharts } from 'echarts'

// 统计数据
const stats = ref({
  totalLeads: 1234,
  totalExchanges: 567,
  activeUsers: 89,
  totalRevenue: '¥12.3万'
})

// 图表实例
const leadTrendChart = ref<HTMLElement>()
const exchangeRateChart = ref<HTMLElement>()
const ratingDistChart = ref<HTMLElement>()
const industryChart = ref<HTMLElement>()

// 图表配置
const leadTrendPeriod = ref('7d')

// 最新动态
const recentActivities = ref([
  {
    id: 1,
    content: '用户张三发布了一条A级线索',
    time: '2024-01-15 14:30',
    type: 'success'
  },
  {
    id: 2,
    content: '线索交换成功：制造业项目',
    time: '2024-01-15 13:45',
    type: 'primary'
  },
  {
    id: 3,
    content: '系统完成了100条线索的自动评级',
    time: '2024-01-15 12:20',
    type: 'info'
  },
  {
    id: 4,
    content: '新用户李四完成注册',
    time: '2024-01-15 11:15',
    type: 'success'
  }
])

// 初始化线索趋势图
const initLeadTrendChart = () => {
  if (!leadTrendChart.value) return
  
  const chart = echarts.init(leadTrendChart.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '发布数量',
        type: 'line',
        smooth: true,
        data: [12, 19, 15, 25, 22, 18, 20],
        itemStyle: {
          color: '#409EFF'
        }
      }
    ]
  }
  chart.setOption(option)
}

// 初始化交换成功率图
const initExchangeRateChart = () => {
  if (!exchangeRateChart.value) return
  
  const chart = echarts.init(exchangeRateChart.value)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    series: [
      {
        name: '交换状态',
        type: 'pie',
        radius: ['40%', '70%'],
        data: [
          { value: 335, name: '成功交换', itemStyle: { color: '#67C23A' } },
          { value: 234, name: '交换中', itemStyle: { color: '#E6A23C' } },
          { value: 135, name: '交换失败', itemStyle: { color: '#F56C6C' } }
        ]
      }
    ]
  }
  chart.setOption(option)
}

// 初始化评级分布图
const initRatingDistChart = () => {
  if (!ratingDistChart.value) return
  
  const chart = echarts.init(ratingDistChart.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['A级', 'B级', 'C级', 'D级']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '线索数量',
        type: 'bar',
        data: [120, 200, 150, 80],
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#409EFF' },
            { offset: 1, color: '#79bbff' }
          ])
        }
      }
    ]
  }
  chart.setOption(option)
}

// 初始化行业分布图
const initIndustryChart = () => {
  if (!industryChart.value) return
  
  const chart = echarts.init(industryChart.value)
  const option = {
    tooltip: {
      trigger: 'item'
    },
    series: [
      {
        name: '行业分布',
        type: 'pie',
        radius: '60%',
        data: [
          { value: 1048, name: '制造业' },
          { value: 735, name: '服务业' },
          { value: 580, name: '科技业' },
          { value: 484, name: '金融业' },
          { value: 300, name: '其他' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  chart.setOption(option)
}

// 组件挂载后初始化图表
onMounted(() => {
  nextTick(() => {
    initLeadTrendChart()
    initExchangeRateChart()
    initRatingDistChart()
    initIndustryChart()
  })
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.page-description {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.stats-cards {
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 8px;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-icon.leads {
  background: linear-gradient(135deg, #409EFF, #79bbff);
}

.stat-icon.exchanges {
  background: linear-gradient(135deg, #67C23A, #95d475);
}

.stat-icon.users {
  background: linear-gradient(135deg, #E6A23C, #ebb563);
}

.stat-icon.revenue {
  background: linear-gradient(135deg, #F56C6C, #f89898);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.charts-section {
  margin-bottom: 24px;
}

.chart-card {
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 300px;
  width: 100%;
}

.activity-section {
  margin-bottom: 24px;
}

.activity-card {
  border-radius: 8px;
}

:deep(.el-timeline-item__timestamp) {
  color: #909399;
  font-size: 12px;
}
</style>