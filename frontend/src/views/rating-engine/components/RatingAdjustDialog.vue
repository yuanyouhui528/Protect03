<template>
  <el-dialog
    v-model="dialogVisible"
    title="手动调整评级"
    width="600px"
    :before-close="handleClose"
    destroy-on-close
  >
    <div v-loading="loading" class="rating-adjust">
      <!-- 当前评级信息 -->
      <div class="current-rating">
        <el-card class="rating-card">
          <template #header>
            <div class="card-header">
              <el-icon><InfoFilled /></el-icon>
              <span>当前评级</span>
            </div>
          </template>
          <div class="rating-info">
            <div class="lead-info">
              <div class="info-item">
                <label>线索标题:</label>
                <span>{{ leadData.title }}</span>
              </div>
              <div class="info-item">
                <label>企业名称:</label>
                <span>{{ leadData.companyName }}</span>
              </div>
            </div>
            <div class="current-rating-display">
              <div class="rating-badge">
                <RatingBadge 
                  :rating="leadData.rating" 
                  :score="leadData.score" 
                  mode="normal"
                />
              </div>
              <div class="rating-details">
                <div class="rating-text">当前: {{ leadData.rating }}级</div>
                <div class="score-text">评分: {{ leadData.score?.toFixed(1) || '0.0' }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 调整表单 -->
      <div class="adjust-form">
        <el-form
          ref="formRef"
          :model="adjustForm"
          :rules="formRules"
          label-width="100px"
          label-position="left"
        >
          <el-form-item label="新评级" prop="newRating">
            <el-radio-group v-model="adjustForm.newRating" @change="handleRatingChange">
              <el-radio-button 
                v-for="rating in ratingOptions" 
                :key="rating.value"
                :label="rating.value"
                :class="`rating-${rating.value.toLowerCase()}`"
              >
                {{ rating.label }}
              </el-radio-button>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="新评分" prop="newScore">
            <div class="score-input">
              <el-slider
                v-model="adjustForm.newScore"
                :min="0"
                :max="100"
                :step="0.1"
                :format-tooltip="formatTooltip"
                show-input
                :show-input-controls="false"
                class="score-slider"
              />
              <div class="score-range">
                <span>{{ getScoreRange(adjustForm.newRating) }}</span>
              </div>
            </div>
          </el-form-item>

          <el-form-item label="调整原因" prop="reason">
            <el-select
              v-model="adjustForm.reason"
              placeholder="请选择调整原因"
              style="width: 100%"
            >
              <el-option
                v-for="reason in reasonOptions"
                :key="reason.value"
                :label="reason.label"
                :value="reason.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="调整说明" prop="description">
            <el-input
              v-model="adjustForm.description"
              type="textarea"
              :rows="4"
              placeholder="请详细说明调整原因和依据..."
              maxlength="500"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="生效时间" prop="effectiveTime">
            <el-radio-group v-model="adjustForm.effectiveType">
              <el-radio label="immediate">立即生效</el-radio>
              <el-radio label="scheduled">定时生效</el-radio>
            </el-radio-group>
            <el-date-picker
              v-if="adjustForm.effectiveType === 'scheduled'"
              v-model="adjustForm.effectiveTime"
              type="datetime"
              placeholder="选择生效时间"
              style="margin-top: 10px; width: 100%"
              :disabled-date="disabledDate"
            />
          </el-form-item>
        </el-form>
      </div>

      <!-- 预览效果 -->
      <div class="preview-section">
        <el-card class="preview-card">
          <template #header>
            <div class="card-header">
              <el-icon><View /></el-icon>
              <span>调整预览</span>
            </div>
          </template>
          <div class="preview-content">
            <div class="preview-comparison">
              <div class="before-after">
                <div class="comparison-item">
                  <div class="comparison-label">调整前</div>
                  <div class="comparison-rating">
                    <RatingBadge 
                      :rating="leadData.rating" 
                      :score="leadData.score" 
                      mode="compact"
                    />
                  </div>
                </div>
                <div class="arrow">
                  <el-icon><ArrowRight /></el-icon>
                </div>
                <div class="comparison-item">
                  <div class="comparison-label">调整后</div>
                  <div class="comparison-rating">
                    <RatingBadge 
                      :rating="adjustForm.newRating" 
                      :score="adjustForm.newScore" 
                      mode="compact"
                    />
                  </div>
                </div>
              </div>
            </div>
            <div class="impact-analysis">
              <div class="impact-item">
                <label>评级变化:</label>
                <span :class="getRatingChangeClass()">
                  {{ getRatingChangeText() }}
                </span>
              </div>
              <div class="impact-item">
                <label>分数变化:</label>
                <span :class="getScoreChangeClass()">
                  {{ getScoreChangeText() }}
                </span>
              </div>
              <div class="impact-item">
                <label>价值变化:</label>
                <span :class="getValueChangeClass()">
                  {{ getValueChangeText() }}
                </span>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button @click="handleReset">
          <el-icon><RefreshLeft /></el-icon>
          重置
        </el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          <el-icon><Check /></el-icon>
          确认调整
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  InfoFilled,
  View,
  ArrowRight,
  RefreshLeft,
  Check
} from '@element-plus/icons-vue'
import RatingBadge from './RatingBadge.vue'
import { ratingEngineApi } from '@/api/rating-engine'

// 组件属性
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  leadData: {
    type: Object,
    default: () => ({})
  }
})

// 事件定义
const emit = defineEmits(['update:modelValue', 'success'])

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const formRef = ref(null)

// 表单数据
const adjustForm = reactive({
  newRating: '',
  newScore: 0,
  reason: '',
  description: '',
  effectiveType: 'immediate',
  effectiveTime: null
})

// 评级选项
const ratingOptions = [
  { label: 'A级', value: 'A' },
  { label: 'B级', value: 'B' },
  { label: 'C级', value: 'C' },
  { label: 'D级', value: 'D' }
]

// 调整原因选项
const reasonOptions = [
  { label: '信息补充完善', value: 'info_complete' },
  { label: '企业资质变更', value: 'qualification_change' },
  { label: '市场价值重估', value: 'market_revaluation' },
  { label: '客户反馈调整', value: 'customer_feedback' },
  { label: '业务策略调整', value: 'strategy_adjustment' },
  { label: '数据错误修正', value: 'data_correction' },
  { label: '其他原因', value: 'other' }
]

// 表单验证规则
const formRules = {
  newRating: [
    { required: true, message: '请选择新的评级', trigger: 'change' }
  ],
  newScore: [
    { required: true, message: '请输入新的评分', trigger: 'blur' },
    { type: 'number', min: 0, max: 100, message: '评分必须在0-100之间', trigger: 'blur' }
  ],
  reason: [
    { required: true, message: '请选择调整原因', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请填写调整说明', trigger: 'blur' },
    { min: 10, message: '调整说明至少10个字符', trigger: 'blur' }
  ]
}

// 计算属性
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 监听对话框显示状态
watch(
  () => props.modelValue,
  (visible) => {
    if (visible && props.leadData) {
      initForm()
    }
  }
)

// 方法定义

/**
 * 初始化表单
 */
const initForm = () => {
  adjustForm.newRating = props.leadData.rating || 'C'
  adjustForm.newScore = props.leadData.score || 50
  adjustForm.reason = ''
  adjustForm.description = ''
  adjustForm.effectiveType = 'immediate'
  adjustForm.effectiveTime = null
}

/**
 * 处理评级变化
 */
const handleRatingChange = (rating) => {
  // 根据评级自动调整分数范围
  const scoreRanges = {
    A: [80, 100],
    B: [60, 79],
    C: [40, 59],
    D: [0, 39]
  }
  
  const range = scoreRanges[rating]
  if (range) {
    const currentScore = adjustForm.newScore
    if (currentScore < range[0] || currentScore > range[1]) {
      adjustForm.newScore = Math.round((range[0] + range[1]) / 2)
    }
  }
}

/**
 * 获取分数范围文本
 */
const getScoreRange = (rating) => {
  const ranges = {
    A: '80-100分',
    B: '60-79分',
    C: '40-59分',
    D: '0-39分'
  }
  return ranges[rating] || '0-100分'
}

/**
 * 格式化滑块提示
 */
const formatTooltip = (value) => {
  return `${value.toFixed(1)}分`
}

/**
 * 禁用日期
 */
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 24 * 60 * 60 * 1000
}

/**
 * 获取评级变化样式类
 */
const getRatingChangeClass = () => {
  const oldRating = props.leadData.rating
  const newRating = adjustForm.newRating
  
  if (oldRating === newRating) return 'no-change'
  
  const ratingValues = { A: 4, B: 3, C: 2, D: 1 }
  const oldValue = ratingValues[oldRating] || 0
  const newValue = ratingValues[newRating] || 0
  
  return newValue > oldValue ? 'upgrade' : 'downgrade'
}

/**
 * 获取评级变化文本
 */
const getRatingChangeText = () => {
  const oldRating = props.leadData.rating
  const newRating = adjustForm.newRating
  
  if (oldRating === newRating) return '无变化'
  
  const ratingValues = { A: 4, B: 3, C: 2, D: 1 }
  const oldValue = ratingValues[oldRating] || 0
  const newValue = ratingValues[newRating] || 0
  
  return newValue > oldValue ? `${oldRating} → ${newRating} (提升)` : `${oldRating} → ${newRating} (降级)`
}

/**
 * 获取分数变化样式类
 */
const getScoreChangeClass = () => {
  const oldScore = props.leadData.score || 0
  const newScore = adjustForm.newScore
  
  if (Math.abs(oldScore - newScore) < 0.1) return 'no-change'
  return newScore > oldScore ? 'increase' : 'decrease'
}

/**
 * 获取分数变化文本
 */
const getScoreChangeText = () => {
  const oldScore = props.leadData.score || 0
  const newScore = adjustForm.newScore
  const diff = newScore - oldScore
  
  if (Math.abs(diff) < 0.1) return '无变化'
  
  const sign = diff > 0 ? '+' : ''
  return `${sign}${diff.toFixed(1)}分`
}

/**
 * 获取价值变化样式类
 */
const getValueChangeClass = () => {
  const oldValue = getRatingValue(props.leadData.rating)
  const newValue = getRatingValue(adjustForm.newRating)
  
  if (oldValue === newValue) return 'no-change'
  return newValue > oldValue ? 'increase' : 'decrease'
}

/**
 * 获取价值变化文本
 */
const getValueChangeText = () => {
  const oldValue = getRatingValue(props.leadData.rating)
  const newValue = getRatingValue(adjustForm.newRating)
  const diff = newValue - oldValue
  
  if (diff === 0) return '无变化'
  
  const sign = diff > 0 ? '+' : ''
  return `${sign}${diff}分`
}

/**
 * 获取评级价值
 */
const getRatingValue = (rating) => {
  const values = { A: 8, B: 4, C: 2, D: 1 }
  return values[rating] || 0
}

/**
 * 重置表单
 */
const handleReset = () => {
  initForm()
  formRef.value?.clearValidate()
}

/**
 * 关闭对话框
 */
const handleClose = () => {
  dialogVisible.value = false
}

/**
 * 提交调整
 */
const handleSubmit = async () => {
  try {
    const valid = await formRef.value?.validate()
    if (!valid) return

    // 确认调整
    await ElMessageBox.confirm(
      '确认要调整该线索的评级吗？调整后将记录到评级历史中。',
      '确认调整',
      { type: 'warning' }
    )

    submitting.value = true
    
    const adjustData = {
      leadId: props.leadData.leadId,
      newRating: adjustForm.newRating,
      newScore: adjustForm.newScore,
      reason: adjustForm.reason,
      description: adjustForm.description,
      effectiveTime: adjustForm.effectiveType === 'immediate' ? null : adjustForm.effectiveTime
    }

    await ratingEngineApi.adjustRating(adjustData)
    
    ElMessage.success('评级调整成功')
    emit('success')
    handleClose()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('调整评级失败:', error)
      ElMessage.error('调整评级失败')
    }
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.rating-adjust {
  max-height: 70vh;
  overflow-y: auto;
}

.rating-card,
.preview-card {
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

.lead-info {
  margin-bottom: 15px;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.info-item label {
  font-weight: 600;
  color: #666;
  margin-right: 8px;
  min-width: 80px;
}

.current-rating-display {
  display: flex;
  align-items: center;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.rating-badge {
  margin-right: 20px;
}

.rating-details {
  flex: 1;
}

.rating-text {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 5px;
}

.score-text {
  font-size: 14px;
  color: #666;
}

.adjust-form {
  margin-bottom: 20px;
}

.rating-a {
  background: #52c41a !important;
  border-color: #52c41a !important;
  color: white !important;
}

.rating-b {
  background: #1890ff !important;
  border-color: #1890ff !important;
  color: white !important;
}

.rating-c {
  background: #faad14 !important;
  border-color: #faad14 !important;
  color: white !important;
}

.rating-d {
  background: #ff4d4f !important;
  border-color: #ff4d4f !important;
  color: white !important;
}

.score-input {
  width: 100%;
}

.score-slider {
  margin-bottom: 10px;
}

.score-range {
  text-align: center;
  font-size: 12px;
  color: #999;
}

.preview-comparison {
  padding: 15px 0;
}

.before-after {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.comparison-item {
  text-align: center;
  flex: 1;
}

.comparison-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.comparison-rating {
  display: flex;
  justify-content: center;
}

.arrow {
  margin: 0 30px;
  font-size: 24px;
  color: #409eff;
}

.impact-analysis {
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.impact-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.impact-item label {
  font-weight: 600;
  color: #666;
  margin-right: 8px;
  min-width: 80px;
}

.no-change {
  color: #909399;
}

.upgrade,
.increase {
  color: #52c41a;
  font-weight: 600;
}

.downgrade,
.decrease {
  color: #ff4d4f;
  font-weight: 600;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .before-after {
    flex-direction: column;
  }
  
  .arrow {
    margin: 15px 0;
    transform: rotate(90deg);
  }
  
  .impact-item {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .impact-item label {
    margin-bottom: 5px;
  }
}
</style>