<template>
  <el-dialog
    v-model="dialogVisible"
    title="评级历史记录"
    width="900px"
    :before-close="handleClose"
    destroy-on-close
  >
    <div v-loading="loading" class="rating-history">
      <!-- 筛选条件 -->
      <div class="filter-section">
        <el-card class="filter-card">
          <div class="filter-content">
            <el-form :model="filters" inline>
              <el-form-item label="变更原因">
                <el-select v-model="filters.reason" placeholder="全部" clearable style="width: 150px">
                  <el-option
                    v-for="reason in reasonOptions"
                    :key="reason.value"
                    :label="reason.label"
                    :value="reason.value"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="操作人">
                <el-input
                  v-model="filters.operator"
                  placeholder="操作人姓名"
                  clearable
                  style="width: 150px"
                />
              </el-form-item>
              <el-form-item label="时间范围">
                <el-date-picker
                  v-model="filters.dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  style="width: 240px"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSearch">
                  <el-icon><Search /></el-icon>
                  搜索
                </el-button>
                <el-button @click="handleReset">
                  <el-icon><RefreshLeft /></el-icon>
                  重置
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-card>
      </div>

      <!-- 历史记录时间线 -->
      <div class="timeline-section">
        <el-card class="timeline-card">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <el-icon><Clock /></el-icon>
                <span>评级变更历史</span>
                <el-tag v-if="historyList.length > 0" type="info" size="small">
                  共 {{ pagination.total }} 条记录
                </el-tag>
              </div>
              <div class="header-actions">
                <el-button size="small" @click="handleExport">
                  <el-icon><Download /></el-icon>
                  导出
                </el-button>
                <el-button size="small" @click="loadHistoryList">
                  <el-icon><Refresh /></el-icon>
                  刷新
                </el-button>
              </div>
            </div>
          </template>

          <div class="timeline-content">
            <el-timeline v-if="historyList.length > 0">
              <el-timeline-item
                v-for="(item, index) in historyList"
                :key="item.id"
                :timestamp="formatDateTime(item.changeTime)"
                :type="getTimelineType(item)"
                :icon="getTimelineIcon(item)"
                placement="top"
              >
                <div class="timeline-item-content">
                  <div class="change-header">
                    <div class="change-title">
                      <span class="change-type">{{ getChangeTypeText(item) }}</span>
                      <el-tag :type="getReasonTagType(item.reason)" size="small">
                        {{ getReasonText(item.reason) }}
                      </el-tag>
                    </div>
                    <div class="change-operator">
                      <el-icon><User /></el-icon>
                      <span>{{ item.operatorName || '系统' }}</span>
                    </div>
                  </div>

                  <div class="change-details">
                    <div class="rating-change">
                      <div class="change-item">
                        <label>评级变更:</label>
                        <div class="rating-comparison">
                          <RatingBadge 
                            :rating="item.oldRating" 
                            :score="item.oldScore" 
                            mode="compact"
                          />
                          <el-icon class="arrow-icon"><ArrowRight /></el-icon>
                          <RatingBadge 
                            :rating="item.newRating" 
                            :score="item.newScore" 
                            mode="compact"
                          />
                        </div>
                      </div>
                      <div class="change-item">
                        <label>分数变化:</label>
                        <span :class="getScoreChangeClass(item)">
                          {{ getScoreChangeText(item) }}
                        </span>
                      </div>
                    </div>

                    <div v-if="item.description" class="change-description">
                      <label>变更说明:</label>
                      <p>{{ item.description }}</p>
                    </div>

                    <div class="change-metadata">
                      <div class="metadata-item">
                        <el-icon><Monitor /></el-icon>
                        <span>{{ item.ipAddress || '未知' }}</span>
                      </div>
                      <div class="metadata-item">
                        <el-icon><Document /></el-icon>
                        <span>版本: {{ item.version || '1.0' }}</span>
                      </div>
                      <div v-if="item.canRollback" class="metadata-item">
                        <el-button 
                          text 
                          type="primary" 
                          size="small"
                          @click="handleRollback(item)"
                        >
                          <el-icon><RefreshLeft /></el-icon>
                          回滚到此版本
                        </el-button>
                      </div>
                    </div>
                  </div>
                </div>
              </el-timeline-item>
            </el-timeline>

            <!-- 空状态 -->
            <div v-else class="empty-state">
              <el-empty description="暂无评级历史记录">
                <template #image>
                  <el-icon size="64" color="#c0c4cc"><DocumentRemove /></el-icon>
                </template>
              </el-empty>
            </div>
          </div>

          <!-- 分页 -->
          <div v-if="historyList.length > 0" class="pagination-container">
            <el-pagination
              v-model:current-page="pagination.page"
              v-model:page-size="pagination.size"
              :total="pagination.total"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </el-card>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  RefreshLeft,
  Clock,
  Download,
  Refresh,
  User,
  ArrowRight,
  Monitor,
  Document,
  DocumentRemove,
  TrendCharts,
  Edit,
  Setting
} from '@element-plus/icons-vue'
import RatingBadge from './RatingBadge.vue'
import { ratingHistoryApi } from '@/api/rating-history'
import { formatDateTime } from '@/utils/date'

// 组件属性
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  leadId: {
    type: [String, Number],
    default: null
  }
})

// 事件定义
const emit = defineEmits(['update:modelValue'])

// 响应式数据
const loading = ref(false)
const historyList = ref([])

// 筛选条件
const filters = reactive({
  reason: '',
  operator: '',
  dateRange: null
})

// 分页数据
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 变更原因选项
const reasonOptions = [
  { label: '自动评级', value: 'auto_rating' },
  { label: '手动调整', value: 'manual_adjust' },
  { label: '信息补充', value: 'info_complete' },
  { label: '资质变更', value: 'qualification_change' },
  { label: '市场重估', value: 'market_revaluation' },
  { label: '客户反馈', value: 'customer_feedback' },
  { label: '策略调整', value: 'strategy_adjustment' },
  { label: '数据修正', value: 'data_correction' },
  { label: '其他', value: 'other' }
]

// 计算属性
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 监听leadId变化
watch(
  () => props.leadId,
  (newLeadId) => {
    if (newLeadId && props.modelValue) {
      loadHistoryList()
    }
  },
  { immediate: true }
)

// 监听对话框显示状态
watch(
  () => props.modelValue,
  (visible) => {
    if (visible && props.leadId) {
      resetFilters()
      loadHistoryList()
    }
  }
)

// 方法定义

/**
 * 加载历史记录列表
 */
const loadHistoryList = async () => {
  if (!props.leadId) return

  loading.value = true
  try {
    const params = {
      leadId: props.leadId,
      page: pagination.page - 1, // 后端从0开始
      size: pagination.size,
      reason: filters.reason,
      operator: filters.operator
    }

    // 处理时间范围
    if (filters.dateRange && filters.dateRange.length === 2) {
      params.startTime = filters.dateRange[0].toISOString()
      params.endTime = filters.dateRange[1].toISOString()
    }

    const response = await ratingHistoryApi.getHistoryList(params)
    historyList.value = response.data.content
    pagination.total = response.data.totalElements
  } catch (error) {
    console.error('加载历史记录失败:', error)
    ElMessage.error('加载历史记录失败')
  } finally {
    loading.value = false
  }
}

/**
 * 搜索处理
 */
const handleSearch = () => {
  pagination.page = 1
  loadHistoryList()
}

/**
 * 重置筛选条件
 */
const handleReset = () => {
  resetFilters()
  handleSearch()
}

/**
 * 重置筛选条件
 */
const resetFilters = () => {
  filters.reason = ''
  filters.operator = ''
  filters.dateRange = null
}

/**
 * 分页大小变化
 */
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadHistoryList()
}

/**
 * 当前页变化
 */
const handleCurrentChange = (page) => {
  pagination.page = page
  loadHistoryList()
}

/**
 * 获取时间线类型
 */
const getTimelineType = (item) => {
  if (item.reason === 'auto_rating') return 'primary'
  if (item.reason === 'manual_adjust') return 'warning'
  if (item.reason === 'data_correction') return 'danger'
  return 'info'
}

/**
 * 获取时间线图标
 */
const getTimelineIcon = (item) => {
  if (item.reason === 'auto_rating') return TrendCharts
  if (item.reason === 'manual_adjust') return Edit
  if (item.reason === 'data_correction') return Setting
  return Document
}

/**
 * 获取变更类型文本
 */
const getChangeTypeText = (item) => {
  const oldRating = item.oldRating
  const newRating = item.newRating
  
  if (oldRating === newRating) {
    return '评分调整'
  }
  
  const ratingValues = { A: 4, B: 3, C: 2, D: 1 }
  const oldValue = ratingValues[oldRating] || 0
  const newValue = ratingValues[newRating] || 0
  
  if (newValue > oldValue) {
    return '评级提升'
  } else {
    return '评级降级'
  }
}

/**
 * 获取原因标签类型
 */
const getReasonTagType = (reason) => {
  const typeMap = {
    auto_rating: 'primary',
    manual_adjust: 'warning',
    info_complete: 'success',
    qualification_change: 'info',
    market_revaluation: '',
    customer_feedback: 'success',
    strategy_adjustment: 'warning',
    data_correction: 'danger',
    other: ''
  }
  return typeMap[reason] || ''
}

/**
 * 获取原因文本
 */
const getReasonText = (reason) => {
  const reasonMap = {
    auto_rating: '自动评级',
    manual_adjust: '手动调整',
    info_complete: '信息补充',
    qualification_change: '资质变更',
    market_revaluation: '市场重估',
    customer_feedback: '客户反馈',
    strategy_adjustment: '策略调整',
    data_correction: '数据修正',
    other: '其他'
  }
  return reasonMap[reason] || reason
}

/**
 * 获取分数变化样式类
 */
const getScoreChangeClass = (item) => {
  const diff = item.newScore - item.oldScore
  if (Math.abs(diff) < 0.1) return 'no-change'
  return diff > 0 ? 'score-increase' : 'score-decrease'
}

/**
 * 获取分数变化文本
 */
const getScoreChangeText = (item) => {
  const diff = item.newScore - item.oldScore
  if (Math.abs(diff) < 0.1) return '无变化'
  
  const sign = diff > 0 ? '+' : ''
  return `${sign}${diff.toFixed(1)}分`
}

/**
 * 处理回滚
 */
const handleRollback = async (item) => {
  try {
    await ElMessageBox.confirm(
      `确认要回滚到 ${formatDateTime(item.changeTime)} 的评级状态吗？\n评级: ${item.oldRating}级\n评分: ${item.oldScore?.toFixed(1)}分`,
      '确认回滚',
      {
        type: 'warning',
        dangerouslyUseHTMLString: true
      }
    )

    loading.value = true
    await ratingHistoryApi.rollbackRating({
      leadId: props.leadId,
      targetHistoryId: item.id
    })
    
    ElMessage.success('回滚成功')
    loadHistoryList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('回滚失败:', error)
      ElMessage.error('回滚失败')
    }
  } finally {
    loading.value = false
  }
}

/**
 * 导出历史记录
 */
const handleExport = async () => {
  try {
    const params = {
      leadId: props.leadId,
      reason: filters.reason,
      operator: filters.operator
    }

    if (filters.dateRange && filters.dateRange.length === 2) {
      params.startTime = filters.dateRange[0].toISOString()
      params.endTime = filters.dateRange[1].toISOString()
    }

    const response = await ratingHistoryApi.exportHistory(params)
    
    // 创建下载链接
    const blob = new Blob([response.data], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `评级历史_${props.leadId}_${new Date().toISOString().slice(0, 10)}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

/**
 * 关闭对话框
 */
const handleClose = () => {
  dialogVisible.value = false
}
</script>

<style scoped>
.rating-history {
  max-height: 70vh;
  overflow-y: auto;
}

.filter-section {
  margin-bottom: 20px;
}

.filter-card {
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filter-content {
  padding: 10px 0;
}

.timeline-card {
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: #2c3e50;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.timeline-content {
  padding: 20px 0;
}

.timeline-item-content {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  border-left: 4px solid #409eff;
}

.change-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.change-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.change-type {
  font-weight: 600;
  color: #2c3e50;
  font-size: 16px;
}

.change-operator {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #666;
  font-size: 14px;
}

.change-details {
  margin-bottom: 10px;
}

.rating-change {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
}

.change-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.change-item label {
  font-weight: 600;
  color: #666;
  min-width: 80px;
}

.rating-comparison {
  display: flex;
  align-items: center;
  gap: 8px;
}

.arrow-icon {
  color: #409eff;
  font-size: 16px;
}

.no-change {
  color: #909399;
}

.score-increase {
  color: #52c41a;
  font-weight: 600;
}

.score-decrease {
  color: #ff4d4f;
  font-weight: 600;
}

.change-description {
  margin-bottom: 15px;
}

.change-description label {
  font-weight: 600;
  color: #666;
  display: block;
  margin-bottom: 5px;
}

.change-description p {
  margin: 0;
  color: #2c3e50;
  line-height: 1.5;
  background: white;
  padding: 8px 12px;
  border-radius: 4px;
  border: 1px solid #e8e8e8;
}

.change-metadata {
  display: flex;
  align-items: center;
  gap: 15px;
  font-size: 12px;
  color: #999;
}

.metadata-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.empty-state {
  padding: 40px 0;
  text-align: center;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e8e8e8;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .change-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .rating-change {
    flex-direction: column;
    gap: 10px;
  }
  
  .change-metadata {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>