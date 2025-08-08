<template>
  <div class="exchange-applications">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>交换申请管理</h1>
      <p class="page-description">管理您发起和收到的交换申请</p>
    </div>

    <!-- 标签页 -->
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <!-- 我发起的申请 -->
      <el-tab-pane label="我发起的" name="sent">
        <div class="applications-list" v-loading="sentLoading">
          <div class="application-item" v-for="app in sentApplications" :key="app.id">
            <div class="application-header">
              <div class="application-info">
                <h3>{{ app.targetLeadTitle }}</h3>
                <el-tag :type="getStatusType(app.status)" size="small">
                  {{ getStatusText(app.status) }}
                </el-tag>
              </div>
              <div class="application-time">
                <span class="time-label">申请时间:</span>
                <span class="time-value">{{ formatDateTime(app.createdAt) }}</span>
              </div>
            </div>
            
            <div class="application-content">
              <div class="target-info">
                <div class="info-item">
                  <span class="label">目标线索所有者:</span>
                  <span class="value">{{ app.targetLeadOwnerName }}</span>
                </div>
                <div class="info-item">
                  <span class="label">我提供的线索:</span>
                  <span class="value">{{ app.offeredLeadTitles }}</span>
                </div>
                <div class="info-item">
                  <span class="label">申请理由:</span>
                  <span class="value reason">{{ app.exchangeReason }}</span>
                </div>
                <div class="info-item" v-if="app.responseMessage">
                  <span class="label">回复消息:</span>
                  <span class="value response">{{ app.responseMessage }}</span>
                </div>
              </div>
            </div>
            
            <div class="application-actions">
              <el-button 
                size="small" 
                @click="viewApplicationDetail(app)"
              >
                查看详情
              </el-button>
              <el-button 
                v-if="app.status === 'PENDING'"
                size="small" 
                type="danger" 
                @click="cancelApplication(app.id)"
              >
                取消申请
              </el-button>
              <el-button 
                v-if="app.status === 'APPROVED'"
                size="small" 
                type="success" 
                @click="contactOwner(app)"
              >
                联系对方
              </el-button>
            </div>
          </div>
          
          <!-- 空状态 -->
          <div v-if="!sentLoading && sentApplications.length === 0" class="empty-state">
            <el-empty description="暂无发起的申请">
              <el-button type="primary" @click="goToMarket">去交换市场</el-button>
            </el-empty>
          </div>
        </div>
        
        <!-- 分页 -->
        <div class="pagination" v-if="sentTotal > 0">
          <el-pagination
            v-model:current-page="sentCurrentPage"
            v-model:page-size="sentPageSize"
            :page-sizes="[10, 20, 50]"
            :total="sentTotal"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSentSizeChange"
            @current-change="handleSentCurrentChange"
          />
        </div>
      </el-tab-pane>
      
      <!-- 我收到的申请 -->
      <el-tab-pane label="我收到的" name="received">
        <div class="applications-list" v-loading="receivedLoading">
          <div class="application-item" v-for="app in receivedApplications" :key="app.id">
            <div class="application-header">
              <div class="application-info">
                <h3>{{ app.targetLeadTitle }}</h3>
                <el-tag :type="getStatusType(app.status)" size="small">
                  {{ getStatusText(app.status) }}
                </el-tag>
                <el-tag v-if="isExpiringSoon(app.expiresAt)" type="warning" size="small">
                  即将过期
                </el-tag>
              </div>
              <div class="application-time">
                <span class="time-label">申请时间:</span>
                <span class="time-value">{{ formatDateTime(app.createdAt) }}</span>
              </div>
            </div>
            
            <div class="application-content">
              <div class="applicant-info">
                <div class="info-item">
                  <span class="label">申请人:</span>
                  <span class="value">{{ app.applicantName }}</span>
                </div>
                <div class="info-item">
                  <span class="label">提供的线索:</span>
                  <span class="value">{{ app.offeredLeadTitles }}</span>
                </div>
                <div class="info-item">
                  <span class="label">申请理由:</span>
                  <span class="value reason">{{ app.exchangeReason }}</span>
                </div>
                <div class="info-item" v-if="app.responseMessage">
                  <span class="label">我的回复:</span>
                  <span class="value response">{{ app.responseMessage }}</span>
                </div>
                <div class="info-item">
                  <span class="label">过期时间:</span>
                  <span class="value expires" :class="{ warning: isExpiringSoon(app.expiresAt) }">
                    {{ formatDateTime(app.expiresAt) }}
                  </span>
                </div>
              </div>
            </div>
            
            <div class="application-actions">
              <el-button 
                size="small" 
                @click="viewApplicationDetail(app)"
              >
                查看详情
              </el-button>
              <template v-if="app.status === 'PENDING'">
                <el-button 
                  size="small" 
                  type="success" 
                  @click="openReviewDialog(app, 'approve')"
                >
                  同意
                </el-button>
                <el-button 
                  size="small" 
                  type="danger" 
                  @click="openReviewDialog(app, 'reject')"
                >
                  拒绝
                </el-button>
              </template>
              <el-button 
                v-if="app.status === 'APPROVED'"
                size="small" 
                type="primary" 
                @click="contactApplicant(app)"
              >
                联系申请人
              </el-button>
            </div>
          </div>
          
          <!-- 空状态 -->
          <div v-if="!receivedLoading && receivedApplications.length === 0" class="empty-state">
            <el-empty description="暂无收到的申请" />
          </div>
        </div>
        
        <!-- 分页 -->
        <div class="pagination" v-if="receivedTotal > 0">
          <el-pagination
            v-model:current-page="receivedCurrentPage"
            v-model:page-size="receivedPageSize"
            :page-sizes="[10, 20, 50]"
            :total="receivedTotal"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleReceivedSizeChange"
            @current-change="handleReceivedCurrentChange"
          />
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 申请详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="申请详情"
      width="600px"
    >
      <div class="application-detail" v-if="selectedApplication">
        <div class="detail-section">
          <h4>基本信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="label">申请时间:</span>
              <span class="value">{{ formatDateTime(selectedApplication.createdAt) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">申请状态:</span>
              <el-tag :type="getStatusType(selectedApplication.status)" size="small">
                {{ getStatusText(selectedApplication.status) }}
              </el-tag>
            </div>
            <div class="detail-item">
              <span class="label">申请人:</span>
              <span class="value">{{ selectedApplication.applicantName }}</span>
            </div>
            <div class="detail-item">
              <span class="label">目标线索所有者:</span>
              <span class="value">{{ selectedApplication.targetLeadOwnerName }}</span>
            </div>
          </div>
        </div>
        
        <div class="detail-section">
          <h4>线索信息</h4>
          <div class="leads-info">
            <div class="lead-item">
              <span class="lead-label">目标线索:</span>
              <span class="lead-value">{{ selectedApplication.targetLeadTitle }}</span>
            </div>
            <div class="lead-item">
              <span class="lead-label">提供线索:</span>
              <span class="lead-value">{{ selectedApplication.offeredLeadTitles }}</span>
            </div>
          </div>
        </div>
        
        <div class="detail-section">
          <h4>申请理由</h4>
          <div class="reason-content">
            <p>{{ selectedApplication.exchangeReason }}</p>
          </div>
        </div>
        
        <div class="detail-section" v-if="selectedApplication.responseMessage">
          <h4>回复消息</h4>
          <div class="response-content">
            <p>{{ selectedApplication.responseMessage }}</p>
          </div>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog
      v-model="reviewDialogVisible"
      :title="reviewAction === 'approve' ? '同意交换申请' : '拒绝交换申请'"
      width="500px"
    >
      <div class="review-form" v-if="reviewingApplication">
        <div class="review-info">
          <p><strong>申请人:</strong> {{ reviewingApplication.applicantName }}</p>
          <p><strong>目标线索:</strong> {{ reviewingApplication.targetLeadTitle }}</p>
          <p><strong>提供线索:</strong> {{ reviewingApplication.offeredLeadTitles }}</p>
        </div>
        
        <div class="review-message">
          <h4>回复消息 <span class="required">*</span></h4>
          <el-input
            v-model="reviewForm.responseMessage"
            type="textarea"
            :rows="4"
            :placeholder="reviewAction === 'approve' ? '请输入同意的理由或后续安排...' : '请输入拒绝的理由...'"
            maxlength="300"
            show-word-limit
          />
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="reviewDialogVisible = false">取消</el-button>
          <el-button 
            :type="reviewAction === 'approve' ? 'success' : 'danger'"
            @click="submitReview"
            :loading="reviewing"
            :disabled="!reviewForm.responseMessage.trim()"
          >
            {{ reviewAction === 'approve' ? '同意' : '拒绝' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { exchangeApi, type ExchangeApplication, ExchangeStatus } from '@/api/exchange'

// 路由
const router = useRouter()

// 数据状态
const activeTab = ref('sent')
const sentLoading = ref(false)
const receivedLoading = ref(false)
const reviewing = ref(false)

// 我发起的申请
const sentApplications = ref<ExchangeApplication[]>([])
const sentTotal = ref(0)
const sentCurrentPage = ref(1)
const sentPageSize = ref(10)

// 我收到的申请
const receivedApplications = ref<ExchangeApplication[]>([])
const receivedTotal = ref(0)
const receivedCurrentPage = ref(1)
const receivedPageSize = ref(10)

// 对话框
const detailDialogVisible = ref(false)
const reviewDialogVisible = ref(false)
const selectedApplication = ref<ExchangeApplication | null>(null)
const reviewingApplication = ref<ExchangeApplication | null>(null)
const reviewAction = ref<'approve' | 'reject'>('approve')

// 审核表单
const reviewForm = reactive({
  responseMessage: ''
})

// 方法
const loadSentApplications = async () => {
  try {
    sentLoading.value = true
    const params = {
      page: sentCurrentPage.value - 1,
      size: sentPageSize.value
    }
    
    const response = await exchangeApi.getMyApplications(params)
    sentApplications.value = response.data.content
    sentTotal.value = response.data.totalElements
  } catch (error) {
    ElMessage.error('加载发起的申请失败')
  } finally {
    sentLoading.value = false
  }
}

const loadReceivedApplications = async () => {
  try {
    receivedLoading.value = true
    const params = {
      page: receivedCurrentPage.value - 1,
      size: receivedPageSize.value
    }
    
    const response = await exchangeApi.getReceivedApplications(params)
    receivedApplications.value = response.data.content
    receivedTotal.value = response.data.totalElements
  } catch (error) {
    ElMessage.error('加载收到的申请失败')
  } finally {
    receivedLoading.value = false
  }
}

const handleTabChange = (tabName: string) => {
  if (tabName === 'sent') {
    loadSentApplications()
  } else {
    loadReceivedApplications()
  }
}

const handleSentSizeChange = (size: number) => {
  sentPageSize.value = size
  sentCurrentPage.value = 1
  loadSentApplications()
}

const handleSentCurrentChange = (page: number) => {
  sentCurrentPage.value = page
  loadSentApplications()
}

const handleReceivedSizeChange = (size: number) => {
  receivedPageSize.value = size
  receivedCurrentPage.value = 1
  loadReceivedApplications()
}

const handleReceivedCurrentChange = (page: number) => {
  receivedCurrentPage.value = page
  loadReceivedApplications()
}

const getStatusType = (status: ExchangeStatus) => {
  const types = {
    [ExchangeStatus.PENDING]: 'warning',
    [ExchangeStatus.APPROVED]: 'success',
    [ExchangeStatus.REJECTED]: 'danger',
    [ExchangeStatus.CANCELLED]: 'info',
    [ExchangeStatus.EXPIRED]: 'info'
  }
  return types[status] || 'info'
}

const getStatusText = (status: ExchangeStatus) => {
  const texts = {
    [ExchangeStatus.PENDING]: '待审核',
    [ExchangeStatus.APPROVED]: '已同意',
    [ExchangeStatus.REJECTED]: '已拒绝',
    [ExchangeStatus.CANCELLED]: '已取消',
    [ExchangeStatus.EXPIRED]: '已过期'
  }
  return texts[status] || '未知'
}

const isExpiringSoon = (expiresAt: string) => {
  const expireTime = new Date(expiresAt).getTime()
  const now = Date.now()
  const hoursLeft = (expireTime - now) / (1000 * 60 * 60)
  return hoursLeft <= 24 && hoursLeft > 0
}

const viewApplicationDetail = (app: ExchangeApplication) => {
  selectedApplication.value = app
  detailDialogVisible.value = true
}

const cancelApplication = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要取消这个交换申请吗？', '确认取消', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await exchangeApi.cancelExchange(id)
    ElMessage.success('申请已取消')
    loadSentApplications()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消申请失败')
    }
  }
}

const openReviewDialog = (app: ExchangeApplication, action: 'approve' | 'reject') => {
  reviewingApplication.value = app
  reviewAction.value = action
  reviewForm.responseMessage = ''
  reviewDialogVisible.value = true
}

const submitReview = async () => {
  if (!reviewingApplication.value) return
  
  try {
    reviewing.value = true
    
    const data = {
      responseMessage: reviewForm.responseMessage
    }
    
    if (reviewAction.value === 'approve') {
      await exchangeApi.approveExchange(reviewingApplication.value.id, data)
      ElMessage.success('已同意交换申请')
    } else {
      await exchangeApi.rejectExchange(reviewingApplication.value.id, data)
      ElMessage.success('已拒绝交换申请')
    }
    
    reviewDialogVisible.value = false
    loadReceivedApplications()
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    reviewing.value = false
  }
}

const contactOwner = (app: ExchangeApplication) => {
  ElMessageBox.alert(
    `您可以通过以下方式联系 ${app.targetLeadOwnerName}:\n\n1. 站内消息\n2. 系统通知\n3. 客服协助`,
    '联系线索所有者',
    {
      confirmButtonText: '确定',
      type: 'info'
    }
  )
}

const contactApplicant = (app: ExchangeApplication) => {
  ElMessageBox.alert(
    `您可以通过以下方式联系 ${app.applicantName}:\n\n1. 站内消息\n2. 系统通知\n3. 客服协助`,
    '联系申请人',
    {
      confirmButtonText: '确定',
      type: 'info'
    }
  )
}

const goToMarket = () => {
  router.push('/exchange/market')
}

const formatDateTime = (dateStr: string) => {
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadSentApplications()
})
</script>

<style scoped>
.exchange-applications {
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

.applications-list {
  display: grid;
  gap: 16px;
  margin-bottom: 20px;
}

.application-item {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 20px;
  transition: box-shadow 0.3s;
}

.application-item:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.application-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.application-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.application-info h3 {
  margin: 0;
  color: #303133;
  font-size: 18px;
}

.application-time {
  text-align: right;
}

.time-label {
  color: #909399;
  font-size: 14px;
}

.time-value {
  color: #606266;
  font-weight: bold;
  margin-left: 4px;
}

.application-content {
  margin-bottom: 16px;
}

.target-info,
.applicant-info {
  display: grid;
  gap: 8px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.info-item .label {
  color: #909399;
  font-size: 14px;
  min-width: 100px;
  flex-shrink: 0;
}

.info-item .value {
  color: #606266;
  font-size: 14px;
  flex: 1;
}

.info-item .value.reason {
  line-height: 1.5;
}

.info-item .value.response {
  color: #409eff;
  font-style: italic;
}

.info-item .value.expires.warning {
  color: #e6a23c;
  font-weight: bold;
}

.application-actions {
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
.application-detail {
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
  align-items: center;
}

.detail-item .label {
  color: #909399;
  font-size: 14px;
}

.detail-item .value {
  color: #303133;
  font-weight: bold;
}

.leads-info {
  display: grid;
  gap: 8px;
}

.lead-item {
  display: flex;
  gap: 8px;
}

.lead-label {
  color: #909399;
  font-size: 14px;
  min-width: 80px;
}

.lead-value {
  color: #606266;
  font-size: 14px;
  flex: 1;
}

.reason-content,
.response-content {
  background: #f5f7fa;
  padding: 12px;
  border-radius: 6px;
}

.reason-content p,
.response-content p {
  margin: 0;
  color: #606266;
  line-height: 1.5;
}

.review-form {
  max-height: 50vh;
  overflow-y: auto;
}

.review-info {
  background: #f5f7fa;
  padding: 16px;
  border-radius: 6px;
  margin-bottom: 16px;
}

.review-info p {
  margin: 4px 0;
  color: #606266;
}

.review-message h4 {
  margin: 0 0 8px 0;
  color: #303133;
}

.required {
  color: #f56c6c;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>