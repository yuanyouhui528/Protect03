<template>
  <div class="exchange-history">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>交换记录</h1>
      <p class="page-description">查看您的线索交换历史记录</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-section">
      <div class="stat-card">
        <div class="stat-icon success">
          <el-icon><Check /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalExchanges }}</div>
          <div class="stat-label">总交换次数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon info">
          <el-icon><TrendCharts /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalValue }}</div>
          <div class="stat-label">总交换价值</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon warning">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.thisMonthExchanges }}</div>
          <div class="stat-label">本月交换</div>
        </div>
      </div>
    </div>

    <!-- 筛选器 -->
    <div class="filters-section">
      <div class="filters">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          @change="handleDateRangeChange"
        />
        <el-select v-model="filterType" placeholder="交换类型" clearable @change="handleFilterChange">
          <el-option label="全部" value="" />
          <el-option label="我发起的" value="applicant" />
          <el-option label="我接受的" value="receiver" />
        </el-select>
        <el-button @click="resetFilters">重置</el-button>
      </div>
    </div>

    <!-- 交换历史列表 -->
    <div class="history-list" v-loading="loading">
      <div class="history-item" v-for="item in historyList" :key="item.id">
        <div class="history-header">
          <div class="exchange-info">
            <div class="exchange-type">
              <el-tag :type="item.applicantId === currentUserId ? 'primary' : 'success'" size="small">
                {{ item.applicantId === currentUserId ? '我发起' : '我接受' }}
              </el-tag>
            </div>
            <div class="exchange-time">
              <el-icon><Clock /></el-icon>
              <span>{{ formatDate(item.createdAt) }}</span>
            </div>
          </div>
          <div class="exchange-value">
            <span class="value-label">交换价值:</span>
            <span class="value-number">{{ item.exchangeValue }}积分</span>
          </div>
        </div>
        
        <div class="history-content">
          <div class="exchange-parties">
            <div class="party applicant">
              <div class="party-label">申请方</div>
              <div class="party-info">
                <el-avatar :size="32">{{ item.applicantName[0] }}</el-avatar>
                <span class="party-name">{{ item.applicantName }}</span>
              </div>
            </div>
            
            <div class="exchange-arrow">
              <el-icon><Right /></el-icon>
            </div>
            
            <div class="party receiver">
              <div class="party-label">接受方</div>
              <div class="party-info">
                <el-avatar :size="32">{{ item.targetLeadOwnerName[0] }}</el-avatar>
                <span class="party-name">{{ item.targetLeadOwnerName }}</span>
              </div>
            </div>
          </div>
          
          <div class="exchanged-leads">
            <div class="leads-info">
              <div class="leads-label">交换线索:</div>
              <div class="leads-content">{{ item.exchangedLeadTitles }}</div>
            </div>
          </div>
        </div>
        
        <div class="history-actions">
          <el-button size="small" @click="viewExchangeDetail(item)">查看详情</el-button>
          <el-button size="small" type="primary" @click="contactParty(item)">联系对方</el-button>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-if="!loading && historyList.length === 0" class="empty-state">
        <el-empty description="暂无交换记录">
          <el-button type="primary" @click="goToMarket">去交换市场</el-button>
        </el-empty>
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

    <!-- 交换详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="交换详情"
      width="600px"
    >
      <div class="exchange-detail" v-if="selectedExchange">
        <div class="detail-section">
          <h4>基本信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="label">交换时间:</span>
              <span class="value">{{ formatDateTime(selectedExchange.createdAt) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">交换价值:</span>
              <span class="value">{{ selectedExchange.exchangeValue }}积分</span>
            </div>
            <div class="detail-item">
              <span class="label">申请方:</span>
              <span class="value">{{ selectedExchange.applicantName }}</span>
            </div>
            <div class="detail-item">
              <span class="label">接受方:</span>
              <span class="value">{{ selectedExchange.targetLeadOwnerName }}</span>
            </div>
          </div>
        </div>
        
        <div class="detail-section">
          <h4>交换线索</h4>
          <div class="leads-detail">
            <p>{{ selectedExchange.exchangedLeadTitles }}</p>
          </div>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check, TrendCharts, Clock, Right } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { exchangeApi, type ExchangeHistory } from '@/api/exchange'

// 路由和用户信息
const router = useRouter()
const userStore = useUserStore()
const currentUserId = computed(() => userStore.userInfo?.id)

// 数据状态
const loading = ref(false)
const historyList = ref<ExchangeHistory[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 统计信息
const stats = reactive({
  totalExchanges: 0,
  totalValue: 0,
  thisMonthExchanges: 0
})

// 筛选器
const dateRange = ref<[string, string] | null>(null)
const filterType = ref('')

// 对话框
const detailDialogVisible = ref(false)
const selectedExchange = ref<ExchangeHistory | null>(null)

// 方法
const loadHistoryList = async () => {
  try {
    loading.value = true
    const params: any = {
      page: currentPage.value - 1,
      size: pageSize.value
    }
    
    // 添加日期筛选
    if (dateRange.value) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    
    // 添加类型筛选
    if (filterType.value) {
      params.type = filterType.value
    }
    
    const response = await exchangeApi.getMyExchangeHistory(params)
    historyList.value = response.data.content
    total.value = response.data.totalElements
  } catch (error) {
    ElMessage.error('加载交换历史失败')
  } finally {
    loading.value = false
  }
}

const loadStats = async () => {
  try {
    // 这里可以调用专门的统计接口，暂时用历史数据计算
    const response = await exchangeApi.getMyExchangeHistory({ size: 1000 })
    const allHistory = response.data.content
    
    stats.totalExchanges = allHistory.length
    stats.totalValue = allHistory.reduce((sum, item) => sum + item.exchangeValue, 0)
    
    // 计算本月交换次数
    const thisMonth = new Date().getMonth()
    const thisYear = new Date().getFullYear()
    stats.thisMonthExchanges = allHistory.filter(item => {
      const itemDate = new Date(item.createdAt)
      return itemDate.getMonth() === thisMonth && itemDate.getFullYear() === thisYear
    }).length
  } catch (error) {
    console.error('加载统计信息失败:', error)
  }
}

const handleDateRangeChange = () => {
  currentPage.value = 1
  loadHistoryList()
}

const handleFilterChange = () => {
  currentPage.value = 1
  loadHistoryList()
}

const resetFilters = () => {
  dateRange.value = null
  filterType.value = ''
  currentPage.value = 1
  loadHistoryList()
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  loadHistoryList()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  loadHistoryList()
}

const viewExchangeDetail = (item: ExchangeHistory) => {
  selectedExchange.value = item
  detailDialogVisible.value = true
}

const contactParty = (item: ExchangeHistory) => {
  const otherParty = item.applicantId === currentUserId.value 
    ? item.targetLeadOwnerName 
    : item.applicantName
  
  ElMessageBox.alert(
    `您可以通过以下方式联系 ${otherParty}:\n\n1. 站内消息\n2. 系统通知\n3. 客服协助`,
    '联系对方',
    {
      confirmButtonText: '确定',
      type: 'info'
    }
  )
}

const goToMarket = () => {
  router.push('/exchange/market')
}

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

const formatDateTime = (dateStr: string) => {
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadHistoryList()
  loadStats()
})
</script>

<style scoped>
.exchange-history {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  color: #303133;
}

.page-description {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.stats-section {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.stat-icon.success {
  background: #f0f9ff;
  color: #67c23a;
}

.stat-icon.info {
  background: #f0f9ff;
  color: #409eff;
}

.stat-icon.warning {
  background: #fdf6ec;
  color: #e6a23c;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.filters-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.filters {
  display: flex;
  gap: 12px;
  align-items: center;
}

.history-list {
  display: grid;
  gap: 16px;
  margin-bottom: 20px;
}

.history-item {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 20px;
  transition: box-shadow 0.3s;
}

.history-item:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.exchange-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.exchange-time {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #909399;
  font-size: 14px;
}

.exchange-value {
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

.history-content {
  margin-bottom: 16px;
}

.exchange-parties {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 12px;
}

.party {
  flex: 1;
  text-align: center;
}

.party-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.party-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.party-name {
  font-weight: bold;
  color: #303133;
}

.exchange-arrow {
  color: #409eff;
  font-size: 18px;
}

.exchanged-leads {
  background: #f5f7fa;
  padding: 12px;
  border-radius: 6px;
}

.leads-label {
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.leads-content {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
}

.history-actions {
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

/* 对话框样式 */
.exchange-detail {
  max-height: 60vh;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section h4 {
  margin: 0 0 12px 0;
  color: #303133;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 8px;
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
}

.detail-item .label {
  color: #909399;
  font-size: 14px;
}

.detail-item .value {
  color: #303133;
  font-weight: bold;
}

.leads-detail {
  background: #f5f7fa;
  padding: 12px;
  border-radius: 6px;
}

.leads-detail p {
  margin: 0;
  color: #606266;
  line-height: 1.5;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>