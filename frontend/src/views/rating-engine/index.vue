<template>
  <div class="rating-engine-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <el-icon><TrendCharts /></el-icon>
          评级引擎
        </h1>
        <p class="page-description">智能线索评级系统，基于多维度算法自动评估线索价值</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="handleBatchRecalculate">
          <el-icon><Refresh /></el-icon>
          批量重算
        </el-button>
        <el-button @click="handleExportReport">
          <el-icon><Download /></el-icon>
          导出报告
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon stats-icon-primary">
                <el-icon><DataAnalysis /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-value">{{ statistics.totalCount || 0 }}</div>
                <div class="stats-label">总线索数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon stats-icon-success">
                <el-icon><Trophy /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-value">{{ statistics.averageScore?.toFixed(1) || '0.0' }}</div>
                <div class="stats-label">平均评分</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon stats-icon-warning">
                <el-icon><Star /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-value">{{ statistics.highQualityCount || 0 }}</div>
                <div class="stats-label">优质线索(A级)</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon stats-icon-info">
                <el-icon><Clock /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-value">{{ statistics.todayCount || 0 }}</div>
                <div class="stats-label">今日新增</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 评级分布图表 -->
    <div class="charts-section">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>评级分布</span>
                <el-button text @click="refreshDistribution">
                  <el-icon><Refresh /></el-icon>
                </el-button>
              </div>
            </template>
            <div class="chart-container">
              <RatingDistributionChart :data="distributionData" />
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>评级趋势</span>
                <el-select v-model="trendPeriod" @change="refreshTrend" style="width: 120px">
                  <el-option label="7天" value="7d" />
                  <el-option label="30天" value="30d" />
                  <el-option label="90天" value="90d" />
                </el-select>
              </div>
            </template>
            <div class="chart-container">
              <RatingTrendChart :data="trendData" :period="trendPeriod" />
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 线索评级列表 -->
    <div class="rating-list-section">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>线索评级列表</span>
            <div class="header-filters">
              <el-select v-model="filters.rating" placeholder="评级筛选" clearable style="width: 120px">
                <el-option label="A级" value="A" />
                <el-option label="B级" value="B" />
                <el-option label="C级" value="C" />
                <el-option label="D级" value="D" />
              </el-select>
              <el-input
                v-model="filters.keyword"
                placeholder="搜索线索"
                style="width: 200px; margin-left: 10px"
                @keyup.enter="handleSearch"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <el-button type="primary" @click="handleSearch" style="margin-left: 10px">
                搜索
              </el-button>
            </div>
          </div>
        </template>

        <!-- 评级列表表格 -->
        <el-table
          :data="ratingList"
          v-loading="loading"
          stripe
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="leadId" label="线索ID" width="100" />
          <el-table-column prop="title" label="线索标题" min-width="200" show-overflow-tooltip />
          <el-table-column prop="companyName" label="企业名称" width="150" show-overflow-tooltip />
          <el-table-column label="当前评级" width="120" align="center">
            <template #default="{ row }">
              <RatingBadge :rating="row.rating" :score="row.score" />
            </template>
          </el-table-column>
          <el-table-column prop="score" label="评分" width="80" align="center">
            <template #default="{ row }">
              <span class="score-text">{{ row.score?.toFixed(1) || '0.0' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="评级详情" width="120" align="center">
            <template #default="{ row }">
              <el-button text @click="viewRatingDetails(row)">
                <el-icon><View /></el-icon>
                查看详情
              </el-button>
            </template>
          </el-table-column>
          <el-table-column prop="calculateTime" label="评级时间" width="160">
            <template #default="{ row }">
              <span>{{ formatDateTime(row.calculateTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" align="center">
            <template #default="{ row }">
              <el-button text @click="recalculateRating(row.leadId)">
                <el-icon><Refresh /></el-icon>
                重新评级
              </el-button>
              <el-button text @click="adjustRating(row)">
                <el-icon><Edit /></el-icon>
                手动调整
              </el-button>
              <el-button text @click="viewHistory(row.leadId)">
                <el-icon><Clock /></el-icon>
                历史记录
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :total="pagination.total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>

    <!-- 评级详情对话框 -->
    <RatingDetailsDialog
      v-model="detailsDialogVisible"
      :lead-id="selectedLeadId"
      @refresh="loadRatingList"
    />

    <!-- 手动调整评级对话框 -->
    <RatingAdjustDialog
      v-model="adjustDialogVisible"
      :lead-data="selectedLead"
      @success="handleAdjustSuccess"
    />

    <!-- 评级历史对话框 -->
    <RatingHistoryDialog
      v-model="historyDialogVisible"
      :lead-id="selectedLeadId"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  TrendCharts,
  Refresh,
  Download,
  DataAnalysis,
  Trophy,
  Star,
  Clock,
  Search,
  View,
  Edit
} from '@element-plus/icons-vue'
import RatingBadge from './components/RatingBadge.vue'
import RatingDistributionChart from './components/RatingDistributionChart.vue'
import RatingTrendChart from './components/RatingTrendChart.vue'
import RatingDetailsDialog from './components/RatingDetailsDialog.vue'
import RatingAdjustDialog from './components/RatingAdjustDialog.vue'
import RatingHistoryDialog from './components/RatingHistoryDialog.vue'
import { ratingEngineApi } from '@/api/rating-engine'
import { formatDateTime } from '@/utils/date'

// 响应式数据
const loading = ref(false)
const statistics = ref({})
const distributionData = ref([])
const trendData = ref([])
const trendPeriod = ref('30d')
const ratingList = ref([])
const selectedRows = ref([])
const selectedLeadId = ref(null)
const selectedLead = ref(null)

// 对话框状态
const detailsDialogVisible = ref(false)
const adjustDialogVisible = ref(false)
const historyDialogVisible = ref(false)

// 筛选条件
const filters = reactive({
  rating: '',
  keyword: ''
})

// 分页数据
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 计算属性
const hasSelection = computed(() => selectedRows.value.length > 0)

// 生命周期
onMounted(() => {
  loadStatistics()
  loadDistributionData()
  loadTrendData()
  loadRatingList()
})

// 方法定义

/**
 * 加载统计数据
 */
const loadStatistics = async () => {
  try {
    const response = await ratingEngineApi.getStatistics()
    statistics.value = response.data
  } catch (error) {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败')
  }
}

/**
 * 加载评级分布数据
 */
const loadDistributionData = async () => {
  try {
    const response = await ratingEngineApi.getRatingDistribution()
    distributionData.value = response.data
  } catch (error) {
    console.error('加载分布数据失败:', error)
    ElMessage.error('加载分布数据失败')
  }
}

/**
 * 加载趋势数据
 */
const loadTrendData = async () => {
  try {
    const response = await ratingEngineApi.getRatingTrend(trendPeriod.value)
    trendData.value = response.data
  } catch (error) {
    console.error('加载趋势数据失败:', error)
    ElMessage.error('加载趋势数据失败')
  }
}

/**
 * 加载评级列表
 */
const loadRatingList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page - 1, // 后端从0开始
      size: pagination.size,
      rating: filters.rating,
      keyword: filters.keyword
    }
    const response = await ratingEngineApi.getRatingList(params)
    ratingList.value = response.data.content
    pagination.total = response.data.totalElements
  } catch (error) {
    console.error('加载评级列表失败:', error)
    ElMessage.error('加载评级列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 刷新分布数据
 */
const refreshDistribution = () => {
  loadDistributionData()
}

/**
 * 刷新趋势数据
 */
const refreshTrend = () => {
  loadTrendData()
}

/**
 * 搜索处理
 */
const handleSearch = () => {
  pagination.page = 1
  loadRatingList()
}

/**
 * 选择变化处理
 */
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

/**
 * 分页大小变化
 */
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadRatingList()
}

/**
 * 当前页变化
 */
const handleCurrentChange = (page) => {
  pagination.page = page
  loadRatingList()
}

/**
 * 查看评级详情
 */
const viewRatingDetails = (row) => {
  selectedLeadId.value = row.leadId
  detailsDialogVisible.value = true
}

/**
 * 重新计算评级
 */
const recalculateRating = async (leadId) => {
  try {
    await ElMessageBox.confirm('确认重新计算该线索的评级吗？', '确认操作', {
      type: 'warning'
    })
    
    loading.value = true
    await ratingEngineApi.recalculateRating(leadId)
    ElMessage.success('评级重新计算成功')
    loadRatingList()
    loadStatistics()
    loadDistributionData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('重新计算评级失败:', error)
      ElMessage.error('重新计算评级失败')
    }
  } finally {
    loading.value = false
  }
}

/**
 * 手动调整评级
 */
const adjustRating = (row) => {
  selectedLead.value = row
  adjustDialogVisible.value = true
}

/**
 * 调整成功处理
 */
const handleAdjustSuccess = () => {
  loadRatingList()
  loadStatistics()
  loadDistributionData()
}

/**
 * 查看历史记录
 */
const viewHistory = (leadId) => {
  selectedLeadId.value = leadId
  historyDialogVisible.value = true
}

/**
 * 批量重新计算
 */
const handleBatchRecalculate = async () => {
  if (!hasSelection.value) {
    ElMessage.warning('请先选择要重新计算的线索')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确认重新计算选中的 ${selectedRows.value.length} 个线索的评级吗？`,
      '批量操作确认',
      { type: 'warning' }
    )

    loading.value = true
    const leadIds = selectedRows.value.map(row => row.leadId)
    await ratingEngineApi.batchRecalculateRating(leadIds)
    ElMessage.success('批量重新计算成功')
    loadRatingList()
    loadStatistics()
    loadDistributionData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量重新计算失败:', error)
      ElMessage.error('批量重新计算失败')
    }
  } finally {
    loading.value = false
  }
}

/**
 * 导出报告
 */
const handleExportReport = async () => {
  try {
    loading.value = true
    const response = await ratingEngineApi.exportReport({
      rating: filters.rating,
      keyword: filters.keyword
    })
    
    // 创建下载链接
    const blob = new Blob([response.data], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `评级报告_${new Date().toISOString().slice(0, 10)}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('报告导出成功')
  } catch (error) {
    console.error('导出报告失败:', error)
    ElMessage.error('导出报告失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.rating-engine-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: white;
}

.header-content .page-title {
  display: flex;
  align-items: center;
  font-size: 28px;
  font-weight: 600;
  margin: 0 0 8px 0;
}

.header-content .page-title .el-icon {
  margin-right: 12px;
  font-size: 32px;
}

.page-description {
  margin: 0;
  opacity: 0.9;
  font-size: 16px;
}

.header-actions .el-button {
  margin-left: 12px;
}

.stats-cards {
  margin-bottom: 24px;
}

.stats-card {
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.stats-card:hover {
  transform: translateY(-4px);
}

.stats-content {
  display: flex;
  align-items: center;
  padding: 8px 0;
}

.stats-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
  color: white;
}

.stats-icon-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stats-icon-success {
  background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);
}

.stats-icon-warning {
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
}

.stats-icon-info {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
}

.stats-value {
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1;
}

.stats-label {
  font-size: 14px;
  color: #7f8c8d;
  margin-top: 4px;
}

.charts-section {
  margin-bottom: 24px;
}

.chart-card {
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: #2c3e50;
}

.chart-container {
  height: 300px;
}

.rating-list-section .el-card {
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.header-filters {
  display: flex;
  align-items: center;
}

.score-text {
  font-weight: 600;
  color: #409eff;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    text-align: center;
  }
  
  .header-actions {
    margin-top: 16px;
  }
  
  .header-filters {
    flex-direction: column;
    gap: 10px;
  }
}
</style>