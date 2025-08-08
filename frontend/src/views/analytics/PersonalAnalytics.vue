<template>
  <div class="personal-analytics">
    <div class="page-header">
      <h2>个人看板</h2>
      <p class="page-description">查看个人线索和交换数据分析</p>
    </div>
    
    <!-- 时间范围选择器 -->
    <el-card class="filter-card">
      <div class="filter-row">
        <div class="filter-item">
          <label>时间范围：</label>
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            @change="handleDateRangeChange"
          />
        </div>
        <div class="filter-item">
          <el-button-group>
            <el-button 
              v-for="preset in datePresets" 
              :key="preset.key"
              :type="selectedPreset === preset.key ? 'primary' : 'default'"
              @click="selectDatePreset(preset.key)"
            >
              {{ preset.label }}
            </el-button>
          </el-button-group>
        </div>
        <div class="filter-item">
          <el-button type="primary" @click="refreshData" :loading="loading">
            <el-icon><Refresh /></el-icon>
            刷新数据
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon leads">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ personalStats.totalLeads || 0 }}</div>
            <div class="stat-label">发布线索</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon exchanges">
            <el-icon><Switch /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ personalStats.totalExchanges || 0 }}</div>
            <div class="stat-label">参与交换</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon points">
            <el-icon><Coin /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ personalStats.totalPoints || 0 }}</div>
            <div class="stat-label">积分余额</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon rating">
            <el-icon><Star /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ personalStats.avgRating || 'N/A' }}</div>
            <div class="stat-label">平均评级</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 图表区域 -->
    <div class="charts-grid">
      <!-- 线索趋势图 -->
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>线索发布趋势</span>
            <el-select v-model="leadTrendGranularity" @change="loadLeadTrends" size="small">
              <el-option label="按小时" value="hour" />
              <el-option label="按天" value="day" />
              <el-option label="按周" value="week" />
              <el-option label="按月" value="month" />
            </el-select>
          </div>
        </template>
        <div ref="leadTrendChart" class="chart-container"></div>
      </el-card>
      
      <!-- 交换趋势图 -->
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>交换参与趋势</span>
            <el-select v-model="exchangeTrendGranularity" @change="loadExchangeTrends" size="small">
              <el-option label="按小时" value="hour" />
              <el-option label="按天" value="day" />
              <el-option label="按周" value="week" />
              <el-option label="按月" value="month" />
            </el-select>
          </div>
        </template>
        <div ref="exchangeTrendChart" class="chart-container"></div>
      </el-card>
    </div>

    <!-- 分布图表 -->
    <div class="distribution-grid">
      <!-- 线索评级分布 -->
      <el-card class="chart-card">
        <template #header>
          <span>线索评级分布</span>
        </template>
        <div ref="ratingDistributionChart" class="chart-container"></div>
      </el-card>
      
      <!-- 行业分布 -->
      <el-card class="chart-card">
        <template #header>
          <span>行业分布</span>
        </template>
        <div ref="industryDistributionChart" class="chart-container"></div>
      </el-card>
    </div>

    <!-- 详细数据表格 -->
    <el-card class="table-card">
      <template #header>
        <span>详细统计数据</span>
      </template>
      <el-table :data="detailTableData" stripe>
        <el-table-column prop="metric" label="指标" width="200" />
        <el-table-column prop="value" label="数值" width="150" />
        <el-table-column prop="description" label="说明" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Document, Switch, Coin, Star, Refresh } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { analyticsApi } from '@/api/analytics'
import type { PersonalStats, TrendData } from '@/types/analytics'

// 响应式数据
const loading = ref(false)
const dateRange = ref<[string, string]>()
const selectedPreset = ref('7d')
const personalStats = ref<PersonalStats>({})
const leadTrendGranularity = ref('day')
const exchangeTrendGranularity = ref('day')

// 图表引用
const leadTrendChart = ref<HTMLElement>()
const exchangeTrendChart = ref<HTMLElement>()
const ratingDistributionChart = ref<HTMLElement>()
const industryDistributionChart = ref<HTMLElement>()

// 图表实例
let leadTrendChartInstance: echarts.ECharts | null = null
let exchangeTrendChartInstance: echarts.ECharts | null = null
let ratingDistributionChartInstance: echarts.ECharts | null = null
let industryDistributionChartInstance: echarts.ECharts | null = null

// 时间预设选项
const datePresets = [
  { key: '1d', label: '今天' },
  { key: '7d', label: '最近7天' },
  { key: '30d', label: '最近30天' },
  { key: '90d', label: '最近90天' }
]

// 详细数据表格
const detailTableData = ref([
  { metric: '发布线索总数', value: '0', description: '累计发布的线索数量' },
  { metric: '交换成功率', value: '0%', description: '成功交换的线索占比' },
  { metric: '平均线索评级', value: 'N/A', description: '发布线索的平均评级' },
  { metric: '积分收益', value: '0', description: '通过交换获得的积分' },
  { metric: '积分支出', value: '0', description: '交换线索消耗的积分' },
  { metric: '浏览次数', value: '0', description: '线索被浏览的总次数' },
  { metric: '收藏次数', value: '0', description: '线索被收藏的总次数' }
])

// 初始化时间范围
const initDateRange = () => {
  const end = new Date()
  const start = new Date()
  start.setDate(start.getDate() - 7)
  
  dateRange.value = [
    start.toISOString().slice(0, 19).replace('T', ' '),
    end.toISOString().slice(0, 19).replace('T', ' ')
  ]
}

// 选择时间预设
const selectDatePreset = (preset: string) => {
  selectedPreset.value = preset
  const end = new Date()
  const start = new Date()
  
  switch (preset) {
    case '1d':
      start.setDate(start.getDate() - 1)
      break
    case '7d':
      start.setDate(start.getDate() - 7)
      break
    case '30d':
      start.setDate(start.getDate() - 30)
      break
    case '90d':
      start.setDate(start.getDate() - 90)
      break
  }
  
  dateRange.value = [
    start.toISOString().slice(0, 19).replace('T', ' '),
    end.toISOString().slice(0, 19).replace('T', ' ')
  ]
  
  refreshData()
}

// 时间范围变化处理
const handleDateRangeChange = () => {
  selectedPreset.value = ''
  refreshData()
}

// 刷新数据
const refreshData = async () => {
  if (!dateRange.value || !dateRange.value[0] || !dateRange.value[1]) {
    ElMessage.warning('请选择时间范围')
    return
  }
  
  loading.value = true
  try {
    await Promise.all([
      loadPersonalStats(),
      loadLeadTrends(),
      loadExchangeTrends(),
      loadRatingDistribution(),
      loadIndustryDistribution()
    ])
    ElMessage.success('数据刷新成功')
  } catch (error) {
    console.error('刷新数据失败:', error)
    ElMessage.error('数据刷新失败')
  } finally {
    loading.value = false
  }
}

// 加载个人统计数据
const loadPersonalStats = async () => {
  try {
    const response = await analyticsApi.getPersonalStats(
      dateRange.value![0],
      dateRange.value![1]
    )
    personalStats.value = response.data
    updateDetailTable()
  } catch (error) {
    console.error('加载个人统计数据失败:', error)
  }
}

// 更新详细数据表格
const updateDetailTable = () => {
  const stats = personalStats.value
  detailTableData.value = [
    { metric: '发布线索总数', value: String(stats.totalLeads || 0), description: '累计发布的线索数量' },
    { metric: '交换成功率', value: `${((stats.completedExchanges || 0) / Math.max(stats.totalExchanges || 1, 1) * 100).toFixed(1)}%`, description: '成功交换的线索占比' },
    { metric: '平均线索评级', value: stats.avgRating || 'N/A', description: '发布线索的平均评级' },
    { metric: '积分收益', value: String(stats.pointsEarned || 0), description: '通过交换获得的积分' },
    { metric: '积分支出', value: String(stats.pointsSpent || 0), description: '交换线索消耗的积分' },
    { metric: '浏览次数', value: String(stats.totalViews || 0), description: '线索被浏览的总次数' },
    { metric: '收藏次数', value: String(stats.totalFavorites || 0), description: '线索被收藏的总次数' }
  ]
}

// 加载线索趋势数据
const loadLeadTrends = async () => {
  try {
    const response = await analyticsApi.getLeadTrends(
      dateRange.value![0],
      dateRange.value![1],
      leadTrendGranularity.value
    )
    renderLeadTrendChart(response.data)
  } catch (error) {
    console.error('加载线索趋势数据失败:', error)
  }
}

// 加载交换趋势数据
const loadExchangeTrends = async () => {
  try {
    const response = await analyticsApi.getExchangeTrends(
      dateRange.value![0],
      dateRange.value![1],
      exchangeTrendGranularity.value
    )
    renderExchangeTrendChart(response.data)
  } catch (error) {
    console.error('加载交换趋势数据失败:', error)
  }
}

// 加载评级分布数据
const loadRatingDistribution = async () => {
  try {
    const response = await analyticsApi.getLeadRatingDistribution(
      dateRange.value![0],
      dateRange.value![1]
    )
    renderRatingDistributionChart(response.data)
  } catch (error) {
    console.error('加载评级分布数据失败:', error)
  }
}

// 加载行业分布数据
const loadIndustryDistribution = async () => {
  try {
    const response = await analyticsApi.getIndustryDistribution(
      dateRange.value![0],
      dateRange.value![1]
    )
    renderIndustryDistributionChart(response.data)
  } catch (error) {
    console.error('加载行业分布数据失败:', error)
  }
}

// 渲染线索趋势图表
const renderLeadTrendChart = (data: TrendData[]) => {
  if (!leadTrendChartInstance) return
  
  const option = {
    title: {
      text: '线索发布趋势',
      left: 'center',
      textStyle: { fontSize: 14, color: '#303133' }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.timePoint),
      axisLabel: { rotate: 45 }
    },
    yAxis: {
      type: 'value',
      name: '数量'
    },
    series: [{
      name: '线索数量',
      type: 'line',
      data: data.map(item => item.value),
      smooth: true,
      itemStyle: { color: '#409EFF' },
      areaStyle: { opacity: 0.3 }
    }]
  }
  
  leadTrendChartInstance.setOption(option)
}

// 渲染交换趋势图表
const renderExchangeTrendChart = (data: TrendData[]) => {
  if (!exchangeTrendChartInstance) return
  
  const option = {
    title: {
      text: '交换参与趋势',
      left: 'center',
      textStyle: { fontSize: 14, color: '#303133' }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.timePoint),
      axisLabel: { rotate: 45 }
    },
    yAxis: {
      type: 'value',
      name: '数量'
    },
    series: [{
      name: '交换数量',
      type: 'bar',
      data: data.map(item => item.value),
      itemStyle: { color: '#67C23A' }
    }]
  }
  
  exchangeTrendChartInstance.setOption(option)
}

// 渲染评级分布图表
const renderRatingDistributionChart = (data: Record<string, number>) => {
  if (!ratingDistributionChartInstance) return
  
  const chartData = Object.entries(data).map(([rating, count]) => ({
    name: `${rating}级`,
    value: count
  }))
  
  const option = {
    title: {
      text: '线索评级分布',
      left: 'center',
      textStyle: { fontSize: 14, color: '#303133' }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [{
      name: '评级分布',
      type: 'pie',
      radius: '50%',
      data: chartData,
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }
  
  ratingDistributionChartInstance.setOption(option)
}

// 渲染行业分布图表
const renderIndustryDistributionChart = (data: Record<string, number>) => {
  if (!industryDistributionChartInstance) return
  
  const industries = Object.keys(data)
  const counts = Object.values(data)
  
  const option = {
    title: {
      text: '行业分布',
      left: 'center',
      textStyle: { fontSize: 14, color: '#303133' }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    xAxis: {
      type: 'category',
      data: industries,
      axisLabel: { 
        rotate: 45,
        interval: 0
      }
    },
    yAxis: {
      type: 'value',
      name: '数量'
    },
    series: [{
      name: '线索数量',
      type: 'bar',
      data: counts,
      itemStyle: { color: '#E6A23C' }
    }]
  }
  
  industryDistributionChartInstance.setOption(option)
}

// 初始化图表
const initCharts = async () => {
  await nextTick()
  
  if (leadTrendChart.value) {
    leadTrendChartInstance = echarts.init(leadTrendChart.value)
  }
  if (exchangeTrendChart.value) {
    exchangeTrendChartInstance = echarts.init(exchangeTrendChart.value)
  }
  if (ratingDistributionChart.value) {
    ratingDistributionChartInstance = echarts.init(ratingDistributionChart.value)
  }
  if (industryDistributionChart.value) {
    industryDistributionChartInstance = echarts.init(industryDistributionChart.value)
  }
  
  // 监听窗口大小变化
  window.addEventListener('resize', () => {
    leadTrendChartInstance?.resize()
    exchangeTrendChartInstance?.resize()
    ratingDistributionChartInstance?.resize()
    industryDistributionChartInstance?.resize()
  })
}

// 组件挂载时初始化
onMounted(() => {
  initDateRange()
  initCharts()
  refreshData()
})
</script>

<style scoped>
.personal-analytics {
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

/* 筛选器样式 */
.filter-card {
  margin-bottom: 24px;
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 24px;
  flex-wrap: wrap;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-item label {
  font-weight: 500;
  color: #303133;
  white-space: nowrap;
}

/* 统计卡片样式 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 24px;
  margin-bottom: 24px;
}

.stat-card {
  border: 1px solid #EBEEF5;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
}

.stat-icon.leads {
  background: linear-gradient(135deg, #409EFF, #66B1FF);
  color: white;
}

.stat-icon.exchanges {
  background: linear-gradient(135deg, #67C23A, #85CE61);
  color: white;
}

.stat-icon.points {
  background: linear-gradient(135deg, #E6A23C, #EEBE77);
  color: white;
}

.stat-icon.rating {
  background: linear-gradient(135deg, #F56C6C, #F78989);
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  font-weight: 500;
}

/* 图表区域样式 */
.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
  margin-bottom: 24px;
}

.distribution-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
  margin-bottom: 24px;
}

.chart-card {
  border: 1px solid #EBEEF5;
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 500;
  color: #303133;
}

.chart-container {
  height: 300px;
  width: 100%;
}

/* 表格样式 */
.table-card {
  border: 1px solid #EBEEF5;
  border-radius: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .filter-row {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
  
  .filter-item {
    justify-content: space-between;
  }
  
  .stats-grid {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;
  }
  
  .charts-grid,
  .distribution-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .stat-content {
    padding: 16px;
  }
  
  .stat-value {
    font-size: 24px;
  }
}

@media (max-width: 480px) {
  .page-header h2 {
    font-size: 20px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .chart-container {
    height: 250px;
  }
}
</style>