<template>
  <div class="lead-create">
    <div class="page-header">
      <h2>发布线索</h2>
      <p class="page-description">发布新的招商线索信息</p>
    </div>
    
    <!-- 步骤条 -->
    <el-card class="steps-card">
      <el-steps :active="currentStep" finish-status="success" align-center>
        <el-step title="基本信息" description="填写企业基本信息" />
        <el-step title="投资需求" description="填写投资相关信息" />
        <el-step title="联系方式" description="填写联系人信息" />
        <el-step title="预览发布" description="确认信息并发布" />
      </el-steps>
    </el-card>

    <!-- 表单内容 -->
    <el-card class="form-card">
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
        label-position="left"
      >
        <!-- 第一步：基本信息 -->
        <div v-show="currentStep === 0" class="step-content">
          <h3 class="step-title">企业基本信息</h3>
          
          <el-form-item label="企业名称" prop="companyName">
            <el-input
              v-model="formData.companyName"
              placeholder="请输入企业名称"
              maxlength="100"
              show-word-limit
            />
          </el-form-item>
          
          <el-form-item label="企业类型" prop="companyType">
            <el-select v-model="formData.companyType" placeholder="请选择企业类型" style="width: 100%">
              <el-option label="有限责任公司" value="LIMITED_LIABILITY" />
              <el-option label="股份有限公司" value="JOINT_STOCK" />
              <el-option label="个人独资企业" value="SOLE_PROPRIETORSHIP" />
              <el-option label="合伙企业" value="PARTNERSHIP" />
              <el-option label="外商投资企业" value="FOREIGN_INVESTED" />
              <el-option label="其他" value="OTHER" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="注册资本" prop="registeredCapital">
            <el-input-number
              v-model="formData.registeredCapital"
              :min="0"
              :precision="2"
              placeholder="请输入注册资本"
              style="width: 100%"
            />
            <span class="unit-text">万元</span>
          </el-form-item>
          
          <el-form-item label="成立时间" prop="establishedDate">
            <el-date-picker
              v-model="formData.establishedDate"
              type="date"
              placeholder="请选择成立时间"
              style="width: 100%"
            />
          </el-form-item>
          
          <el-form-item label="所在地区" prop="location">
            <el-cascader
              v-model="formData.location"
              :options="regionOptions"
              placeholder="请选择所在地区"
              style="width: 100%"
            />
          </el-form-item>
          
          <el-form-item label="详细地址" prop="address">
            <el-input
              v-model="formData.address"
              placeholder="请输入详细地址"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>
        </div>

        <!-- 第二步：投资需求 -->
        <div v-show="currentStep === 1" class="step-content">
          <h3 class="step-title">投资需求信息</h3>
          
          <el-form-item label="行业方向" prop="industryDirection">
            <el-select v-model="formData.industryDirection" placeholder="请选择行业方向" style="width: 100%">
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
          </el-form-item>
          
          <el-form-item label="投资金额" prop="investmentAmount">
            <el-input-number
              v-model="formData.investmentAmount"
              :min="0"
              :precision="2"
              placeholder="请输入投资金额"
              style="width: 100%"
            />
            <span class="unit-text">万元</span>
          </el-form-item>
          
          <el-form-item label="投资方式" prop="investmentType">
            <el-checkbox-group v-model="formData.investmentType">
              <el-checkbox label="股权投资" value="EQUITY" />
              <el-checkbox label="债权投资" value="DEBT" />
              <el-checkbox label="合作经营" value="COOPERATION" />
              <el-checkbox label="技术入股" value="TECHNOLOGY" />
              <el-checkbox label="其他" value="OTHER" />
            </el-checkbox-group>
          </el-form-item>
          
          <el-form-item label="项目描述" prop="description">
            <el-input
              v-model="formData.description"
              type="textarea"
              :rows="4"
              placeholder="请详细描述项目情况、投资用途、预期收益等"
              maxlength="1000"
              show-word-limit
            />
          </el-form-item>
          
          <el-form-item label="特殊要求">
            <el-input
              v-model="formData.specialRequirements"
              type="textarea"
              :rows="3"
              placeholder="请描述对投资方的特殊要求（可选）"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
        </div>

        <!-- 第三步：联系方式 -->
        <div v-show="currentStep === 2" class="step-content">
          <h3 class="step-title">联系人信息</h3>
          
          <el-form-item label="联系人姓名" prop="contactPerson">
            <el-input
              v-model="formData.contactPerson"
              placeholder="请输入联系人姓名"
              maxlength="50"
            />
          </el-form-item>
          
          <el-form-item label="联系电话" prop="contactPhone">
            <el-input
              v-model="formData.contactPhone"
              placeholder="请输入联系电话"
              maxlength="20"
            />
          </el-form-item>
          
          <el-form-item label="电子邮箱" prop="contactEmail">
            <el-input
              v-model="formData.contactEmail"
              placeholder="请输入电子邮箱"
              maxlength="100"
            />
          </el-form-item>
          
          <el-form-item label="微信号">
            <el-input
              v-model="formData.wechatId"
              placeholder="请输入微信号（可选）"
              maxlength="50"
            />
          </el-form-item>
          
          <el-form-item label="QQ号">
            <el-input
              v-model="formData.qqId"
              placeholder="请输入QQ号（可选）"
              maxlength="20"
            />
          </el-form-item>
          
          <el-form-item label="最佳联系时间">
            <el-time-picker
              v-model="formData.bestContactTime"
              is-range
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              format="HH:mm"
              value-format="HH:mm"
            />
          </el-form-item>
        </div>

        <!-- 第四步：预览发布 -->
        <div v-show="currentStep === 3" class="step-content">
          <h3 class="step-title">信息预览</h3>
          
          <div class="preview-section">
            <h4>企业基本信息</h4>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="企业名称">{{ formData.companyName }}</el-descriptions-item>
              <el-descriptions-item label="企业类型">{{ getCompanyTypeLabel(formData.companyType) }}</el-descriptions-item>
              <el-descriptions-item label="注册资本">{{ formData.registeredCapital }}万元</el-descriptions-item>
              <el-descriptions-item label="成立时间">{{ formatDate(formData.establishedDate) }}</el-descriptions-item>
              <el-descriptions-item label="所在地区" :span="2">{{ getLocationText(formData.location) }}</el-descriptions-item>
              <el-descriptions-item label="详细地址" :span="2">{{ formData.address }}</el-descriptions-item>
            </el-descriptions>
          </div>
          
          <div class="preview-section">
            <h4>投资需求信息</h4>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="行业方向">{{ getIndustryLabel(formData.industryDirection) }}</el-descriptions-item>
              <el-descriptions-item label="投资金额">{{ formData.investmentAmount }}万元</el-descriptions-item>
              <el-descriptions-item label="投资方式" :span="2">{{ formData.investmentType.join('、') }}</el-descriptions-item>
              <el-descriptions-item label="项目描述" :span="2">
                <div class="description-text">{{ formData.description }}</div>
              </el-descriptions-item>
              <el-descriptions-item v-if="formData.specialRequirements" label="特殊要求" :span="2">
                <div class="description-text">{{ formData.specialRequirements }}</div>
              </el-descriptions-item>
            </el-descriptions>
          </div>
          
          <div class="preview-section">
            <h4>联系人信息</h4>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="联系人姓名">{{ formData.contactPerson }}</el-descriptions-item>
              <el-descriptions-item label="联系电话">{{ formData.contactPhone }}</el-descriptions-item>
              <el-descriptions-item label="电子邮箱">{{ formData.contactEmail }}</el-descriptions-item>
              <el-descriptions-item label="微信号">{{ formData.wechatId || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="QQ号">{{ formData.qqId || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="最佳联系时间">{{ getBestContactTimeText() }}</el-descriptions-item>
            </el-descriptions>
          </div>
          
          <div class="publish-options">
            <el-checkbox v-model="agreeTerms">我已阅读并同意《线索发布协议》和《隐私政策》</el-checkbox>
          </div>
        </div>
      </el-form>
    </el-card>

    <!-- 操作按钮 -->
    <el-card class="action-card">
      <div class="action-buttons">
        <el-button v-if="currentStep > 0" @click="prevStep">上一步</el-button>
        <el-button v-if="currentStep < 3" type="primary" @click="nextStep">下一步</el-button>
        <el-button v-if="currentStep === 3" type="success" :disabled="!agreeTerms" :loading="submitting" @click="submitForm">
          发布线索
        </el-button>
        <el-button @click="resetForm">重置</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const formRef = ref<FormInstance>()

// 当前步骤
const currentStep = ref(0)

// 是否同意条款
const agreeTerms = ref(false)

// 提交状态
const submitting = ref(false)

// 表单数据
const formData = reactive({
  // 基本信息
  companyName: '',
  companyType: '',
  registeredCapital: null as number | null,
  establishedDate: '',
  location: [] as string[],
  address: '',
  
  // 投资需求
  industryDirection: '',
  investmentAmount: null as number | null,
  investmentType: [] as string[],
  description: '',
  specialRequirements: '',
  
  // 联系方式
  contactPerson: '',
  contactPhone: '',
  contactEmail: '',
  wechatId: '',
  qqId: '',
  bestContactTime: [] as string[]
})

// 表单验证规则
const formRules: FormRules = {
  companyName: [
    { required: true, message: '请输入企业名称', trigger: 'blur' },
    { min: 2, max: 100, message: '企业名称长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  companyType: [
    { required: true, message: '请选择企业类型', trigger: 'change' }
  ],
  registeredCapital: [
    { required: true, message: '请输入注册资本', trigger: 'blur' },
    { type: 'number', min: 0, message: '注册资本必须大于等于0', trigger: 'blur' }
  ],
  establishedDate: [
    { required: true, message: '请选择成立时间', trigger: 'change' }
  ],
  location: [
    { required: true, message: '请选择所在地区', trigger: 'change' }
  ],
  address: [
    { required: true, message: '请输入详细地址', trigger: 'blur' },
    { min: 5, max: 200, message: '详细地址长度在 5 到 200 个字符', trigger: 'blur' }
  ],
  industryDirection: [
    { required: true, message: '请选择行业方向', trigger: 'change' }
  ],
  investmentAmount: [
    { required: true, message: '请输入投资金额', trigger: 'blur' },
    { type: 'number', min: 0, message: '投资金额必须大于等于0', trigger: 'blur' }
  ],
  investmentType: [
    { required: true, message: '请选择投资方式', trigger: 'change' },
    { type: 'array', min: 1, message: '至少选择一种投资方式', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入项目描述', trigger: 'blur' },
    { min: 10, max: 1000, message: '项目描述长度在 10 到 1000 个字符', trigger: 'blur' }
  ],
  contactPerson: [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '联系人姓名长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  contactEmail: [
    { required: true, message: '请输入电子邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 地区选项（简化版，实际项目中应该从API获取）
const regionOptions = [
  {
    value: 'beijing',
    label: '北京市',
    children: [
      { value: 'dongcheng', label: '东城区' },
      { value: 'xicheng', label: '西城区' },
      { value: 'chaoyang', label: '朝阳区' },
      { value: 'fengtai', label: '丰台区' },
      { value: 'shijingshan', label: '石景山区' },
      { value: 'haidian', label: '海淀区' }
    ]
  },
  {
    value: 'shanghai',
    label: '上海市',
    children: [
      { value: 'huangpu', label: '黄浦区' },
      { value: 'xuhui', label: '徐汇区' },
      { value: 'changning', label: '长宁区' },
      { value: 'jingan', label: '静安区' },
      { value: 'putuo', label: '普陀区' },
      { value: 'hongkou', label: '虹口区' }
    ]
  },
  {
    value: 'guangdong',
    label: '广东省',
    children: [
      { value: 'guangzhou', label: '广州市' },
      { value: 'shenzhen', label: '深圳市' },
      { value: 'dongguan', label: '东莞市' },
      { value: 'foshan', label: '佛山市' },
      { value: 'zhongshan', label: '中山市' },
      { value: 'zhuhai', label: '珠海市' }
    ]
  },
  {
    value: 'jiangsu',
    label: '江苏省',
    children: [
      { value: 'nanjing', label: '南京市' },
      { value: 'suzhou', label: '苏州市' },
      { value: 'wuxi', label: '无锡市' },
      { value: 'changzhou', label: '常州市' },
      { value: 'nantong', label: '南通市' },
      { value: 'yangzhou', label: '扬州市' }
    ]
  }
]

// 获取企业类型标签
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

// 获取行业标签
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

// 格式化日期
const formatDate = (date: string) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

// 获取地区文本
const getLocationText = (location: string[]) => {
  if (!location || location.length === 0) return ''
  
  const province = regionOptions.find(p => p.value === location[0])
  if (!province) return ''
  
  if (location.length === 1) {
    return province.label
  }
  
  const city = province.children?.find(c => c.value === location[1])
  return city ? `${province.label} ${city.label}` : province.label
}

// 获取最佳联系时间文本
const getBestContactTimeText = () => {
  if (!formData.bestContactTime || formData.bestContactTime.length !== 2) {
    return '未设置'
  }
  return `${formData.bestContactTime[0]} - ${formData.bestContactTime[1]}`
}

// 下一步
const nextStep = async () => {
  if (!formRef.value) return
  
  // 验证当前步骤的表单字段
  const fieldsToValidate = getFieldsForStep(currentStep.value)
  
  try {
    await formRef.value.validateField(fieldsToValidate)
    currentStep.value++
  } catch (error) {
    ElMessage.warning('请完善当前步骤的必填信息')
  }
}

// 上一步
const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

// 获取当前步骤需要验证的字段
const getFieldsForStep = (step: number): string[] => {
  switch (step) {
    case 0:
      return ['companyName', 'companyType', 'registeredCapital', 'establishedDate', 'location', 'address']
    case 1:
      return ['industryDirection', 'investmentAmount', 'investmentType', 'description']
    case 2:
      return ['contactPerson', 'contactPhone', 'contactEmail']
    default:
      return []
  }
}

// 重置表单
const resetForm = async () => {
  try {
    await ElMessageBox.confirm('确定要重置表单吗？所有已填写的信息将被清空。', '确认重置', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    if (formRef.value) {
      formRef.value.resetFields()
    }
    
    // 重置其他状态
    currentStep.value = 0
    agreeTerms.value = false
    
    ElMessage.success('表单已重置')
  } catch {
    // 用户取消重置
  }
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    // 最终验证
    await formRef.value.validate()
    
    if (!agreeTerms.value) {
      ElMessage.warning('请先同意线索发布协议和隐私政策')
      return
    }
    
    submitting.value = true
    
    // 构造提交数据
    const submitData = {
      companyName: formData.companyName,
      companyType: formData.companyType,
      registeredCapital: formData.registeredCapital,
      establishedDate: formData.establishedDate,
      location: formData.location.join(','),
      address: formData.address,
      industryDirection: formData.industryDirection,
      investmentAmount: formData.investmentAmount,
      investmentType: formData.investmentType.join(','),
      description: formData.description,
      specialRequirements: formData.specialRequirements,
      contactPerson: formData.contactPerson,
      contactPhone: formData.contactPhone,
      contactEmail: formData.contactEmail,
      wechatId: formData.wechatId,
      qqId: formData.qqId,
      bestContactTime: formData.bestContactTime.join('-')
    }
    
    // 调用API
    const response = await axios.post('/api/leads', submitData)
    
    if (response.data.success) {
      ElMessage.success('线索发布成功！')
      
      // 跳转到线索列表页面
      await router.push('/leads/list')
    } else {
      ElMessage.error(response.data.message || '发布失败，请重试')
    }
    
  } catch (error: any) {
    console.error('提交表单失败:', error)
    
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('发布失败，请检查网络连接后重试')
    }
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.lead-create {
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

/* 步骤条样式 */
.steps-card {
  margin-bottom: 24px;
}

/* 表单卡片样式 */
.form-card {
  margin-bottom: 24px;
  min-height: 500px;
}

/* 步骤内容样式 */
.step-content {
  padding: 20px 0;
}

.step-title {
  margin: 0 0 24px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  border-bottom: 2px solid #f0f0f0;
  padding-bottom: 12px;
}

/* 表单项样式 */
.el-form-item {
  margin-bottom: 24px;
}

.unit-text {
  margin-left: 8px;
  color: #909399;
  font-size: 14px;
}

/* 预览部分样式 */
.preview-section {
  margin-bottom: 32px;
}

.preview-section h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  padding: 8px 12px;
  background-color: #f8f9fa;
  border-left: 4px solid #409eff;
}

.description-text {
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.publish-options {
  margin-top: 32px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 6px;
  text-align: center;
}

/* 操作按钮样式 */
.action-card {
  position: sticky;
  bottom: 0;
  z-index: 100;
  background-color: #fff;
  border-top: 1px solid #e4e7ed;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding: 16px 0;
}

.action-buttons .el-button {
  min-width: 100px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .step-content {
    padding: 16px 0;
  }
  
  .step-title {
    font-size: 16px;
    margin-bottom: 20px;
  }
  
  .el-form-item {
    margin-bottom: 20px;
  }
  
  .preview-section {
    margin-bottom: 24px;
  }
  
  .action-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .action-buttons .el-button {
    width: 200px;
  }
}

/* 表单验证错误样式优化 */
:deep(.el-form-item.is-error .el-input__wrapper) {
  box-shadow: 0 0 0 1px #f56c6c inset;
}

:deep(.el-form-item.is-error .el-textarea__inner) {
  box-shadow: 0 0 0 1px #f56c6c inset;
}

/* 步骤条样式优化 */
:deep(.el-steps) {
  padding: 20px 0;
}

:deep(.el-step__title) {
  font-weight: 600;
}

:deep(.el-step__description) {
  color: #909399;
  font-size: 13px;
}

/* 描述列表样式优化 */
:deep(.el-descriptions__label) {
  font-weight: 600;
  color: #606266;
}

:deep(.el-descriptions__content) {
  color: #303133;
}

/* 复选框组样式 */
:deep(.el-checkbox-group) {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

:deep(.el-checkbox) {
  margin-right: 0;
}

/* 级联选择器样式 */
:deep(.el-cascader) {
  width: 100%;
}

/* 时间选择器样式 */
:deep(.el-time-picker) {
  width: 100%;
}
</style>