<template>
  <div class="system-analytics">
    <div class="page-header">
      <h2>系统看板</h2>
      <p class="page-description">查看系统整体运行数据和分析</p>
    </div>
    
    <!-- 筛选器 -->
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
          <label>快速选择：</label>
          <el-select v-model="quickSelect" placeholder="选择时间范围" @change="handleQuickSelect">
            <el-option label="今天" value="today" />
            <el-option label="昨天" value="yesterday" />
            <el-option label="最近7天" value="week" />
            <el-option label="最近30天" value="month" />
            <el-option label="最近90天" value="quarter" />
          </el-select>
        </div>
        <div class="filter-item">
          <el-button type="primary" @click="loadData" :loading="loading">
            <el-icon><Refresh /></el-icon>
            刷新数据
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 系统概览统计卡片 -->
    <div class="stats-grid">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon users">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ systemStats?.totalUsers || 0 }}</div>
            <div class="stat-label">总用户数</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon active-users">
            <el-icon><UserFilled /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ systemStats?.activeUsers || 0 }}</div>
            <div class="stat-label">活跃用户数</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon leads">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ systemStats?.totalLeads || 0 }}</div>
            <div class="stat-label">线索总数</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon exchanges">
            <el-icon><Switch /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ systemStats?.totalExchanges || 0 }}</div>
            <div class="stat-label">交换总次数</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon points">
            <el-icon><Coin /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ systemStats?.totalPointsCirculation || 0 }}</div>
            <div class="stat-label">积分流通量</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon views">
            <el-icon><View /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ systemStats?.totalViews || 0 }}</div>
            <div class="stat-label">总浏览次数</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 趋势图表 -->
    <div class="charts-grid">
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>用户活跃度趋势</span>
          </div>
        </template>
        <div ref="activityTrendChart" class="chart-container"></div>
      </el-card>
      
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>线索发布趋势</span>
          </div>
        </template>
        <div ref="leadTrendChart" class="chart-container"></div>
      </el-card>
    </div>

    <!-- 分布图表 -->
    <div class="distribution-grid">
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>线索评级分布</span>
          </div>
        </template>
        <div ref="ratingDistributionChart" class="chart-container"></div>
      </el-card>
      
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>热门行业分布</span>
          </div>
        </template>
        <div ref="industryDistributionChart" class="chart-container"></div>
      </el-card>
    </div>

    <!-- 详细数据表格 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>最活跃用户</span>
          <el-button type="text" @click="exportReport">
            <el-icon><Download /></el-icon>
            导出报表
          </el-button>
        </div>
      </template>
      <el-table :data="systemStats?.topActiveUsers || []" style="width: 100%">
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="activityScore" label="活跃度评分" width="120">
          <template #default="{ row }">
            <el-tag :type="getActivityScoreType(row.activityScore)">
              {{ row.activityScore }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import {
  User, UserFilled, Document, Switch, Coin, View,
  Refresh, Download
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { analyticsApi } from '@/api/analytics'
import type { SystemStats, TrendData } from '@/types/analytics'

// 响应式数据
const loading = ref(false)
const dateRange = ref<[string, string]>()
const quickSelect = ref('')
const systemStats = ref<SystemStats>()

// 图表实例
const activityTrendChart = ref<HTMLElement>()
const leadTrendChart = ref<HTMLElement>()
const ratingDistributionChart = ref<HTMLElement>()
const industryDistributionChart = ref<HTMLElement>()

let activityChart: echarts.ECharts | null = null
let leadChart: echarts.ECharts | null = null
let ratingChart: echarts.ECharts | null = null
let industryChart: echarts.ECharts | null = null

// 初始化默认时间范围（最近30天）
const initDefaultTimeRange = () => {
  const endTime = new Date()
  const startTime = new Date()
  startTime.setDate(endTime.getDate() - 30)
  
  dateRange.value = [
    startTime.toISOString().slice(0, 19).replace('T', ' '),
    endTime.toISOString().slice(0, 19).replace('T', ' ')
  ]
  quickSelect.value = 'month'
}

// 处理时间范围变化
const handleDateRangeChange = (value: [string, string] | null) => {
  if (value) {
    quickSelect.value = ''
    loadData()
  }
}

// 处理快速选择
const handleQuickSelect = (value: string) => {
  const endTime = new Date()
  const startTime = new Date()
  
  switch (value) {
    case 'today':
      startTime.setHours(0, 0, 0, 0)
      endTime.setHours(23, 59, 59, 999)
      break
    case 'yesterday':
      startTime.setDate(endTime.getDate() - 1)
      startTime.setHours(0, 0, 0, 0)
      endTime.setDate(endTime.getDate() - 1)
      endTime.setHours(23, 59, 59, 999)
      break
    case 'week':
      startTime.setDate(endTime.getDate() - 7)
      break
    case 'month':
      startTime.setDate(endTime.getDate() - 30)
      break
    case 'quarter':
      startTime.setDate(endTime.getDate() - 90)
      break
  }
  
  dateRange.value = [
    startTime.toISOString().slice(0, 19).replace('T', ' '),
    endTime.toISOString().slice(0, 19).replace('T', ' ')
  ]
  
  loadData()
}

// 加载系统统计数据
const loadSystemStats = async () => {
  if (!dateRange.value) return
  
  try {
    const response = await analyticsApi.getSystemStats(
      dateRange.value[0],
      dateRange.value[1]
    )
    systemStats.value = response.data
  } catch (error) {
    console.error('加载系统统计数据失败:', error)
    ElMessage.error('加载系统统计数据失败')
  }
}

// 加载用户活跃度趋势数据
const loadActivityTrends = async () => {
  if (!dateRange.value) return
  
  try {
    const response = await analyticsApi.getUserActivityTrends(
      dateRange.value[0],
      dateRange.value[1],
      'day'
    )
    renderActivityTrendChart(response.data)
  } catch (error) {
    console.error('加载用户活跃度趋势失败:', error)
  }
}

// 加载线索趋势数据
const loadLeadTrends = async () => {
  if (!dateRange.value) return
  
  try {
    const response = await analyticsApi.getLeadTrends(
      dateRange.value[0],
      dateRange.value[1],
      'day'
    )
    renderLeadTrendChart(response.data)
  } catch (error) {
    console.error('加载线索趋势数据失败:', error)
  }
}

// 加载评级分布数据
const loadRatingDistribution = async () => {
  if (!dateRange.value) return
  
  try {
    const response = await analyticsApi.getLeadRatingDistribution(
      dateRange.value[0],
      dateRange.value[1]
    )
    renderRatingDistributionChart(response.data)
  } catch (error) {
    console.error('加载评级分布数据失败:', error)
  }
}

// 加载行业分布数据
const loadIndustryDistribution = async () => {
  if (!dateRange.value) return
  
  try {
    const response = await analyticsApi.getIndustryDistribution(
      dateRange.value[0],
      dateRange.value[1]
    )
    renderIndustryDistributionChart(response.data)
  } catch (error) {
    console.error('加载行业分布数据失败:', error)
  }
}

// 渲染用户活跃度趋势图表
const renderActivityTrendChart = (data: TrendData[]) => {
  if (!activityChart || !data.length) return
  
  const option = {
    title: {
      text: '用户活跃度趋势',
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'normal'
      }
    },
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>{a}: {c}人'
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.time.split(' ')[0])
    },
    yAxis: {
      type: 'value',
      name: '活跃用户数'
    },
    series: [{
      name: '活跃用户',
      type: 'line',
      data: data.map(item => item.value),
      smooth: true,
      itemStyle: {
        color: '#409EFF'
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [{
            offset: 0, color: 'rgba(64, 158, 255, 0.3)'
          }, {
            offset: 1, color: 'rgba(64, 158, 255, 0.1)'
          }]
        }
      }
    }]
  }
  
  activityChart.setOption(option)
}

// 渲染线索趋势图表
const renderLeadTrendChart = (data: TrendData[]) => {
  if (!leadChart || !data.length) return
  
  const option = {
    title: {
      text: '线索发布趋势',
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'normal'
      }
    },
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>{a}: {c}条'
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.time.split(' ')[0])
    },
    yAxis: {
      type: 'value',
      name: '线索数量'
    },
    series: [{
      name: '新增线索',
      type: 'bar',
      data: data.map(item => item.value),
      itemStyle: {
        color: '#67C23A'
      }
    }]
  }
  
  leadChart.setOption(option)
}

// 渲染评级分布图表
const renderRatingDistributionChart = (data: Record<string, number>) => {
  if (!ratingChart || !Object.keys(data).length) return
  
  const chartData = Object.entries(data).map(([rating, count]) => ({
    name: `${rating}级`,
    value: count
  }))
  
  const option = {
    title: {
      text: '线索评级分布',
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'normal'
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a}<br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [{
      name: '线索评级',
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
  
  ratingChart.setOption(option)
}

// 渲染行业分布图表
const renderIndustryDistributionChart = (data: Record<string, number>) => {
  if (!industryChart || !Object.keys(data).length) return
  
  const sortedData = Object.entries(data)
    .sort(([, a], [, b]) => b - a)
    .slice(0, 10) // 只显示前10个行业
  
  const option = {
    title: {
      text: '热门行业分布',
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'normal'
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: '{b}<br/>{a}: {c}条'
    },
    xAxis: {
      type: 'value',
      name: '线索数量'
    },
    yAxis: {
      type: 'category',
      data: sortedData.map(([industry]) => industry)
    },
    series: [{
      name: '线索数量',
      type: 'bar',
      data: sortedData.map(([, count]) => count),
      itemStyle: {
        color: '#E6A23C'
      }
    }]
  }
  
  industryChart.setOption(option)
}

// 获取活跃度评分类型
const getActivityScoreType = (score: number) => {
  if (score >= 80) return 'success'
  if (score >= 60) return 'warning'
  return 'danger'
}

// 导出报表
const exportReport = async () => {
  if (!dateRange.value) {
    ElMessage.warning('请先选择时间范围')
    return
  }
  
  try {
    const response = await analyticsApi.exportReport(
      dateRange.value[0],
      dateRange.value[1],
      'system'
    )
    
    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `系统数据报表_${new Date().toISOString().slice(0, 10)}.xlsx`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('报表导出成功')
  } catch (error) {
    console.error('导出报表失败:', error)
    ElMessage.error('导出报表失败')
  }
}

// 加载所有数据
const loadData = async () => {
  loading.value = true
  try {
    await Promise.all([
      loadSystemStats(),
      loadActivityTrends(),
      loadLeadTrends(),
      loadRatingDistribution(),
      loadIndustryDistribution()
    ])
  } finally {
    loading.value = false
  }
}

// 初始化图表
const initCharts = async () => {
  await nextTick()
  
  if (activityTrendChart.value) {
    activityChart = echarts.init(activityTrendChart.value)
  }
  if (leadTrendChart.value) {
    leadChart = echarts.init(leadTrendChart.value)
  }
  if (ratingDistributionChart.value) {
    ratingChart = echarts.init(ratingDistributionChart.value)
  }
  if (industryDistributionChart.value) {
    industryChart = echarts.init(industryDistributionChart.value)
  }
  
  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)
}

// 处理窗口大小变化
const handleResize = () => {
  activityChart?.resize()
  leadChart?.resize()
  ratingChart?.resize()
  industryChart?.resize()
}

// 组件挂载
onMounted(async () => {
  initDefaultTimeRange()
  await initCharts()
  await loadData()
})

// 组件卸载
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  activityChart?.dispose()
  leadChart?.dispose()
  ratingChart?.dispose()
  industryChart?.dispose()
})
</script>

<style scoped>
.system-analytics {
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

.stat-icon.users {
  background: linear-gradient(135deg, #409EFF, #66B1FF);
  color: white;
}

.stat-icon.active-users {
  background: linear-gradient(135deg, #67C23A, #85CE61);
  color: white;
}

.stat-icon.leads {
  background: linear-gradient(135deg, #E6A23C, #EEBE77);
  color: white;
}

.stat-icon.exchanges {
  background: linear-gradient(135deg, #F56C6C, #F78989);
  color: white;
}

.stat-icon.points {
  background: linear-gradient(135deg, #909399, #B3B6BB);
  color: white;
}

.stat-icon.views {
  background: linear-gradient(135deg, #9C27B0, #BA68C8);
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