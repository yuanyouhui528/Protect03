<template>
  <div class="exchange-market">
    <!-- 页面标题和统计信息 -->
    <div class="market-header">
      <h1>交换市场</h1>
      <div class="stats-cards">
        <div class="stat-card">
          <div class="stat-number">{{ stats.totalLeads }}</div>
          <div class="stat-label">可交换线索</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">{{ stats.myCredits }}</div>
          <div class="stat-label">我的积分</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">{{ stats.pendingApplications }}</div>
          <div class="stat-label">待处理申请</div>
        </div>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-filters">
      <div class="search-bar">
        <el-input
          v-model="searchQuery"
          placeholder="搜索线索标题、公司名称..."
          prefix-icon="Search"
          @input="handleSearch"
          clearable
        />
      </div>
      <div class="filters">
        <el-select v-model="filters.rating" placeholder="线索评级" clearable @change="handleFilter">
          <el-option label="A级" value="A" />
          <el-option label="B级" value="B" />
          <el-option label="C级" value="C" />
          <el-option label="D级" value="D" />
        </el-select>
        <el-select v-model="filters.industry" placeholder="行业" clearable @change="handleFilter">
          <el-option label="金融" value="金融" />
          <el-option label="科技" value="科技" />
          <el-option label="制造" value="制造" />
          <el-option label="教育" value="教育" />
          <el-option label="医疗" value="医疗" />
        </el-select>
        <el-select v-model="filters.region" placeholder="地区" clearable @change="handleFilter">
          <el-option label="北京" value="北京" />
          <el-option label="上海" value="上海" />
          <el-option label="广州" value="广州" />
          <el-option label="深圳" value="深圳" />
          <el-option label="杭州" value="杭州" />
        </el-select>
        <el-button @click="resetFilters">重置筛选</el-button>
      </div>
    </div>

    <!-- 线索列表 -->
    <div class="leads-list" v-loading="loading">
      <div class="lead-card" v-for="lead in leads" :key="lead.id">
        <div class="lead-header">
          <div class="lead-title">
            <h3>{{ lead.title }}</h3>
            <el-tag :type="getRatingType(lead.rating)" size="small">{{ lead.rating }}级</el-tag>
          </div>
          <div class="lead-value">
            <span class="value-label">价值:</span>
            <span class="value-number">{{ lead.value }}积分</span>
          </div>
        </div>
        
        <div class="lead-content">
          <div class="lead-info">
            <p><strong>公司:</strong> {{ lead.companyName }}</p>
            <p><strong>行业:</strong> {{ lead.industry }}</p>
            <p><strong>地区:</strong> {{ lead.region }}</p>
            <p><strong>联系人:</strong> {{ lead.contactName }}</p>
            <p><strong>描述:</strong> {{ lead.description }}</p>
          </div>
          
          <div class="lead-owner">
            <div class="owner-info">
              <el-avatar :size="40" :src="lead.ownerAvatar">{{ lead.ownerName[0] }}</el-avatar>
              <div class="owner-details">
                <div class="owner-name">{{ lead.ownerName }}</div>
                <div class="owner-credit">积分: {{ lead.ownerCredit }}</div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="lead-actions">
          <el-button 
            type="primary" 
            @click="openExchangeDialog(lead)"
            :disabled="lead.ownerId === currentUserId"
          >
            申请交换
          </el-button>
          <el-button @click="viewLeadDetail(lead)">查看详情</el-button>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-if="!loading && leads.length === 0" class="empty-state">
        <el-empty description="暂无可交换的线索" />
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="total > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 交换申请对话框 -->
    <el-dialog
      v-model="exchangeDialogVisible"
      title="申请线索交换"
      width="600px"
      @close="resetExchangeForm"
    >
      <div class="exchange-form" v-if="selectedLead">
        <div class="target-lead">
          <h4>目标线索</h4>
          <div class="lead-summary">
            <span class="lead-name">{{ selectedLead.title }}</span>
            <el-tag :type="getRatingType(selectedLead.rating)" size="small">{{ selectedLead.rating }}级</el-tag>
            <span class="lead-value">{{ selectedLead.value }}积分</span>
          </div>
        </div>
        
        <div class="offered-leads">
          <h4>我的线索 <span class="required">*</span></h4>
          <div class="my-leads-list">
            <div 
              v-for="myLead in myLeads" 
              :key="myLead.id"
              class="my-lead-item"
              :class="{ selected: exchangeForm.offeredLeadIds.includes(myLead.id) }"
              @click="toggleLeadSelection(myLead.id)"
            >
              <el-checkbox 
                :model-value="exchangeForm.offeredLeadIds.includes(myLead.id)"
                @change="toggleLeadSelection(myLead.id)"
              />
              <div class="lead-info">
                <span class="lead-name">{{ myLead.title }}</span>
                <el-tag :type="getRatingType(myLead.rating)" size="small">{{ myLead.rating }}级</el-tag>
                <span class="lead-value">{{ myLead.value }}积分</span>
              </div>
            </div>
          </div>
          
          <div class="value-comparison" v-if="exchangeForm.offeredLeadIds.length > 0">
            <div class="value-item">
              <span>我的线索总价值:</span>
              <span class="value">{{ offeredTotalValue }}积分</span>
            </div>
            <div class="value-item">
              <span>目标线索价值:</span>
              <span class="value">{{ selectedLead.value }}积分</span>
            </div>
            <div class="value-item" :class="{ fair: isExchangeFair, unfair: !isExchangeFair }">
              <span>价值差异:</span>
              <span class="value">{{ valueDifference }}积分</span>
            </div>
            <div v-if="!isExchangeFair" class="unfair-notice">
              <el-alert
                :title="valueDifference > 0 ? '您的线索价值较高，可能需要对方补充积分' : '您的线索价值较低，可能需要补充积分'"
                type="warning"
                show-icon
                :closable="false"
              />
            </div>
          </div>
        </div>
        
        <div class="exchange-reason">
          <h4>交换理由 <span class="required">*</span></h4>
          <el-input
            v-model="exchangeForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请说明您申请交换的理由..."
            maxlength="500"
            show-word-limit
          />
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="exchangeDialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="submitExchangeApplication"
            :loading="submitting"
            :disabled="!canSubmitExchange"
          >
            提交申请
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { exchangeApi } from '@/api/exchange'
import { leadApi } from '@/api/lead'

// 用户信息
const userStore = useUserStore()
const currentUserId = computed(() => userStore.userInfo?.id)

// 数据状态
const loading = ref(false)
const submitting = ref(false)
const leads = ref([])
const myLeads = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 统计信息
const stats = reactive({
  totalLeads: 0,
  myCredits: 0,
  pendingApplications: 0
})

// 搜索和筛选
const searchQuery = ref('')
const filters = reactive({
  rating: '',
  industry: '',
  region: ''
})

// 交换对话框
const exchangeDialogVisible = ref(false)
const selectedLead = ref(null)
const exchangeForm = reactive({
  offeredLeadIds: [],
  reason: ''
})

// 计算属性
const offeredTotalValue = computed(() => {
  return exchangeForm.offeredLeadIds.reduce((total, leadId) => {
    const lead = myLeads.value.find(l => l.id === leadId)
    return total + (lead ? lead.value : 0)
  }, 0)
})

const valueDifference = computed(() => {
  return offeredTotalValue.value - (selectedLead.value?.value || 0)
})

const isExchangeFair = computed(() => {
  const diff = Math.abs(valueDifference.value)
  const targetValue = selectedLead.value?.value || 0
  return diff <= targetValue * 0.1 // 10%的差异范围内认为公平
})

const canSubmitExchange = computed(() => {
  return exchangeForm.offeredLeadIds.length > 0 && 
         exchangeForm.reason.trim().length > 0
})

// 方法
const getRatingType = (rating: string) => {
  const types = { A: 'danger', B: 'warning', C: 'info', D: 'success' }
  return types[rating] || 'info'
}

const loadLeads = async () => {
  try {
    loading.value = true
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      search: searchQuery.value,
      ...filters
    }
    
    const response = await leadApi.getExchangeableLeads(params)
    leads.value = response.data.content
    total.value = response.data.totalElements
  } catch (error) {
    ElMessage.error('加载线索列表失败')
  } finally {
    loading.value = false
  }
}

const loadMyLeads = async () => {
  try {
    const response = await leadApi.getMyLeads({ exchangeable: true })
    myLeads.value = response.data
  } catch (error) {
    ElMessage.error('加载我的线索失败')
  }
}

const loadStats = async () => {
  try {
    const [leadsResponse, creditResponse, applicationsResponse] = await Promise.all([
      leadApi.getExchangeableLeadsCount(),
      exchangeApi.getUserCreditInfo(),
      exchangeApi.getMyApplications({ status: 'PENDING' })
    ])
    
    stats.totalLeads = leadsResponse.data
    stats.myCredits = creditResponse.data.balance
    stats.pendingApplications = applicationsResponse.data.totalElements
  } catch (error) {
    console.error('加载统计信息失败:', error)
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadLeads()
}

const handleFilter = () => {
  currentPage.value = 1
  loadLeads()
}

const resetFilters = () => {
  filters.rating = ''
  filters.industry = ''
  filters.region = ''
  searchQuery.value = ''
  currentPage.value = 1
  loadLeads()
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  loadLeads()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  loadLeads()
}

const openExchangeDialog = (lead: any) => {
  selectedLead.value = lead
  exchangeDialogVisible.value = true
  loadMyLeads()
}

const toggleLeadSelection = (leadId: number) => {
  const index = exchangeForm.offeredLeadIds.indexOf(leadId)
  if (index > -1) {
    exchangeForm.offeredLeadIds.splice(index, 1)
  } else {
    exchangeForm.offeredLeadIds.push(leadId)
  }
}

const resetExchangeForm = () => {
  exchangeForm.offeredLeadIds = []
  exchangeForm.reason = ''
  selectedLead.value = null
}

const submitExchangeApplication = async () => {
  try {
    submitting.value = true
    
    const applicationData = {
      targetLeadId: selectedLead.value.id,
      offeredLeadIds: exchangeForm.offeredLeadIds,
      reason: exchangeForm.reason
    }
    
    await exchangeApi.applyForExchange(applicationData)
    
    ElMessage.success('交换申请提交成功')
    exchangeDialogVisible.value = false
    resetExchangeForm()
    loadLeads() // 刷新列表
    loadStats() // 刷新统计
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '提交申请失败')
  } finally {
    submitting.value = false
  }
}

const viewLeadDetail = (lead: any) => {
  // 这里可以打开线索详情对话框或跳转到详情页面
  ElMessageBox.alert(
    `线索详情:\n公司: ${lead.companyName}\n行业: ${lead.industry}\n地区: ${lead.region}\n联系人: ${lead.contactName}\n描述: ${lead.description}`,
    '线索详情',
    { confirmButtonText: '确定' }
  )
}

// 生命周期
onMounted(() => {
  loadLeads()
  loadStats()
})
</script>

<style scoped>
.exchange-market {
  padding: 20px;
}

.market-header {
  margin-bottom: 24px;
}

.market-header h1 {
  margin: 0 0 16px 0;
  color: #303133;
}

.stats-cards {
  display: flex;
  gap: 16px;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  text-align: center;
  min-width: 120px;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.search-filters {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.search-bar {
  margin-bottom: 16px;
}

.search-bar .el-input {
  max-width: 400px;
}

.filters {
  display: flex;
  gap: 12px;
  align-items: center;
}

.filters .el-select {
  width: 120px;
}

.leads-list {
  display: grid;
  gap: 16px;
  margin-bottom: 20px;
}

.lead-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 20px;
  transition: box-shadow 0.3s;
}

.lead-card:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.lead-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.lead-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.lead-title h3 {
  margin: 0;
  color: #303133;
  font-size: 18px;
}

.lead-value {
  text-align: right;
}

.value-label {
  color: #909399;
  font-size: 14px;
}

.value-number {
  color: #409eff;
  font-weight: bold;
  font-size: 16px;
  margin-left: 4px;
}

.lead-content {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
}

.lead-info {
  flex: 1;
}

.lead-info p {
  margin: 4px 0;
  color: #606266;
  font-size: 14px;
}

.lead-owner {
  min-width: 150px;
}

.owner-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.owner-details {
  flex: 1;
}

.owner-name {
  font-weight: bold;
  color: #303133;
  font-size: 14px;
}

.owner-credit {
  color: #909399;
  font-size: 12px;
}

.lead-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.empty-state {
  text-align: center;
  padding: 40px;
}

.pagination {
  display: flex;
  justify-content: center;
}

/* 交换对话框样式 */
.exchange-form {
  max-height: 60vh;
  overflow-y: auto;
}

.target-lead {
  margin-bottom: 20px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 6px;
}

.target-lead h4 {
  margin: 0 0 8px 0;
  color: #303133;
}

.lead-summary {
  display: flex;
  align-items: center;
  gap: 8px;
}

.lead-name {
  font-weight: bold;
  color: #303133;
}

.offered-leads h4 {
  margin: 0 0 12px 0;
  color: #303133;
}

.required {
  color: #f56c6c;
}

.my-leads-list {
  max-height: 200px;
  overflow-y: auto;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  margin-bottom: 16px;
}

.my-lead-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
  transition: background-color 0.3s;
}

.my-lead-item:last-child {
  border-bottom: none;
}

.my-lead-item:hover {
  background-color: #f5f7fa;
}

.my-lead-item.selected {
  background-color: #ecf5ff;
}

.my-lead-item .lead-info {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.value-comparison {
  background: #f9f9f9;
  padding: 16px;
  border-radius: 6px;
  margin-bottom: 16px;
}

.value-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.value-item:last-child {
  margin-bottom: 0;
}

.value-item.fair .value {
  color: #67c23a;
}

.value-item.unfair .value {
  color: #f56c6c;
}

.unfair-notice {
  margin-top: 12px;
}

.exchange-reason h4 {
  margin: 0 0 8px 0;
  color: #303133;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>