<template>
  <div class="lead-favorites">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>我的收藏</h2>
        <p class="page-description">查看和管理收藏的线索</p>
      </div>
      <div class="header-actions">
        <el-button @click="refreshData" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
        <el-button 
          type="danger" 
          :disabled="selectedLeads.length === 0"
          @click="batchUnfavorite"
        >
          <el-icon><Delete /></el-icon>
          批量取消收藏
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="filter-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索企业名称、行业等"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="行业方向">
          <el-select
            v-model="searchForm.industryDirection"
            placeholder="选择行业"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="industry in industryOptions"
              :key="industry.value"
              :label="industry.label"
              :value="industry.value"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="投资金额">
          <el-select
            v-model="searchForm.investmentRange"
            placeholder="选择金额范围"
            clearable
            style="width: 150px"
          >
            <el-option label="100万以下" value="0-100" />
            <el-option label="100-500万" value="100-500" />
            <el-option label="500-1000万" value="500-1000" />
            <el-option label="1000万以上" value="1000+" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="收藏时间">
          <el-date-picker
            v-model="searchForm.favoriteTimeRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 240px"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetSearch">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计信息 -->
    <div class="stats-bar">
      <div class="stats-item">
        <span class="stats-label">总收藏数：</span>
        <span class="stats-value">{{ pagination.total }}</span>
      </div>
      <div class="stats-item">
        <span class="stats-label">本月新增：</span>
        <span class="stats-value">{{ monthlyCount }}</span>
      </div>
      <div class="stats-item">
        <span class="stats-label">已选择：</span>
        <span class="stats-value">{{ selectedLeads.length }}</span>
      </div>
      
      <div class="view-controls">
        <el-radio-group v-model="viewMode" size="small">
          <el-radio-button label="list">
            <el-icon><List /></el-icon>
            列表
          </el-radio-button>
          <el-radio-button label="grid">
            <el-icon><Grid /></el-icon>
            卡片
          </el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- 收藏列表 -->
    <el-card v-loading="loading" class="list-card">
      <!-- 列表视图 -->
      <div v-if="viewMode === 'list'">
        <el-table
          :data="favoriteLeads"
          @selection-change="handleSelectionChange"
          stripe
          style="width: 100%"
        >
          <el-table-column type="selection" width="55" />
          
          <el-table-column prop="companyName" label="企业名称" min-width="200">
            <template #default="{ row }">
              <div class="company-info">
                <div class="company-name" @click="viewDetail(row.id)">{{ row.companyName }}</div>
                <div class="company-meta">
                  <el-tag size="small">{{ getIndustryLabel(row.industryDirection) }}</el-tag>
                  <span class="location">{{ row.location }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="investmentAmount" label="投资金额" width="120">
            <template #default="{ row }">
              <span class="amount">{{ formatAmount(row.investmentAmount) }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="investmentType" label="投资方式" width="100">
            <template #default="{ row }">
              {{ getInvestmentTypeLabel(row.investmentType) }}
            </template>
          </el-table-column>
          
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusTagType(row.status)" size="small">
                {{ getStatusLabel(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="favoriteTime" label="收藏时间" width="120">
            <template #default="{ row }">
              {{ formatDate(row.favoriteTime) }}
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="viewDetail(row.id)">
                查看详情
              </el-button>
              <el-button type="warning" size="small" @click="unfavorite(row)">
                取消收藏
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 卡片视图 -->
      <div v-else class="grid-view">
        <div class="grid-container">
          <div
            v-for="lead in favoriteLeads"
            :key="lead.id"
            class="lead-card"
            :class="{ selected: selectedLeads.includes(lead.id) }"
            @click="toggleSelection(lead)"
          >
            <div class="card-header">
              <div class="company-name" @click.stop="viewDetail(lead.id)">{{ lead.companyName }}</div>
              <el-checkbox
                :model-value="selectedLeads.includes(lead.id)"
                @change="toggleSelection(lead)"
                @click.stop
              />
            </div>
            
            <div class="card-content">
              <div class="info-row">
                <span class="label">行业：</span>
                <el-tag size="small">{{ getIndustryLabel(lead.industryDirection) }}</el-tag>
              </div>
              <div class="info-row">
                <span class="label">投资金额：</span>
                <span class="amount">{{ formatAmount(lead.investmentAmount) }}</span>
              </div>
              <div class="info-row">
                <span class="label">投资方式：</span>
                <span>{{ getInvestmentTypeLabel(lead.investmentType) }}</span>
              </div>
              <div class="info-row">
                <span class="label">所在地区：</span>
                <span>{{ lead.location }}</span>
              </div>
              <div class="info-row">
                <span class="label">状态：</span>
                <el-tag :type="getStatusTagType(lead.status)" size="small">
                  {{ getStatusLabel(lead.status) }}
                </el-tag>
              </div>
            </div>
            
            <div class="card-footer">
              <div class="favorite-time">
                <el-icon><Star /></el-icon>
                {{ formatDate(lead.favoriteTime) }}
              </div>
              <div class="card-actions">
                <el-button type="primary" size="small" @click.stop="viewDetail(lead.id)">
                  查看详情
                </el-button>
                <el-button type="warning" size="small" @click.stop="unfavorite(lead)">
                  取消收藏
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="!loading && favoriteLeads.length === 0" class="empty-state">
        <el-empty description="暂无收藏的线索">
          <el-button type="primary" @click="$router.push('/leads/list')">
            去发现线索
          </el-button>
        </el-empty>
      </div>
    </el-card>

    <!-- 分页 -->
    <div v-if="favoriteLeads.length > 0" class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Refresh,
  Delete,
  Search,
  List,
  Grid,
  Star
} from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const viewMode = ref('list')
const favoriteLeads = ref<any[]>([])
const selectedLeads = ref<number[]>([])
const monthlyCount = ref(0)

// 搜索表单
const searchForm = reactive({
  keyword: '',
  industryDirection: '',
  investmentRange: '',
  favoriteTimeRange: null as any
})

// 分页信息
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 行业选项
const industryOptions = [
  { label: '制造业', value: 'MANUFACTURING' },
  { label: '信息技术', value: 'IT' },
  { label: '金融服务', value: 'FINANCE' },
  { label: '房地产', value: 'REAL_ESTATE' },
  { label: '批发零售', value: 'RETAIL' },
  { label: '交通运输', value: 'TRANSPORTATION' },
  { label: '住宿餐饮', value: 'HOSPITALITY' },
  { label: '文化娱乐', value: 'ENTERTAINMENT' },
  { label: '教育培训', value: 'EDUCATION' },
  { label: '医疗健康', value: 'HEALTHCARE' },
  { label: '其他', value: 'OTHER' }
]

// 获取收藏列表
const getFavoriteLeads = async () => {
  loading.value = true
  
  try {
    const params = {
      page: pagination.page - 1, // 后端从0开始
      size: pagination.size,
      ...searchForm
    }
    
    // 处理时间范围
    if (searchForm.favoriteTimeRange && searchForm.favoriteTimeRange.length === 2) {
      params.startDate = searchForm.favoriteTimeRange[0]
      params.endDate = searchForm.favoriteTimeRange[1]
      delete params.favoriteTimeRange
    }
    
    const response = await axios.get('/api/leads/favorites', { params })
    
    if (response.data.success) {
      favoriteLeads.value = response.data.data.content || []
      pagination.total = response.data.data.totalElements || 0
    } else {
      ElMessage.error(response.data.message || '获取收藏列表失败')
    }
  } catch (error: any) {
    console.error('获取收藏列表失败:', error)
    ElMessage.error('获取收藏列表失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}

// 获取本月收藏统计
const getMonthlyStats = async () => {
  try {
    const response = await axios.get('/api/leads/favorites/monthly-stats')
    if (response.data.success) {
      monthlyCount.value = response.data.data.count || 0
    }
  } catch (error: any) {
    console.error('获取月度统计失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  getFavoriteLeads()
}

// 重置搜索
const resetSearch = () => {
  Object.assign(searchForm, {
    keyword: '',
    industryDirection: '',
    investmentRange: '',
    favoriteTimeRange: null
  })
  pagination.page = 1
  getFavoriteLeads()
}

// 刷新数据
const refreshData = () => {
  getFavoriteLeads()
  getMonthlyStats()
}

// 分页处理
const handlePageChange = (page: number) => {
  pagination.page = page
  getFavoriteLeads()
}

const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.page = 1
  getFavoriteLeads()
}

// 选择处理
const handleSelectionChange = (selection: any[]) => {
  selectedLeads.value = selection.map(item => item.id)
}

const toggleSelection = (lead: any) => {
  const index = selectedLeads.value.indexOf(lead.id)
  if (index > -1) {
    selectedLeads.value.splice(index, 1)
  } else {
    selectedLeads.value.push(lead.id)
  }
}

// 查看详情
const viewDetail = (leadId: number) => {
  router.push(`/leads/detail/${leadId}`)
}

// 取消收藏
const unfavorite = async (lead: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消收藏「${lead.companyName}」吗？`,
      '取消收藏',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await axios.post('/api/leads/unfavorite', {
      leadId: lead.id
    })
    
    if (response.data.success) {
      ElMessage.success('取消收藏成功')
      getFavoriteLeads()
      getMonthlyStats()
    } else {
      ElMessage.error(response.data.message || '取消收藏失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
      ElMessage.error('取消收藏失败，请重试')
    }
  }
}

// 批量取消收藏
const batchUnfavorite = async () => {
  if (selectedLeads.value.length === 0) {
    ElMessage.warning('请先选择要取消收藏的线索')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要取消收藏选中的 ${selectedLeads.value.length} 个线索吗？`,
      '批量取消收藏',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await axios.post('/api/leads/batch-unfavorite', {
      leadIds: selectedLeads.value
    })
    
    if (response.data.success) {
      ElMessage.success('批量取消收藏成功')
      selectedLeads.value = []
      getFavoriteLeads()
      getMonthlyStats()
    } else {
      ElMessage.error(response.data.message || '批量取消收藏失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('批量取消收藏失败:', error)
      ElMessage.error('批量取消收藏失败，请重试')
    }
  }
}

// 工具函数
const getIndustryLabel = (value: string) => {
  const industry = industryOptions.find(item => item.value === value)
  return industry ? industry.label : value
}

const getInvestmentTypeLabel = (value: string) => {
  const types: Record<string, string> = {
    'EQUITY': '股权投资',
    'DEBT': '债权投资',
    'CONVERTIBLE': '可转债',
    'MIXED': '混合投资'
  }
  return types[value] || value
}

const getStatusLabel = (value: string) => {
  const statuses: Record<string, string> = {
    'DRAFT': '草稿',
    'PENDING_AUDIT': '待审核',
    'PUBLISHED': '已发布',
    'EXCHANGING': '交换中',
    'COMPLETED': '已完成',
    'OFFLINE': '已下架',
    'REJECTED': '已拒绝'
  }
  return statuses[value] || value
}

const getStatusTagType = (value: string) => {
  const types: Record<string, string> = {
    'DRAFT': 'info',
    'PENDING_AUDIT': 'warning',
    'PUBLISHED': 'success',
    'EXCHANGING': 'primary',
    'COMPLETED': 'success',
    'OFFLINE': 'info',
    'REJECTED': 'danger'
  }
  return types[value] || 'info'
}

const formatAmount = (amount: number) => {
  if (!amount) return '面议'
  return `${amount}万元`
}

const formatDate = (date: string) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

// 组件挂载时获取数据
onMounted(() => {
  getFavoriteLeads()
  getMonthlyStats()
})
</script>

<style scoped>
.lead-favorites {
  padding: 0;
}

/* 页面头部样式 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.header-left h2 {
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

.header-actions {
  display: flex;
  gap: 12px;
}

/* 筛选卡片样式 */
.filter-card {
  margin-bottom: 16px;
}

/* 统计栏样式 */
.stats-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #f8f9fa;
  border-radius: 6px;
  margin-bottom: 16px;
}

.stats-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.stats-label {
  color: #909399;
  font-size: 14px;
}

.stats-value {
  color: #409eff;
  font-weight: 600;
  font-size: 16px;
}

.view-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 列表卡片样式 */
.list-card {
  margin-bottom: 24px;
}

/* 表格样式 */
.company-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.company-name {
  font-weight: 500;
  color: #409eff;
  cursor: pointer;
  transition: color 0.3s;
}

.company-name:hover {
  color: #66b1ff;
}

.company-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}

.location {
  color: #909399;
}

.amount {
  font-weight: 600;
  color: #f56c6c;
}

/* 卡片视图样式 */
.grid-view {
  padding: 16px 0;
}

.grid-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.lead-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  background: #fff;
  transition: all 0.3s;
  cursor: pointer;
}

.lead-card:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.lead-card.selected {
  border-color: #409eff;
  background-color: #f0f9ff;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.card-header .company-name {
  font-size: 16px;
  font-weight: 600;
  color: #409eff;
  cursor: pointer;
  flex: 1;
  margin-right: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-content {
  margin-bottom: 16px;
}

.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
}

.info-row .label {
  width: 80px;
  color: #909399;
  flex-shrink: 0;
}

.info-row .amount {
  font-weight: 600;
  color: #f56c6c;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.favorite-time {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
}

.card-actions {
  display: flex;
  gap: 8px;
}

/* 空状态样式 */
.empty-state {
  padding: 60px 20px;
}

/* 分页样式 */
.pagination-container {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .header-actions {
    justify-content: flex-end;
  }
  
  .stats-bar {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .view-controls {
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .grid-container {
    grid-template-columns: 1fr;
  }
  
  .stats-bar {
    padding: 12px 16px;
  }
  
  .card-footer {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .card-actions {
    justify-content: center;
  }
}

/* Element Plus 样式覆盖 */
:deep(.el-table .el-table__cell) {
  padding: 12px 0;
}

:deep(.el-card__body) {
  padding: 20px;
}

:deep(.el-form--inline .el-form-item) {
  margin-right: 16px;
  margin-bottom: 16px;
}

:deep(.el-pagination) {
  justify-content: center;
}
</style>