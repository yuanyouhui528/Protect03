<template>
  <div class="lead-list">
    <div class="page-header">
      <h2>线索列表</h2>
      <p class="page-description">管理和查看所有线索信息</p>
    </div>
    
    <!-- 搜索和筛选区域 -->
    <el-card class="search-card">
      <div class="search-form">
        <el-row :gutter="16">
          <el-col :span="6">
            <el-input
              v-model="searchForm.keyword"
              placeholder="搜索企业名称、行业、地区"
              clearable
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </el-col>
          
          <el-col :span="4">
            <el-select v-model="searchForm.status" placeholder="线索状态" clearable>
              <el-option label="全部状态" value="" />
              <el-option label="草稿" value="DRAFT" />
              <el-option label="待审核" value="PENDING_AUDIT" />
              <el-option label="已发布" value="PUBLISHED" />
              <el-option label="交换中" value="EXCHANGING" />
              <el-option label="已完成" value="COMPLETED" />
              <el-option label="已下架" value="OFFLINE" />
              <el-option label="已拒绝" value="REJECTED" />
            </el-select>
          </el-col>
          
          <el-col :span="4">
            <el-select v-model="searchForm.industryDirection" placeholder="行业方向" clearable>
              <el-option label="全部行业" value="" />
              <el-option label="制造业" value="MANUFACTURING" />
              <el-option label="信息技术" value="IT" />
              <el-option label="金融服务" value="FINANCE" />
              <el-option label="房地产" value="REAL_ESTATE" />
              <el-option label="批发零售" value="RETAIL" />
              <el-option label="交通运输" value="TRANSPORTATION" />
              <el-option label="住宿餐饮" value="HOSPITALITY" />
              <el-option label="文化娱乐" value="ENTERTAINMENT" />
              <el-option label="教育培训" value="EDUCATION" />
              <el-option label="医疗健康" value="HEALTHCARE" />
              <el-option label="其他" value="OTHER" />
            </el-select>
          </el-col>
          
          <el-col :span="4">
            <el-select v-model="searchForm.investmentRange" placeholder="投资金额" clearable>
              <el-option label="全部金额" value="" />
              <el-option label="100万以下" value="0-100" />
              <el-option label="100-500万" value="100-500" />
              <el-option label="500-1000万" value="500-1000" />
              <el-option label="1000-5000万" value="1000-5000" />
              <el-option label="5000万以上" value="5000-" />
            </el-select>
          </el-col>
          
          <el-col :span="6">
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="handleReset">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
            <el-button type="success" @click="$router.push('/leads/create')">
              <el-icon><Plus /></el-icon>
              发布线索
            </el-button>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <!-- 工具栏 -->
    <el-card class="toolbar-card">
      <div class="toolbar">
        <div class="toolbar-left">
          <span class="result-count">共找到 {{ pagination.total }} 条线索</span>
          
          <!-- 批量操作 -->
          <el-dropdown v-if="selectedLeads.length > 0" @command="handleBatchAction">
            <el-button type="primary" size="small">
              批量操作({{ selectedLeads.length }})
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="favorite">批量收藏</el-dropdown-item>
                <el-dropdown-item command="unfavorite">取消收藏</el-dropdown-item>
                <el-dropdown-item command="offline" divided>批量下架</el-dropdown-item>
                <el-dropdown-item command="delete">批量删除</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
        
        <div class="toolbar-right">
          <!-- 排序 -->
          <el-select v-model="searchForm.sortBy" placeholder="排序方式" size="small" style="width: 150px; margin-right: 12px;">
            <el-option label="创建时间" value="createdTime" />
            <el-option label="更新时间" value="updatedTime" />
            <el-option label="投资金额" value="investmentAmount" />
            <el-option label="浏览次数" value="viewCount" />
            <el-option label="收藏次数" value="favoriteCount" />
          </el-select>
          
          <el-radio-group v-model="searchForm.sortOrder" size="small" style="margin-right: 12px;">
            <el-radio-button label="desc">降序</el-radio-button>
            <el-radio-button label="asc">升序</el-radio-button>
          </el-radio-group>
          
          <!-- 视图切换 -->
          <el-radio-group v-model="viewMode" size="small">
            <el-radio-button label="list">
              <el-icon><List /></el-icon>
              列表
            </el-radio-button>
            <el-radio-button label="card">
              <el-icon><Grid /></el-icon>
              卡片
            </el-radio-button>
          </el-radio-group>
        </div>
      </div>
    </el-card>

    <!-- 线索列表内容 -->
    <el-card class="content-card">
      <!-- 列表视图 -->
      <div v-if="viewMode === 'list'" class="list-view">
        <el-table
          v-loading="loading"
          :data="leadList"
          @selection-change="handleSelectionChange"
          stripe
          style="width: 100%"
        >
          <el-table-column type="selection" width="55" />
          
          <el-table-column prop="companyName" label="企业名称" min-width="200">
            <template #default="{ row }">
              <div class="company-info">
                <div class="company-name" @click="viewLeadDetail(row.id)">{{ row.companyName }}</div>
                <div class="company-type">{{ getCompanyTypeLabel(row.companyType) }}</div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="industryDirection" label="行业方向" width="120">
            <template #default="{ row }">
              <el-tag size="small">{{ getIndustryLabel(row.industryDirection) }}</el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="investmentAmount" label="投资金额" width="120" sortable>
            <template #default="{ row }">
              <span class="amount">{{ formatAmount(row.investmentAmount) }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="location" label="所在地区" width="150">
            <template #default="{ row }">
              <span>{{ row.location }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusTagType(row.status)" size="small">
                {{ getStatusLabel(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="createdTime" label="发布时间" width="180" sortable>
            <template #default="{ row }">
              <span>{{ formatDateTime(row.createdTime) }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="viewCount" label="浏览" width="80" sortable>
            <template #default="{ row }">
              <span class="count">{{ row.viewCount || 0 }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="favoriteCount" label="收藏" width="80" sortable>
            <template #default="{ row }">
              <span class="count">{{ row.favoriteCount || 0 }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="viewLeadDetail(row.id)">
                查看
              </el-button>
              <el-button 
                :type="row.isFavorited ? 'warning' : 'success'" 
                size="small" 
                @click="toggleFavorite(row)"
              >
                {{ row.isFavorited ? '取消收藏' : '收藏' }}
              </el-button>
              <el-dropdown @command="(command) => handleRowAction(command, row)">
                <el-button size="small">
                  更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="edit">编辑</el-dropdown-item>
                    <el-dropdown-item command="offline">下架</el-dropdown-item>
                    <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 卡片视图 -->
      <div v-else class="card-view">
        <el-row :gutter="16" v-loading="loading">
          <el-col :span="8" v-for="lead in leadList" :key="lead.id" class="card-col">
            <div class="lead-card" :class="{ 'selected': selectedLeads.includes(lead.id) }">
              <!-- 卡片头部 -->
              <div class="card-header">
                <el-checkbox 
                  :model-value="selectedLeads.includes(lead.id)"
                  @change="(checked) => handleCardSelection(checked, lead.id)"
                  class="card-checkbox"
                />
                <el-tag :type="getStatusTagType(lead.status)" size="small">
                  {{ getStatusLabel(lead.status) }}
                </el-tag>
              </div>
              
              <!-- 企业信息 -->
              <div class="card-content" @click="viewLeadDetail(lead.id)">
                <h3 class="company-name">{{ lead.companyName }}</h3>
                <p class="company-type">{{ getCompanyTypeLabel(lead.companyType) }}</p>
                
                <div class="lead-info">
                  <div class="info-item">
                    <span class="label">行业：</span>
                    <span class="value">{{ getIndustryLabel(lead.industryDirection) }}</span>
                  </div>
                  <div class="info-item">
                    <span class="label">投资：</span>
                    <span class="value amount">{{ formatAmount(lead.investmentAmount) }}</span>
                  </div>
                  <div class="info-item">
                    <span class="label">地区：</span>
                    <span class="value">{{ lead.location }}</span>
                  </div>
                </div>
                
                <div class="description">
                  {{ truncateText(lead.description, 100) }}
                </div>
              </div>
              
              <!-- 卡片底部 -->
              <div class="card-footer">
                <div class="stats">
                  <span class="stat-item">
                    <el-icon><View /></el-icon>
                    {{ lead.viewCount || 0 }}
                  </span>
                  <span class="stat-item">
                    <el-icon><Star /></el-icon>
                    {{ lead.favoriteCount || 0 }}
                  </span>
                  <span class="time">{{ formatDateTime(lead.createdTime) }}</span>
                </div>
                
                <div class="actions">
                  <el-button 
                    :type="lead.isFavorited ? 'warning' : 'success'" 
                    size="small" 
                    @click.stop="toggleFavorite(lead)"
                  >
                    <el-icon><Star /></el-icon>
                  </el-button>
                  <el-button type="primary" size="small" @click.stop="viewLeadDetail(lead.id)">
                    <el-icon><View /></el-icon>
                  </el-button>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 空状态 -->
      <div v-if="!loading && leadList.length === 0" class="empty-state">
        <el-empty description="暂无线索数据">
          <el-button type="primary" @click="$router.push('/leads/create')">
            发布第一条线索
          </el-button>
        </el-empty>
      </div>
    </el-card>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Plus,
  ArrowDown,
  List,
  Grid,
  View,
  Star
} from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const viewMode = ref('list') // 'list' | 'card'
const leadList = ref([])
const selectedLeads = ref<number[]>([])

// 搜索表单
const searchForm = reactive({
  keyword: '',
  status: '',
  industryDirection: '',
  investmentRange: '',
  sortBy: 'createdTime',
  sortOrder: 'desc'
})

// 分页信息
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 获取线索列表
const getLeadList = async () => {
  loading.value = true
  
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword,
      status: searchForm.status,
      industryDirection: searchForm.industryDirection,
      investmentRange: searchForm.investmentRange,
      sortBy: searchForm.sortBy,
      sortOrder: searchForm.sortOrder
    }
    
    // 过滤空值参数
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null || params[key] === undefined) {
        delete params[key]
      }
    })
    
    const response = await axios.get('/api/leads', { params })
    
    if (response.data.success) {
      leadList.value = response.data.data.records || []
      pagination.total = response.data.data.total || 0
    } else {
      ElMessage.error(response.data.message || '获取线索列表失败')
    }
  } catch (error: any) {
    console.error('获取线索列表失败:', error)
    ElMessage.error('获取线索列表失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  getLeadList()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    status: '',
    industryDirection: '',
    investmentRange: '',
    sortBy: 'createdTime',
    sortOrder: 'desc'
  })
  pagination.page = 1
  getLeadList()
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.page = 1
  getLeadList()
}

// 当前页改变
const handleCurrentChange = (page: number) => {
  pagination.page = page
  getLeadList()
}

// 表格选择改变
const handleSelectionChange = (selection: any[]) => {
  selectedLeads.value = selection.map(item => item.id)
}

// 卡片选择
const handleCardSelection = (checked: boolean, leadId: number) => {
  if (checked) {
    if (!selectedLeads.value.includes(leadId)) {
      selectedLeads.value.push(leadId)
    }
  } else {
    const index = selectedLeads.value.indexOf(leadId)
    if (index > -1) {
      selectedLeads.value.splice(index, 1)
    }
  }
}

// 批量操作
const handleBatchAction = async (command: string) => {
  if (selectedLeads.value.length === 0) {
    ElMessage.warning('请先选择要操作的线索')
    return
  }
  
  try {
    let confirmText = ''
    let apiUrl = ''
    
    switch (command) {
      case 'favorite':
        confirmText = `确定要批量收藏 ${selectedLeads.value.length} 条线索吗？`
        apiUrl = '/api/leads/batch/favorite'
        break
      case 'unfavorite':
        confirmText = `确定要取消收藏 ${selectedLeads.value.length} 条线索吗？`
        apiUrl = '/api/leads/batch/unfavorite'
        break
      case 'offline':
        confirmText = `确定要批量下架 ${selectedLeads.value.length} 条线索吗？`
        apiUrl = '/api/leads/batch/offline'
        break
      case 'delete':
        confirmText = `确定要批量删除 ${selectedLeads.value.length} 条线索吗？此操作不可恢复！`
        apiUrl = '/api/leads/batch/delete'
        break
    }
    
    await ElMessageBox.confirm(confirmText, '确认操作', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await axios.post(apiUrl, {
      leadIds: selectedLeads.value
    })
    
    if (response.data.success) {
      ElMessage.success('批量操作成功')
      selectedLeads.value = []
      getLeadList()
    } else {
      ElMessage.error(response.data.message || '批量操作失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('批量操作失败:', error)
      ElMessage.error('批量操作失败，请重试')
    }
  }
}

// 行操作
const handleRowAction = async (command: string, row: any) => {
  switch (command) {
    case 'edit':
      router.push(`/leads/edit/${row.id}`)
      break
    case 'offline':
      await offlineLead(row.id)
      break
    case 'delete':
      await deleteLead(row.id)
      break
  }
}

// 查看线索详情
const viewLeadDetail = (id: number) => {
  router.push(`/leads/detail/${id}`)
}

// 切换收藏状态
const toggleFavorite = async (lead: any) => {
  try {
    const apiUrl = lead.isFavorited ? '/api/leads/unfavorite' : '/api/leads/favorite'
    const response = await axios.post(apiUrl, { leadId: lead.id })
    
    if (response.data.success) {
      lead.isFavorited = !lead.isFavorited
      lead.favoriteCount += lead.isFavorited ? 1 : -1
      ElMessage.success(lead.isFavorited ? '收藏成功' : '取消收藏成功')
    } else {
      ElMessage.error(response.data.message || '操作失败')
    }
  } catch (error: any) {
    console.error('切换收藏状态失败:', error)
    ElMessage.error('操作失败，请重试')
  }
}

// 下架线索
const offlineLead = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要下架此线索吗？', '确认下架', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await axios.post(`/api/leads/${id}/offline`)
    
    if (response.data.success) {
      ElMessage.success('线索下架成功')
      getLeadList()
    } else {
      ElMessage.error(response.data.message || '下架失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('下架线索失败:', error)
      ElMessage.error('下架失败，请重试')
    }
  }
}

// 删除线索
const deleteLead = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除此线索吗？此操作不可恢复！', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await axios.delete(`/api/leads/${id}`)
    
    if (response.data.success) {
      ElMessage.success('线索删除成功')
      getLeadList()
    } else {
      ElMessage.error(response.data.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除线索失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  }
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

const formatAmount = (amount: number) => {
  if (!amount) return '面议'
  return `${amount}万元`
}

const formatDateTime = (dateTime: string) => {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString('zh-CN')
}

const truncateText = (text: string, maxLength: number) => {
  if (!text) return ''
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
}

// 监听搜索条件变化
watch(
  () => [searchForm.sortBy, searchForm.sortOrder],
  () => {
    pagination.page = 1
    getLeadList()
  }
)

// 组件挂载时获取数据
onMounted(() => {
  getLeadList()
})
</script>

<style scoped>
.lead-list {
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

/* 搜索卡片样式 */
.search-card {
  margin-bottom: 16px;
}

.search-form {
  padding: 8px 0;
}

/* 工具栏样式 */
.toolbar-card {
  margin-bottom: 16px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.toolbar-right {
  display: flex;
  align-items: center;
}

.result-count {
  color: #606266;
  font-size: 14px;
}

/* 内容卡片样式 */
.content-card {
  margin-bottom: 16px;
  min-height: 400px;
}

/* 列表视图样式 */
.list-view {
  padding: 0;
}

.company-info {
  cursor: pointer;
}

.company-name {
  font-weight: 600;
  color: #409eff;
  margin-bottom: 4px;
}

.company-name:hover {
  text-decoration: underline;
}

.company-type {
  font-size: 12px;
  color: #909399;
}

.amount {
  font-weight: 600;
  color: #f56c6c;
}

.count {
  font-weight: 600;
  color: #909399;
}

/* 卡片视图样式 */
.card-view {
  padding: 0;
}

.card-col {
  margin-bottom: 16px;
}

.lead-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.3s;
  cursor: pointer;
  height: 280px;
  display: flex;
  flex-direction: column;
}

.lead-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
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

.card-checkbox {
  margin-right: 8px;
}

.card-content {
  flex: 1;
  overflow: hidden;
}

.card-content .company-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 4px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-content .company-type {
  font-size: 12px;
  color: #909399;
  margin-bottom: 12px;
}

.lead-info {
  margin-bottom: 12px;
}

.info-item {
  display: flex;
  margin-bottom: 4px;
  font-size: 13px;
}

.info-item .label {
  color: #909399;
  width: 40px;
  flex-shrink: 0;
}

.info-item .value {
  color: #303133;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.info-item .value.amount {
  color: #f56c6c;
  font-weight: 600;
}

.description {
  font-size: 12px;
  color: #606266;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.stats {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #909399;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 2px;
}

.time {
  color: #c0c4cc;
}

.actions {
  display: flex;
  gap: 8px;
}

/* 空状态样式 */
.empty-state {
  padding: 60px 20px;
  text-align: center;
}

/* 分页样式 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 24px 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .card-col {
    span: 12;
  }
}

@media (max-width: 768px) {
  .toolbar {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .toolbar-left,
  .toolbar-right {
    justify-content: center;
  }
  
  .search-form .el-col {
    margin-bottom: 12px;
  }
  
  .card-col {
    span: 24;
  }
  
  .lead-card {
    height: auto;
    min-height: 240px;
  }
}

/* Element Plus 样式覆盖 */
:deep(.el-table .el-table__cell) {
  padding: 12px 0;
}

:deep(.el-table .company-name) {
  cursor: pointer;
  color: #409eff;
}

:deep(.el-table .company-name:hover) {
  text-decoration: underline;
}

:deep(.el-pagination) {
  justify-content: center;
}

:deep(.el-card__body) {
  padding: 20px;
}
</style>