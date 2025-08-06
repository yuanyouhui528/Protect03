<template>
  <el-dialog
    v-model="dialogVisible"
    title="评级详情"
    width="800px"
    :before-close="handleClose"
    destroy-on-close
  >
    <div v-loading="loading" class="rating-details">
      <!-- 基本信息 -->
      <div class="basic-info">
        <el-card class="info-card">
          <template #header>
            <div class="card-header">
              <el-icon><InfoFilled /></el-icon>
              <span>基本信息</span>
            </div>
          </template>
          <div class="info-content">
            <div class="info-row">
              <div class="info-item">
                <label>线索ID:</label>
                <span>{{ ratingDetails.leadId }}</span>
              </div>
              <div class="info-item">
                <label>线索标题:</label>
                <span>{{ ratingDetails.title }}</span>
              </div>
            </div>
            <div class="info-row">
              <div class="info-item">
                <label>企业名称:</label>
                <span>{{ ratingDetails.companyName }}</span>
              </div>
              <div class="info-item">
                <label>评级时间:</label>
                <span>{{ formatDateTime(ratingDetails.calculateTime) }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 评级结果 -->
      <div class="rating-result">
        <el-card class="result-card">
          <template #header>
            <div class="card-header">
              <el-icon><Trophy /></el-icon>
              <span>评级结果</span>
            </div>
          </template>
          <div class="result-content">
            <div class="rating-display">
              <div class="rating-badge-large">
                <RatingBadge 
                  :rating="ratingDetails.rating" 
                  :score="ratingDetails.score" 
                  mode="normal"
                />
              </div>
              <div class="rating-info">
                <div class="rating-text">
                  <span class="rating-level">{{ ratingDetails.rating }}级线索</span>
                  <span class="rating-desc">{{ getRatingDescription(ratingDetails.rating) }}</span>
                </div>
                <div class="score-info">
                  <span class="score-label">综合评分:</span>
                  <span class="score-value">{{ ratingDetails.score?.toFixed(1) || '0.0' }}</span>
                  <span class="score-unit">/100</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 维度评分 -->
      <div class="dimension-scores">
        <el-card class="scores-card">
          <template #header>
            <div class="card-header">
              <el-icon><DataAnalysis /></el-icon>
              <span>维度评分</span>
            </div>
          </template>
          <div class="scores-content">
            <div class="scores-grid">
              <div 
                v-for="dimension in dimensionScores" 
                :key="dimension.name"
                class="dimension-item"
              >
                <div class="dimension-header">
                  <span class="dimension-name">{{ dimension.name }}</span>
                  <span class="dimension-weight">(权重: {{ dimension.weight }}%)</span>
                </div>
                <div class="dimension-score">
                  <el-progress
                    :percentage="dimension.score"
                    :color="getProgressColor(dimension.score)"
                    :stroke-width="8"
                    :show-text="false"
                  />
                  <span class="score-text">{{ dimension.score?.toFixed(1) || '0.0' }}</span>
                </div>
                <div class="dimension-desc">{{ dimension.description }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 评级规则 -->
      <div class="rating-rules">
        <el-card class="rules-card">
          <template #header>
            <div class="card-header">
              <el-icon><Document /></el-icon>
              <span>评级规则</span>
            </div>
          </template>
          <div class="rules-content">
            <el-table :data="ratingRules" stripe>
              <el-table-column prop="name" label="规则名称" width="200" />
              <el-table-column prop="type" label="规则类型" width="120">
                <template #default="{ row }">
                  <el-tag :type="getRuleTypeTag(row.type)">{{ row.type }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="weight" label="权重" width="80" align="center">
                <template #default="{ row }">
                  <span>{{ row.weight }}%</span>
                </template>
              </el-table-column>
              <el-table-column prop="score" label="得分" width="80" align="center">
                <template #default="{ row }">
                  <span class="score-text">{{ row.score?.toFixed(1) || '0.0' }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="description" label="规则说明" show-overflow-tooltip />
            </el-table>
          </div>
        </el-card>
      </div>

      <!-- 评级建议 -->
      <div class="rating-suggestions">
        <el-card class="suggestions-card">
          <template #header>
            <div class="card-header">
              <el-icon><Lightbulb /></el-icon>
              <span>优化建议</span>
            </div>
          </template>
          <div class="suggestions-content">
            <div v-if="suggestions.length > 0" class="suggestions-list">
              <div 
                v-for="(suggestion, index) in suggestions" 
                :key="index"
                class="suggestion-item"
              >
                <div class="suggestion-icon">
                  <el-icon><ArrowRight /></el-icon>
                </div>
                <div class="suggestion-content">
                  <div class="suggestion-title">{{ suggestion.title }}</div>
                  <div class="suggestion-desc">{{ suggestion.description }}</div>
                </div>
              </div>
            </div>
            <div v-else class="no-suggestions">
              <el-icon><Check /></el-icon>
              <span>该线索评级已达到最优状态，无需优化</span>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button type="primary" @click="handleRecalculate">
          <el-icon><Refresh /></el-icon>
          重新评级
        </el-button>
        <el-button @click="handleExport">
          <el-icon><Download /></el-icon>
          导出详情
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import {
  InfoFilled,
  Trophy,
  DataAnalysis,
  Document,
  Lightbulb,
  ArrowRight,
  Check,
  Refresh,
  Download
} from '@element-plus/icons-vue'
import RatingBadge from './RatingBadge.vue'
import { ratingEngineApi } from '@/api/rating-engine'
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
const emit = defineEmits(['update:modelValue', 'refresh'])

// 响应式数据
const loading = ref(false)
const ratingDetails = ref({})
const dimensionScores = ref([])
const ratingRules = ref([])
const suggestions = ref([])

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
      loadRatingDetails()
    }
  },
  { immediate: true }
)

// 监听对话框显示状态
watch(
  () => props.modelValue,
  (visible) => {
    if (visible && props.leadId) {
      loadRatingDetails()
    }
  }
)

// 方法定义

/**
 * 加载评级详情
 */
const loadRatingDetails = async () => {
  if (!props.leadId) return

  loading.value = true
  try {
    const response = await ratingEngineApi.getRatingDetails(props.leadId)
    const data = response.data
    
    ratingDetails.value = data.basic || {}
    dimensionScores.value = data.dimensions || []
    ratingRules.value = data.rules || []
    suggestions.value = data.suggestions || []
  } catch (error) {
    console.error('加载评级详情失败:', error)
    ElMessage.error('加载评级详情失败')
  } finally {
    loading.value = false
  }
}

/**
 * 获取评级描述
 */
const getRatingDescription = (rating) => {
  const descriptions = {
    A: '优质线索，具有很高的转化潜力',
    B: '良好线索，具有较高的转化潜力',
    C: '一般线索，具有一定的转化潜力',
    D: '较差线索，转化潜力较低'
  }
  return descriptions[rating] || '未知评级'
}

/**
 * 获取进度条颜色
 */
const getProgressColor = (score) => {
  if (score >= 80) return '#52c41a'
  if (score >= 60) return '#1890ff'
  if (score >= 40) return '#faad14'
  return '#ff4d4f'
}

/**
 * 获取规则类型标签
 */
const getRuleTypeTag = (type) => {
  const typeMap = {
    '信息完整度': 'primary',
    '企业资质': 'success',
    '企业规模': 'warning',
    '产业价值': 'danger',
    '地域因素': 'info',
    '时效性': '',
    '历史表现': 'primary'
  }
  return typeMap[type] || ''
}

/**
 * 关闭对话框
 */
const handleClose = () => {
  dialogVisible.value = false
}

/**
 * 重新评级
 */
const handleRecalculate = async () => {
  try {
    loading.value = true
    await ratingEngineApi.recalculateRating(props.leadId)
    ElMessage.success('重新评级成功')
    loadRatingDetails()
    emit('refresh')
  } catch (error) {
    console.error('重新评级失败:', error)
    ElMessage.error('重新评级失败')
  } finally {
    loading.value = false
  }
}

/**
 * 导出详情
 */
const handleExport = async () => {
  try {
    const response = await ratingEngineApi.exportRatingDetails(props.leadId)
    
    // 创建下载链接
    const blob = new Blob([response.data], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `评级详情_${props.leadId}_${new Date().toISOString().slice(0, 10)}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}
</script>

<style scoped>
.rating-details {
  max-height: 70vh;
  overflow-y: auto;
}

.info-card,
.result-card,
.scores-card,
.rules-card,
.suggestions-card {
  margin-bottom: 20px;
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  font-weight: 600;
  color: #2c3e50;
}

.card-header .el-icon {
  margin-right: 8px;
  font-size: 16px;
}

.info-content {
  padding: 10px 0;
}

.info-row {
  display: flex;
  margin-bottom: 15px;
}

.info-item {
  flex: 1;
  display: flex;
  align-items: center;
}

.info-item label {
  font-weight: 600;
  color: #666;
  margin-right: 8px;
  min-width: 80px;
}

.rating-display {
  display: flex;
  align-items: center;
  padding: 20px 0;
}

.rating-badge-large {
  margin-right: 30px;
}

.rating-info {
  flex: 1;
}

.rating-text {
  margin-bottom: 10px;
}

.rating-level {
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
  margin-right: 15px;
}

.rating-desc {
  font-size: 14px;
  color: #666;
}

.score-info {
  display: flex;
  align-items: center;
}

.score-label {
  font-size: 16px;
  color: #666;
  margin-right: 8px;
}

.score-value {
  font-size: 28px;
  font-weight: 700;
  color: #409eff;
  margin-right: 5px;
}

.score-unit {
  font-size: 16px;
  color: #999;
}

.scores-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  padding: 10px 0;
}

.dimension-item {
  padding: 15px;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  background: #fafafa;
}

.dimension-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.dimension-name {
  font-weight: 600;
  color: #2c3e50;
}

.dimension-weight {
  font-size: 12px;
  color: #999;
}

.dimension-score {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.dimension-score .el-progress {
  flex: 1;
  margin-right: 10px;
}

.dimension-score .score-text {
  font-weight: 600;
  color: #409eff;
  min-width: 40px;
}

.dimension-desc {
  font-size: 12px;
  color: #666;
  line-height: 1.4;
}

.suggestions-list {
  padding: 10px 0;
}

.suggestion-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 15px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.suggestion-icon {
  margin-right: 10px;
  margin-top: 2px;
  color: #409eff;
}

.suggestion-title {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 5px;
}

.suggestion-desc {
  font-size: 14px;
  color: #666;
  line-height: 1.4;
}

.no-suggestions {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30px;
  color: #52c41a;
  font-size: 16px;
}

.no-suggestions .el-icon {
  margin-right: 8px;
  font-size: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .info-row {
    flex-direction: column;
  }
  
  .info-item {
    margin-bottom: 10px;
  }
  
  .rating-display {
    flex-direction: column;
    text-align: center;
  }
  
  .rating-badge-large {
    margin-right: 0;
    margin-bottom: 20px;
  }
  
  .scores-grid {
    grid-template-columns: 1fr;
  }
}
</style>