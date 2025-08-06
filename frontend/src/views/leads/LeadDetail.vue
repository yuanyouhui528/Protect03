<template>
  <div class="lead-detail">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="8" animated />
    </div>
    
    <!-- 线索详情内容 -->
    <div v-else-if="leadDetail" class="detail-content">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-left">
          <el-button @click="$router.back()" circle>
            <el-icon><ArrowLeft /></el-icon>
          </el-button>
          <div class="title-info">
            <h2>{{ leadDetail.companyName }}</h2>
            <div class="meta-info">
              <el-tag :type="getStatusTagType(leadDetail.status)" size="small">
                {{ getStatusLabel(leadDetail.status) }}
              </el-tag>
              <span class="publish-time">发布于 {{ formatDateTime(leadDetail.createdTime) }}</span>
              <span class="view-count">
                <el-icon><View /></el-icon>
                {{ leadDetail.viewCount || 0 }} 次浏览
              </span>
            </div>
          </div>
        </div>
        
        <div class="header-actions">
          <el-button 
            :type="leadDetail.isFavorited ? 'warning' : 'success'" 
            @click="toggleFavorite"
            :loading="favoriteLoading"
          >
            <el-icon><Star /></el-icon>
            {{ leadDetail.isFavorited ? '取消收藏' : '收藏线索' }}
          </el-button>
          
          <el-button type="primary" @click="handleContact">
            <el-icon><Phone /></el-icon>
            联系对接
          </el-button>
          
          <el-dropdown @command="handleMoreAction">
            <el-button>
              更多操作
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="share">
                  <el-icon><Share /></el-icon>
                  分享线索
                </el-dropdown-item>
                <el-dropdown-item command="report">
                  <el-icon><Warning /></el-icon>
                  举报线索
                </el-dropdown-item>
                <el-dropdown-item command="print" divided>
                  <el-icon><Printer /></el-icon>
                  打印详情
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 主要内容区域 -->
      <el-row :gutter="24">
        <!-- 左侧主要信息 -->
        <el-col :span="16">
          <!-- 企业基本信息 -->
          <el-card class="info-card">
            <template #header>
              <div class="card-header">
                <h3>
                  <el-icon><OfficeBuilding /></el-icon>
                  企业基本信息
                </h3>
              </div>
            </template>
            
            <el-descriptions :column="2" border>
              <el-descriptions-item label="企业名称" :span="2">
                <span class="company-name">{{ leadDetail.companyName }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="企业类型">
                {{ getCompanyTypeLabel(leadDetail.companyType) }}
              </el-descriptions-item>
              <el-descriptions-item label="行业方向">
                <el-tag>{{ getIndustryLabel(leadDetail.industryDirection) }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="成立时间">
                {{ formatDate(leadDetail.establishedDate) }}
              </el-descriptions-item>
              <el-descriptions-item label="注册资本">
                {{ formatAmount(leadDetail.registeredCapital) }}
              </el-descriptions-item>
              <el-descriptions-item label="员工规模">
                {{ getEmployeeSizeLabel(leadDetail.employeeSize) }}
              </el-descriptions-item>
              <el-descriptions-item label="年营业额">
                {{ formatAmount(leadDetail.annualRevenue) }}
              </el-descriptions-item>
              <el-descriptions-item label="所在地区" :span="2">
                {{ leadDetail.location }}
              </el-descriptions-item>
              <el-descriptions-item label="详细地址" :span="2">
                {{ leadDetail.address }}
              </el-descriptions-item>
            </el-descriptions>
          </el-card>

          <!-- 投资需求信息 -->
          <el-card class="info-card">
            <template #header>
              <div class="card-header">
                <h3>
                  <el-icon><Money /></el-icon>
                  投资需求信息
                </h3>
              </div>
            </template>
            
            <el-descriptions :column="2" border>
              <el-descriptions-item label="投资金额">
                <span class="amount">{{ formatAmount(leadDetail.investmentAmount) }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="投资方式">
                {{ getInvestmentTypeLabel(leadDetail.investmentType) }}
              </el-descriptions-item>
              <el-descriptions-item label="资金用途" :span="2">
                {{ leadDetail.fundingPurpose }}
              </el-descriptions-item>
              <el-descriptions-item label="预期回报">
                {{ leadDetail.expectedReturn }}%
              </el-descriptions-item>
              <el-descriptions-item label="投资期限">
                {{ leadDetail.investmentPeriod }}年
              </el-descriptions-item>
              <el-descriptions-item label="风险等级">
                <el-rate v-model="leadDetail.riskLevel" disabled show-score />
              </el-descriptions-item>
              <el-descriptions-item label="退出方式">
                {{ getExitStrategyLabel(leadDetail.exitStrategy) }}
              </el-descriptions-item>
            </el-descriptions>
          </el-card>

          <!-- 项目描述 -->
          <el-card class="info-card">
            <template #header>
              <div class="card-header">
                <h3>
                  <el-icon><Document /></el-icon>
                  项目详细描述
                </h3>
              </div>
            </template>
            
            <div class="description-content">
              <p>{{ leadDetail.description }}</p>
            </div>
          </el-card>

          <!-- 附件文件 -->
          <el-card v-if="leadDetail.attachments && leadDetail.attachments.length > 0" class="info-card">
            <template #header>
              <div class="card-header">
                <h3>
                  <el-icon><Paperclip /></el-icon>
                  相关附件
                </h3>
              </div>
            </template>
            
            <div class="attachments">
              <div 
                v-for="(file, index) in leadDetail.attachments" 
                :key="index" 
                class="attachment-item"
                @click="downloadFile(file)"
              >
                <el-icon class="file-icon"><Document /></el-icon>
                <span class="file-name">{{ file.name }}</span>
                <span class="file-size">{{ formatFileSize(file.size) }}</span>
                <el-icon class="download-icon"><Download /></el-icon>
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- 右侧边栏 -->
        <el-col :span="8">
          <!-- 联系信息 -->
          <el-card class="sidebar-card">
            <template #header>
              <div class="card-header">
                <h3>
                  <el-icon><User /></el-icon>
                  联系信息
                </h3>
              </div>
            </template>
            
            <div class="contact-info">
              <div class="contact-item">
                <label>联系人：</label>
                <span>{{ leadDetail.contactPerson }}</span>
              </div>
              <div class="contact-item">
                <label>联系电话：</label>
                <span class="phone">{{ maskPhone(leadDetail.contactPhone) }}</span>
                <el-button 
                  type="text" 
                  size="small" 
                  @click="showFullContact"
                  v-if="!contactVisible"
                >
                  查看完整联系方式
                </el-button>
              </div>
              <div class="contact-item" v-if="contactVisible">
                <label>电子邮箱：</label>
                <span>{{ leadDetail.contactEmail }}</span>
              </div>
              <div class="contact-item" v-if="contactVisible && leadDetail.wechatId">
                <label>微信号：</label>
                <span>{{ leadDetail.wechatId }}</span>
              </div>
              <div class="contact-item" v-if="contactVisible && leadDetail.qqId">
                <label>QQ号：</label>
                <span>{{ leadDetail.qqId }}</span>
              </div>
              <div class="contact-item">
                <label>最佳联系时间：</label>
                <span>{{ getBestContactTimeText(leadDetail.bestContactTime) }}</span>
              </div>
            </div>
            
            <div class="contact-actions">
              <el-button type="primary" block @click="handleContact">
                <el-icon><Phone /></el-icon>
                立即联系
              </el-button>
            </div>
          </el-card>

          <!-- 统计信息 -->
          <el-card class="sidebar-card">
            <template #header>
              <div class="card-header">
                <h3>
                  <el-icon><DataAnalysis /></el-icon>
                  统计信息
                </h3>
              </div>
            </template>
            
            <div class="stats-info">
              <div class="stat-item">
                <div class="stat-value">{{ leadDetail.viewCount || 0 }}</div>
                <div class="stat-label">浏览次数</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ leadDetail.favoriteCount || 0 }}</div>
                <div class="stat-label">收藏次数</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ leadDetail.contactCount || 0 }}</div>
                <div class="stat-label">联系次数</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ calculateDaysOnline() }}</div>
                <div class="stat-label">在线天数</div>
              </div>
            </div>
          </el-card>

          <!-- 相似线索推荐 -->
          <el-card class="sidebar-card">
            <template #header>
              <div class="card-header">
                <h3>
                  <el-icon><Connection /></el-icon>
                  相似线索推荐
                </h3>
              </div>
            </template>
            
            <div v-loading="similarLoading" class="similar-leads">
              <div 
                v-for="lead in similarLeads" 
                :key="lead.id" 
                class="similar-item"
                @click="$router.push(`/leads/detail/${lead.id}`)"
              >
                <div class="similar-title">{{ lead.companyName }}</div>
                <div class="similar-info">
                  <span class="industry">{{ getIndustryLabel(lead.industryDirection) }}</span>
                  <span class="amount">{{ formatAmount(lead.investmentAmount) }}</span>
                </div>
                <div class="similar-time">{{ formatDateTime(lead.createdTime) }}</div>
              </div>
              
              <div v-if="similarLeads.length === 0" class="no-similar">
                暂无相似线索
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 错误状态 -->
    <div v-else class="error-state">
      <el-result
        icon="error"
        title="线索不存在"
        sub-title="您访问的线索可能已被删除或不存在"
      >
        <template #extra>
          <el-button type="primary" @click="$router.push('/leads/list')">
            返回线索列表
          </el-button>
        </template>
      </el-result>
    </div>

    <!-- 联系对话框 -->
    <el-dialog
      v-model="contactDialogVisible"
      title="联系线索发布方"
      width="500px"
      :before-close="handleContactDialogClose"
    >
      <el-form :model="contactForm" :rules="contactRules" ref="contactFormRef" label-width="100px">
        <el-form-item label="联系方式" prop="contactMethod">
          <el-radio-group v-model="contactForm.contactMethod">
            <el-radio label="phone">电话联系</el-radio>
            <el-radio label="email">邮件联系</el-radio>
            <el-radio label="wechat">微信联系</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="联系内容" prop="message">
          <el-input
            v-model="contactForm.message"
            type="textarea"
            :rows="4"
            placeholder="请输入您的联系内容和合作意向"
          />
        </el-form-item>
        
        <el-form-item label="您的联系方式" prop="yourContact">
          <el-input
            v-model="contactForm.yourContact"
            placeholder="请输入您的联系方式"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="contactDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitContact" :loading="contactSubmitting">
            发送联系请求
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft,
  View,
  Star,
  Phone,
  ArrowDown,
  Share,
  Warning,
  Printer,
  OfficeBuilding,
  Money,
  Document,
  Paperclip,
  Download,
  User,
  DataAnalysis,
  Connection
} from '@element-plus/icons-vue'
import axios from 'axios'

const route = useRoute()
const router = useRouter()

// 响应式数据
const loading = ref(true)
const favoriteLoading = ref(false)
const similarLoading = ref(false)
const contactVisible = ref(false)
const contactDialogVisible = ref(false)
const contactSubmitting = ref(false)
const leadDetail = ref<any>(null)
const similarLeads = ref<any[]>([])
const contactFormRef = ref()

// 联系表单
const contactForm = reactive({
  contactMethod: 'phone',
  message: '',
  yourContact: ''
})

// 表单验证规则
const contactRules = {
  contactMethod: [
    { required: true, message: '请选择联系方式', trigger: 'change' }
  ],
  message: [
    { required: true, message: '请输入联系内容', trigger: 'blur' },
    { min: 10, message: '联系内容至少10个字符', trigger: 'blur' }
  ],
  yourContact: [
    { required: true, message: '请输入您的联系方式', trigger: 'blur' }
  ]
}

// 获取线索详情
const getLeadDetail = async () => {
  loading.value = true
  
  try {
    const leadId = route.params.id
    const response = await axios.get(`/api/leads/${leadId}`)
    
    if (response.data.success) {
      leadDetail.value = response.data.data
      // 获取相似线索推荐
      getSimilarLeads()
    } else {
      ElMessage.error(response.data.message || '获取线索详情失败')
      leadDetail.value = null
    }
  } catch (error: any) {
    console.error('获取线索详情失败:', error)
    ElMessage.error('获取线索详情失败，请检查网络连接')
    leadDetail.value = null
  } finally {
    loading.value = false
  }
}

// 获取相似线索
const getSimilarLeads = async () => {
  if (!leadDetail.value) return
  
  similarLoading.value = true
  
  try {
    const response = await axios.get(`/api/leads/${leadDetail.value.id}/similar`, {
      params: { limit: 5 }
    })
    
    if (response.data.success) {
      similarLeads.value = response.data.data || []
    }
  } catch (error: any) {
    console.error('获取相似线索失败:', error)
  } finally {
    similarLoading.value = false
  }
}

// 切换收藏状态
const toggleFavorite = async () => {
  if (!leadDetail.value) return
  
  favoriteLoading.value = true
  
  try {
    const apiUrl = leadDetail.value.isFavorited ? '/api/leads/unfavorite' : '/api/leads/favorite'
    const response = await axios.post(apiUrl, { leadId: leadDetail.value.id })
    
    if (response.data.success) {
      leadDetail.value.isFavorited = !leadDetail.value.isFavorited
      leadDetail.value.favoriteCount += leadDetail.value.isFavorited ? 1 : -1
      ElMessage.success(leadDetail.value.isFavorited ? '收藏成功' : '取消收藏成功')
    } else {
      ElMessage.error(response.data.message || '操作失败')
    }
  } catch (error: any) {
    console.error('切换收藏状态失败:', error)
    ElMessage.error('操作失败，请重试')
  } finally {
    favoriteLoading.value = false
  }
}

// 显示完整联系方式
const showFullContact = () => {
  contactVisible.value = true
  // 这里可以添加查看联系方式的统计逻辑
}

// 处理联系操作
const handleContact = () => {
  contactDialogVisible.value = true
}

// 提交联系请求
const submitContact = async () => {
  if (!contactFormRef.value) return
  
  try {
    await contactFormRef.value.validate()
    
    contactSubmitting.value = true
    
    const response = await axios.post('/api/leads/contact', {
      leadId: leadDetail.value.id,
      ...contactForm
    })
    
    if (response.data.success) {
      ElMessage.success('联系请求发送成功')
      contactDialogVisible.value = false
      // 重置表单
      Object.assign(contactForm, {
        contactMethod: 'phone',
        message: '',
        yourContact: ''
      })
      // 更新联系次数
      if (leadDetail.value) {
        leadDetail.value.contactCount = (leadDetail.value.contactCount || 0) + 1
      }
    } else {
      ElMessage.error(response.data.message || '发送失败')
    }
  } catch (error: any) {
    if (error !== 'validation failed') {
      console.error('发送联系请求失败:', error)
      ElMessage.error('发送失败，请重试')
    }
  } finally {
    contactSubmitting.value = false
  }
}

// 关闭联系对话框
const handleContactDialogClose = (done: () => void) => {
  if (contactSubmitting.value) {
    ElMessage.warning('正在发送请求，请稍候...')
    return
  }
  done()
}

// 处理更多操作
const handleMoreAction = (command: string) => {
  switch (command) {
    case 'share':
      handleShare()
      break
    case 'report':
      handleReport()
      break
    case 'print':
      handlePrint()
      break
  }
}

// 分享线索
const handleShare = () => {
  // 复制链接到剪贴板
  const url = window.location.href
  navigator.clipboard.writeText(url).then(() => {
    ElMessage.success('链接已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败，请手动复制链接')
  })
}

// 举报线索
const handleReport = () => {
  ElMessageBox.prompt('请输入举报原因', '举报线索', {
    confirmButtonText: '提交举报',
    cancelButtonText: '取消',
    inputPattern: /.{5,}/,
    inputErrorMessage: '举报原因至少5个字符'
  }).then(({ value }) => {
    // 提交举报
    axios.post('/api/leads/report', {
      leadId: leadDetail.value?.id,
      reason: value
    }).then(response => {
      if (response.data.success) {
        ElMessage.success('举报提交成功，我们会尽快处理')
      } else {
        ElMessage.error('举报提交失败')
      }
    })
  }).catch(() => {
    // 取消举报
  })
}

// 打印详情
const handlePrint = () => {
  window.print()
}

// 下载文件
const downloadFile = (file: any) => {
  // 创建下载链接
  const link = document.createElement('a')
  link.href = file.url
  link.download = file.name
  link.click()
}

// 工具函数
const getCompanyTypeLabel = (value: string) => {
  const types: Record<string, string> = {
    'LIMITED_LIABILITY': '有限责任公司',
    'JOINT_STOCK': '股份有限公司',
    'SOLE_PROPRIETORSHIP': '个人独资企业',
    'PARTNERSHIP': '合伙企业',
    'FOREIGN_INVESTED': '外商投资企业',
    'OTHER': '其他'
  }
  return types[value] || value
}

const getIndustryLabel = (value: string) => {
  const industries: Record<string, string> = {
    'MANUFACTURING': '制造业',
    'IT': '信息技术',
    'FINANCE': '金融服务',
    'REAL_ESTATE': '房地产',
    'RETAIL': '批发零售',
    'TRANSPORTATION': '交通运输',
    'HOSPITALITY': '住宿餐饮',
    'ENTERTAINMENT': '文化娱乐',
    'EDUCATION': '教育培训',
    'HEALTHCARE': '医疗健康',
    'OTHER': '其他'
  }
  return industries[value] || value
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

const getEmployeeSizeLabel = (value: string) => {
  const sizes: Record<string, string> = {
    'SMALL': '50人以下',
    'MEDIUM': '50-200人',
    'LARGE': '200-1000人',
    'XLARGE': '1000人以上'
  }
  return sizes[value] || value
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

const getExitStrategyLabel = (value: string) => {
  const strategies: Record<string, string> = {
    'IPO': '公开上市',
    'ACQUISITION': '并购退出',
    'BUYBACK': '回购退出',
    'TRANSFER': '股权转让'
  }
  return strategies[value] || value
}

const getBestContactTimeText = (timeSlots: string[]) => {
  if (!timeSlots || timeSlots.length === 0) return '随时'
  
  const timeLabels: Record<string, string> = {
    'MORNING': '上午',
    'AFTERNOON': '下午',
    'EVENING': '晚上',
    'WEEKEND': '周末'
  }
  
  return timeSlots.map(slot => timeLabels[slot] || slot).join('、')
}

const formatAmount = (amount: number) => {
  if (!amount) return '面议'
  return `${amount}万元`
}

const formatDate = (date: string) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

const formatDateTime = (dateTime: string) => {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString('zh-CN')
}

const formatFileSize = (size: number) => {
  if (size < 1024) return `${size}B`
  if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)}KB`
  return `${(size / (1024 * 1024)).toFixed(1)}MB`
}

const maskPhone = (phone: string) => {
  if (!phone) return ''
  if (contactVisible.value) return phone
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

const calculateDaysOnline = () => {
  if (!leadDetail.value?.createdTime) return 0
  const created = new Date(leadDetail.value.createdTime)
  const now = new Date()
  const diffTime = Math.abs(now.getTime() - created.getTime())
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24))
}

// 组件挂载时获取数据
onMounted(() => {
  getLeadDetail()
})
</script>

<style scoped>
.lead-detail {
  padding: 0;
}

.loading-container {
  padding: 24px;
}

.detail-content {
  padding: 0;
}

/* 页面头部样式 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding: 24px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.title-info h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.meta-info {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 14px;
  color: #909399;
}

.publish-time,
.view-count {
  display: flex;
  align-items: center;
  gap: 4px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 卡片样式 */
.info-card,
.sidebar-card {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 企业名称特殊样式 */
.company-name {
  font-size: 18px;
  font-weight: 600;
  color: #409eff;
}

.amount {
  font-size: 16px;
  font-weight: 600;
  color: #f56c6c;
}

/* 项目描述样式 */
.description-content {
  padding: 16px 0;
  line-height: 1.8;
  color: #606266;
  font-size: 14px;
}

/* 附件样式 */
.attachments {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.attachment-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.attachment-item:hover {
  border-color: #409eff;
  background-color: #f0f9ff;
}

.file-icon {
  color: #409eff;
  font-size: 20px;
}

.file-name {
  flex: 1;
  font-weight: 500;
  color: #303133;
}

.file-size {
  font-size: 12px;
  color: #909399;
}

.download-icon {
  color: #909399;
  font-size: 16px;
}

/* 联系信息样式 */
.contact-info {
  margin-bottom: 16px;
}

.contact-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
}

.contact-item label {
  width: 80px;
  color: #909399;
  flex-shrink: 0;
}

.contact-item span {
  color: #303133;
  flex: 1;
}

.phone {
  font-family: 'Courier New', monospace;
  font-weight: 600;
}

.contact-actions {
  margin-top: 16px;
}

/* 统计信息样式 */
.stats-info {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.stat-item {
  text-align: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 6px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

/* 相似线索样式 */
.similar-leads {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.similar-item {
  padding: 12px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.similar-item:hover {
  border-color: #409eff;
  background-color: #f0f9ff;
}

.similar-title {
  font-weight: 500;
  color: #303133;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.similar-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
  font-size: 12px;
}

.similar-info .industry {
  color: #909399;
}

.similar-info .amount {
  color: #f56c6c;
  font-weight: 600;
}

.similar-time {
  font-size: 11px;
  color: #c0c4cc;
}

.no-similar {
  text-align: center;
  color: #909399;
  font-size: 14px;
  padding: 20px;
}

/* 错误状态样式 */
.error-state {
  padding: 60px 20px;
}

/* 对话框样式 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
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
}

@media (max-width: 768px) {
  .page-header {
    padding: 16px;
  }
  
  .header-left {
    flex-direction: column;
    gap: 12px;
  }
  
  .meta-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .header-actions {
    flex-direction: column;
  }
  
  .stats-info {
    grid-template-columns: 1fr;
  }
}

/* Element Plus 样式覆盖 */
:deep(.el-descriptions__label) {
  font-weight: 500;
  color: #909399;
}

:deep(.el-descriptions__content) {
  color: #303133;
}

:deep(.el-card__header) {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

:deep(.el-card__body) {
  padding: 20px;
}

:deep(.el-rate) {
  display: flex;
  align-items: center;
}

/* 打印样式 */
@media print {
  .page-header .header-actions,
  .sidebar-card {
    display: none;
  }
  
  .lead-detail {
    padding: 0;
  }
  
  .info-card {
    break-inside: avoid;
    margin-bottom: 20px;
  }
}
</style>