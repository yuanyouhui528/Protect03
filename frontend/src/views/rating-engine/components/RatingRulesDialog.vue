<template>
  <el-dialog
    v-model="dialogVisible"
    title="评级规则说明"
    width="1000px"
    :before-close="handleClose"
    destroy-on-close
  >
    <div v-loading="loading" class="rating-rules">
      <el-tabs v-model="activeTab" type="border-card">
        <!-- 评级概览 -->
        <el-tab-pane label="评级概览" name="overview">
          <div class="overview-content">
            <el-card class="section-card">
              <template #header>
                <div class="card-header">
                  <el-icon><TrendCharts /></el-icon>
                  <span>评级等级说明</span>
                </div>
              </template>
              
              <div class="rating-levels">
                <div v-for="level in ratingLevels" :key="level.grade" class="level-item">
                  <div class="level-header">
                    <RatingBadge :rating="level.grade" :score="level.scoreRange[1]" mode="normal" />
                    <div class="level-info">
                      <h3>{{ level.name }}</h3>
                      <p class="score-range">评分范围：{{ level.scoreRange[0] }} - {{ level.scoreRange[1] }}分</p>
                    </div>
                  </div>
                  <div class="level-description">
                    <p><strong>特征：</strong>{{ level.description }}</p>
                    <p><strong>价值：</strong>{{ level.value }}</p>
                    <p><strong>建议：</strong>{{ level.suggestion }}</p>
                  </div>
                </div>
              </div>
            </el-card>

            <el-card class="section-card">
              <template #header>
                <div class="card-header">
                  <el-icon><Coin /></el-icon>
                  <span>价值换算标准</span>
                </div>
              </template>
              
              <div class="value-conversion">
                <div class="conversion-formula">
                  <div class="formula-item">
                    <span class="grade">A级</span>
                    <span class="equals">=</span>
                    <span class="value">8分</span>
                  </div>
                  <div class="formula-item">
                    <span class="grade">B级</span>
                    <span class="equals">=</span>
                    <span class="value">4分</span>
                  </div>
                  <div class="formula-item">
                    <span class="grade">C级</span>
                    <span class="equals">=</span>
                    <span class="value">2分</span>
                  </div>
                  <div class="formula-item">
                    <span class="grade">D级</span>
                    <span class="equals">=</span>
                    <span class="value">1分</span>
                  </div>
                </div>
                <div class="conversion-examples">
                  <h4>换算示例：</h4>
                  <ul>
                    <li>1个A级线索 = 2个B级线索 = 4个C级线索 = 8个D级线索</li>
                    <li>3个B级线索 + 1个C级线索 = 14分（相当于1.75个A级线索）</li>
                    <li>5个C级线索 = 10分（相当于1.25个A级线索）</li>
                  </ul>
                </div>
              </div>
            </el-card>
          </div>
        </el-tab-pane>

        <!-- 维度说明 -->
        <el-tab-pane label="维度说明" name="dimensions">
          <div class="dimensions-content">
            <el-card class="section-card">
              <template #header>
                <div class="card-header">
                  <el-icon><DataAnalysis /></el-icon>
                  <span>评分维度详解</span>
                </div>
              </template>
              
              <div class="dimensions-list">
                <div v-for="dimension in dimensions" :key="dimension.key" class="dimension-item">
                  <div class="dimension-header">
                    <div class="dimension-title">
                      <el-icon :class="dimension.icon"></el-icon>
                      <h3>{{ dimension.name }}</h3>
                      <el-tag type="info" size="small">权重: {{ dimension.weight }}%</el-tag>
                    </div>
                    <div class="dimension-score">
                      <span>满分: {{ dimension.maxScore }}分</span>
                    </div>
                  </div>
                  
                  <div class="dimension-content">
                    <div class="dimension-description">
                      <p><strong>说明：</strong>{{ dimension.description }}</p>
                    </div>
                    
                    <div class="scoring-criteria">
                      <h4>评分标准：</h4>
                      <el-table :data="dimension.criteria" size="small" border>
                        <el-table-column prop="range" label="分数范围" width="100" />
                        <el-table-column prop="condition" label="评分条件" />
                        <el-table-column prop="example" label="示例" />
                      </el-table>
                    </div>
                  </div>
                </div>
              </div>
            </el-card>
          </div>
        </el-tab-pane>

        <!-- 算法流程 -->
        <el-tab-pane label="算法流程" name="algorithm">
          <div class="algorithm-content">
            <el-card class="section-card">
              <template #header>
                <div class="card-header">
                  <el-icon><Connection /></el-icon>
                  <span>评级计算流程</span>
                </div>
              </template>
              
              <div class="algorithm-flow">
                <div class="flow-steps">
                  <div v-for="(step, index) in algorithmSteps" :key="index" class="flow-step">
                    <div class="step-number">{{ index + 1 }}</div>
                    <div class="step-content">
                      <h4>{{ step.title }}</h4>
                      <p>{{ step.description }}</p>
                      <div v-if="step.formula" class="step-formula">
                        <code>{{ step.formula }}</code>
                      </div>
                    </div>
                    <div v-if="index < algorithmSteps.length - 1" class="step-arrow">
                      <el-icon><ArrowDown /></el-icon>
                    </div>
                  </div>
                </div>
                
                <div class="calculation-example">
                  <h4>计算示例：</h4>
                  <el-card class="example-card">
                    <div class="example-data">
                      <h5>某线索的维度评分：</h5>
                      <ul>
                        <li>信息完整度：85分 (权重25%)</li>
                        <li>企业规模：70分 (权重20%)</li>
                        <li>行业价值：90分 (权重15%)</li>
                        <li>地域价值：75分 (权重10%)</li>
                        <li>时效性：95分 (权重10%)</li>
                        <li>客户质量：80分 (权重10%)</li>
                        <li>历史表现：65分 (权重10%)</li>
                      </ul>
                    </div>
                    <div class="example-calculation">
                      <h5>计算过程：</h5>
                      <p>综合评分 = 85×0.25 + 70×0.20 + 90×0.15 + 75×0.10 + 95×0.10 + 80×0.10 + 65×0.10</p>
                      <p>= 21.25 + 14 + 13.5 + 7.5 + 9.5 + 8 + 6.5 = <strong>80.25分</strong></p>
                      <p>评级等级：<RatingBadge rating="B" :score="80.25" mode="compact" /></p>
                    </div>
                  </el-card>
                </div>
              </div>
            </el-card>
          </div>
        </el-tab-pane>

        <!-- 规则配置 -->
        <el-tab-pane label="规则配置" name="rules">
          <div class="rules-content">
            <el-card class="section-card">
              <template #header>
                <div class="card-header">
                  <el-icon><Setting /></el-icon>
                  <span>当前生效规则</span>
                  <el-button size="small" @click="loadRulesConfig">
                    <el-icon><Refresh /></el-icon>
                    刷新
                  </el-button>
                </div>
              </template>
              
              <div class="rules-list">
                <el-table :data="rulesConfig" border>
                  <el-table-column prop="name" label="规则名称" width="200" />
                  <el-table-column prop="description" label="规则描述" />
                  <el-table-column prop="priority" label="优先级" width="100" align="center" />
                  <el-table-column prop="status" label="状态" width="100" align="center">
                    <template #default="{ row }">
                      <el-tag :type="row.status === 'active' ? 'success' : 'info'">
                        {{ row.status === 'active' ? '生效' : '停用' }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column prop="updateTime" label="更新时间" width="180" />
                </el-table>
              </div>
            </el-card>

            <el-card class="section-card">
              <template #header>
                <div class="card-header">
                  <el-icon><Tools /></el-icon>
                  <span>算法参数配置</span>
                </div>
              </template>
              
              <div class="algorithm-params">
                <el-descriptions :column="2" border>
                  <el-descriptions-item label="评级阈值">
                    <div class="threshold-config">
                      <div>A级：≥ {{ algorithmConfig.thresholds?.A || 85 }}分</div>
                      <div>B级：{{ algorithmConfig.thresholds?.B || 70 }}-{{ (algorithmConfig.thresholds?.A || 85) - 1 }}分</div>
                      <div>C级：{{ algorithmConfig.thresholds?.C || 55 }}-{{ (algorithmConfig.thresholds?.B || 70) - 1 }}分</div>
                      <div>D级：< {{ algorithmConfig.thresholds?.C || 55 }}分</div>
                    </div>
                  </el-descriptions-item>
                  <el-descriptions-item label="维度权重">
                    <div class="weight-config">
                      <div v-for="(weight, key) in algorithmConfig.weights" :key="key">
                        {{ getDimensionName(key) }}：{{ weight }}%
                      </div>
                    </div>
                  </el-descriptions-item>
                  <el-descriptions-item label="缓存配置">
                    <div>缓存时间：{{ algorithmConfig.cacheTime || 30 }}分钟</div>
                    <div>自动刷新：{{ algorithmConfig.autoRefresh ? '开启' : '关闭' }}</div>
                  </el-descriptions-item>
                  <el-descriptions-item label="更新策略">
                    <div>实时更新：{{ algorithmConfig.realTimeUpdate ? '开启' : '关闭' }}</div>
                    <div>批量处理：{{ algorithmConfig.batchSize || 100 }}条/批次</div>
                  </el-descriptions-item>
                </el-descriptions>
              </div>
            </el-card>
          </div>
        </el-tab-pane>

        <!-- 常见问题 -->
        <el-tab-pane label="常见问题" name="faq">
          <div class="faq-content">
            <el-card class="section-card">
              <template #header>
                <div class="card-header">
                  <el-icon><QuestionFilled /></el-icon>
                  <span>常见问题解答</span>
                </div>
              </template>
              
              <el-collapse v-model="activeFaq">
                <el-collapse-item
                  v-for="(faq, index) in faqList"
                  :key="index"
                  :title="faq.question"
                  :name="index"
                >
                  <div class="faq-answer">
                    <p v-html="faq.answer"></p>
                    <div v-if="faq.example" class="faq-example">
                      <h5>示例：</h5>
                      <div v-html="faq.example"></div>
                    </div>
                  </div>
                </el-collapse-item>
              </el-collapse>
            </el-card>

            <el-card class="section-card">
              <template #header>
                <div class="card-header">
                  <el-icon><Service /></el-icon>
                  <span>联系我们</span>
                </div>
              </template>
              
              <div class="contact-info">
                <el-descriptions :column="1" border>
                  <el-descriptions-item label="技术支持">
                    <div class="contact-item">
                      <el-icon><Phone /></el-icon>
                      <span>400-123-4567</span>
                    </div>
                  </el-descriptions-item>
                  <el-descriptions-item label="邮箱反馈">
                    <div class="contact-item">
                      <el-icon><Message /></el-icon>
                      <span>support@leadexchange.com</span>
                    </div>
                  </el-descriptions-item>
                  <el-descriptions-item label="在线客服">
                    <div class="contact-item">
                      <el-icon><ChatDotRound /></el-icon>
                      <span>工作日 9:00-18:00</span>
                    </div>
                  </el-descriptions-item>
                  <el-descriptions-item label="帮助文档">
                    <div class="contact-item">
                      <el-icon><Document /></el-icon>
                      <el-link href="#" type="primary">查看完整文档</el-link>
                    </div>
                  </el-descriptions-item>
                </el-descriptions>
              </div>
            </el-card>
          </div>
        </el-tab-pane>
      </el-tabs>
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
import { ElMessage } from 'element-plus'
import {
  TrendCharts,
  Coin,
  DataAnalysis,
  Connection,
  ArrowDown,
  Setting,
  Refresh,
  Tools,
  QuestionFilled,
  Service,
  Phone,
  Message,
  ChatDotRound,
  Document
} from '@element-plus/icons-vue'
import RatingBadge from './RatingBadge.vue'
import { ratingRulesApi } from '@/api/rating-rules'

// 组件属性
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

// 事件定义
const emit = defineEmits(['update:modelValue'])

// 响应式数据
const loading = ref(false)
const activeTab = ref('overview')
const activeFaq = ref([])
const rulesConfig = ref([])
const algorithmConfig = ref({})

// 计算属性
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 评级等级数据
const ratingLevels = [
  {
    grade: 'A',
    name: '优质线索',
    scoreRange: [85, 100],
    description: '信息完整、企业规模大、行业价值高、成交概率高',
    value: '具有很高的商业价值，优先推荐给销售团队',
    suggestion: '立即跟进，重点关注，制定专门的销售策略'
  },
  {
    grade: 'B',
    name: '良好线索',
    scoreRange: [70, 84],
    description: '信息较完整、企业规模中等、有一定价值、成交可能性较高',
    value: '具有良好的商业价值，值得投入资源跟进',
    suggestion: '及时跟进，补充完善信息，提升转化率'
  },
  {
    grade: 'C',
    name: '一般线索',
    scoreRange: [55, 69],
    description: '信息基本完整、企业规模较小、价值一般、需要进一步验证',
    value: '具有一定的商业价值，可作为储备线索',
    suggestion: '定期跟进，持续培育，寻找合适时机'
  },
  {
    grade: 'D',
    name: '待完善线索',
    scoreRange: [0, 54],
    description: '信息不完整、企业规模小、价值较低、成交概率低',
    value: '商业价值较低，需要大量投入才能转化',
    suggestion: '低优先级跟进，重点完善信息，评估投入产出比'
  }
]

// 维度数据
const dimensions = [
  {
    key: 'completeness',
    name: '信息完整度',
    weight: 25,
    maxScore: 100,
    icon: 'el-icon-document',
    description: '评估线索信息的完整性和准确性，包括企业基本信息、联系方式、需求描述等',
    criteria: [
      { range: '90-100', condition: '信息完整，所有必填字段均已填写，信息准确可靠', example: '企业名称、联系人、电话、邮箱、地址、需求等信息齐全' },
      { range: '70-89', condition: '信息较完整，主要字段已填写，少量信息缺失', example: '缺少部分联系方式或详细地址' },
      { range: '50-69', condition: '信息基本完整，部分重要字段缺失', example: '缺少企业规模、行业信息等' },
      { range: '0-49', condition: '信息不完整，大量重要字段缺失', example: '仅有企业名称和基本联系方式' }
    ]
  },
  {
    key: 'companySize',
    name: '企业规模',
    weight: 20,
    maxScore: 100,
    icon: 'el-icon-office-building',
    description: '根据企业的员工数量、注册资本、年营业额等指标评估企业规模',
    criteria: [
      { range: '90-100', condition: '大型企业，员工500+，年营收1亿+', example: '上市公司、知名企业集团' },
      { range: '70-89', condition: '中型企业，员工100-500，年营收1000万-1亿', example: '行业内知名企业、区域龙头' },
      { range: '50-69', condition: '小型企业，员工20-100，年营收100万-1000万', example: '成长型企业、专业服务公司' },
      { range: '0-49', condition: '微型企业，员工20以下，年营收100万以下', example: '初创企业、个体工商户' }
    ]
  },
  {
    key: 'industryValue',
    name: '行业价值',
    weight: 15,
    maxScore: 100,
    icon: 'el-icon-pie-chart',
    description: '评估企业所属行业的市场前景、发展潜力和商业价值',
    criteria: [
      { range: '90-100', condition: '高价值行业，市场前景广阔', example: '科技、金融、医疗、新能源等' },
      { range: '70-89', condition: '中高价值行业，有一定发展潜力', example: '制造业、教育、物流等' },
      { range: '50-69', condition: '一般价值行业，市场相对稳定', example: '传统零售、餐饮、服务业等' },
      { range: '0-49', condition: '低价值行业，市场萎缩或竞争激烈', example: '夕阳产业、过度竞争行业' }
    ]
  },
  {
    key: 'regionValue',
    name: '地域价值',
    weight: 10,
    maxScore: 100,
    icon: 'el-icon-location',
    description: '根据企业所在地区的经济发展水平、市场活跃度评估地域价值',
    criteria: [
      { range: '90-100', condition: '一线城市核心区域', example: '北京、上海、深圳、广州CBD' },
      { range: '70-89', condition: '一线城市其他区域或新一线城市', example: '杭州、成都、武汉、南京等' },
      { range: '50-69', condition: '二线城市或经济发达地区', example: '省会城市、经济强县市' },
      { range: '0-49', condition: '三四线城市或经济欠发达地区', example: '偏远地区、经济落后地区' }
    ]
  },
  {
    key: 'timeliness',
    name: '时效性',
    weight: 10,
    maxScore: 100,
    icon: 'el-icon-timer',
    description: '评估线索的新鲜度和时效性，越新的线索价值越高',
    criteria: [
      { range: '90-100', condition: '24小时内的新线索', example: '当天提交的线索' },
      { range: '70-89', condition: '3天内的线索', example: '近期提交的热门线索' },
      { range: '50-69', condition: '1周内的线索', example: '一周内的有效线索' },
      { range: '0-49', condition: '1周以上的线索', example: '较老的线索，需要重新验证' }
    ]
  },
  {
    key: 'customerQuality',
    name: '客户质量',
    weight: 10,
    maxScore: 100,
    icon: 'el-icon-user',
    description: '评估客户的决策能力、购买意向和合作潜力',
    criteria: [
      { range: '90-100', condition: '决策者本人，购买意向强烈', example: '企业负责人直接咨询，有明确需求' },
      { range: '70-89', condition: '有决策影响力，购买意向较强', example: '部门负责人，有采购权限' },
      { range: '50-69', condition: '一般员工，需要上级决策', example: '普通员工咨询，需要汇报' },
      { range: '0-49', condition: '非目标客户或意向不明', example: '学生、个人用户等非企业客户' }
    ]
  },
  {
    key: 'historicalPerformance',
    name: '历史表现',
    weight: 10,
    maxScore: 100,
    icon: 'el-icon-data-line',
    description: '基于历史数据分析客户的合作表现和转化情况',
    criteria: [
      { range: '90-100', condition: '历史合作良好，多次成交', example: '老客户，有良好合作记录' },
      { range: '70-89', condition: '有过合作，表现良好', example: '曾经成交，满意度高' },
      { range: '50-69', condition: '有过接触，表现一般', example: '曾经咨询，未成交但有潜力' },
      { range: '0-49', condition: '新客户或历史表现不佳', example: '首次接触或曾经投诉' }
    ]
  }
]

// 算法流程步骤
const algorithmSteps = [
  {
    title: '数据收集',
    description: '收集线索的基本信息、企业信息、联系方式等数据',
    formula: null
  },
  {
    title: '数据清洗',
    description: '验证数据的完整性和准确性，处理缺失值和异常值',
    formula: null
  },
  {
    title: '维度评分',
    description: '对每个维度进行单独评分，计算各维度的得分',
    formula: '维度得分 = f(维度指标值)'
  },
  {
    title: '权重计算',
    description: '根据预设权重对各维度得分进行加权计算',
    formula: '综合评分 = Σ(维度得分 × 维度权重)'
  },
  {
    title: '等级划分',
    description: '根据综合评分和阈值设置确定最终的评级等级',
    formula: 'if 评分 ≥ 85 then A级; elif 评分 ≥ 70 then B级; elif 评分 ≥ 55 then C级; else D级'
  },
  {
    title: '结果输出',
    description: '输出最终的评级结果，包括等级、评分和各维度详情',
    formula: null
  }
]

// 常见问题数据
const faqList = [
  {
    question: '评级结果多久更新一次？',
    answer: '评级结果会根据线索信息的变化实时更新。当线索的基本信息、企业信息或其他关键数据发生变化时，系统会自动重新计算评级。同时，系统每天会进行一次全量的评级更新，确保所有线索的评级都是最新的。',
    example: '例如：当客户补充了企业规模信息后，系统会立即重新计算该线索的评级，可能从C级提升到B级。'
  },
  {
    question: '为什么我的线索评级突然发生了变化？',
    answer: '线索评级变化可能由以下原因引起：<br/>1. 线索信息更新（如补充了企业规模、联系方式等）<br/>2. 评级规则调整（如权重变化、阈值调整）<br/>3. 市场环境变化（如行业价值重新评估）<br/>4. 时效性影响（线索随时间推移价值下降）',
    example: '例如：一个B级线索因为超过一周未跟进，时效性评分下降，可能降级为C级。'
  },
  {
    question: '如何提升线索的评级？',
    answer: '提升线索评级的方法：<br/>1. 完善线索信息，提高信息完整度<br/>2. 验证和更新企业规模信息<br/>3. 及时跟进，保持线索的时效性<br/>4. 深入了解客户需求，提升客户质量评分<br/>5. 建立良好的客户关系，改善历史表现',
    example: '例如：通过电话沟通获得了客户的详细需求和企业规模信息，可以显著提升信息完整度和企业规模两个维度的评分。'
  },
  {
    question: '评级算法是否会考虑行业特殊性？',
    answer: '是的，评级算法会考虑不同行业的特殊性。系统会根据行业类型调整相应的评分标准和权重配置。例如，对于科技行业，创新能力和技术实力会被给予更高的权重；对于制造业，企业规模和生产能力会更加重要。',
    example: '例如：同样规模的企业，科技公司的行业价值评分会高于传统制造业企业。'
  },
  {
    question: '可以手动调整线索评级吗？',
    answer: '可以。系统支持手动调整线索评级，但需要提供调整原因和说明。手动调整会被记录在评级历史中，便于后续审计和分析。建议只在有充分理由的情况下进行手动调整，如获得了系统无法自动识别的重要信息。',
    example: '例如：通过线下会议了解到客户有紧急采购需求，可以手动将评级从B级调整为A级，并说明调整原因。'
  },
  {
    question: '评级历史记录保存多长时间？',
    answer: '评级历史记录会永久保存，包括每次评级变更的详细信息、变更原因、操作人员等。这些记录用于分析评级算法的效果、追踪线索的价值变化趋势，以及进行业务分析和优化。',
    example: null
  },
  {
    question: '如何理解评级的商业价值？',
    answer: '评级的商业价值体现在资源配置优化上：<br/>1. A级线索获得最高优先级和最多资源投入<br/>2. B级线索获得适中的关注和跟进<br/>3. C级线索作为储备，定期培育<br/>4. D级线索低优先级处理，重点完善信息<br/>通过这种分级管理，可以最大化销售团队的效率和转化率。',
    example: '例如：将80%的销售精力投入到A级和B级线索上，可以获得更高的成交率和收益。'
  }
]

// 监听对话框显示状态
watch(
  () => props.modelValue,
  (visible) => {
    if (visible) {
      loadRulesConfig()
    }
  }
)

// 方法定义

/**
 * 加载规则配置
 */
const loadRulesConfig = async () => {
  loading.value = true
  try {
    const [rulesResponse, configResponse] = await Promise.all([
      ratingRulesApi.getRulesList(),
      ratingRulesApi.getAlgorithmConfig()
    ])
    
    rulesConfig.value = rulesResponse.data
    algorithmConfig.value = configResponse.data
  } catch (error) {
    console.error('加载规则配置失败:', error)
    ElMessage.error('加载规则配置失败')
  } finally {
    loading.value = false
  }
}

/**
 * 获取维度名称
 */
const getDimensionName = (key) => {
  const dimensionMap = {
    completeness: '信息完整度',
    companySize: '企业规模',
    industryValue: '行业价值',
    regionValue: '地域价值',
    timeliness: '时效性',
    customerQuality: '客户质量',
    historicalPerformance: '历史表现'
  }
  return dimensionMap[key] || key
}

/**
 * 关闭对话框
 */
const handleClose = () => {
  dialogVisible.value = false
}
</script>

<style scoped>
.rating-rules {
  max-height: 70vh;
  overflow-y: auto;
}

.section-card {
  margin-bottom: 20px;
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #2c3e50;
}

/* 评级概览样式 */
.rating-levels {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.level-item {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 20px;
  background: #fafafa;
}

.level-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.level-info h3 {
  margin: 0 0 5px 0;
  color: #2c3e50;
}

.score-range {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.level-description p {
  margin: 8px 0;
  line-height: 1.6;
}

.value-conversion {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.conversion-formula {
  display: flex;
  justify-content: space-around;
  align-items: center;
  background: #f0f9ff;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #e1f5fe;
}

.formula-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
}

.grade {
  color: #409eff;
}

.equals {
  color: #666;
}

.value {
  color: #52c41a;
}

.conversion-examples h4 {
  margin: 0 0 10px 0;
  color: #2c3e50;
}

.conversion-examples ul {
  margin: 0;
  padding-left: 20px;
}

.conversion-examples li {
  margin: 8px 0;
  line-height: 1.6;
}

/* 维度说明样式 */
.dimensions-list {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.dimension-item {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 20px;
  background: #fafafa;
}

.dimension-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.dimension-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.dimension-title h3 {
  margin: 0;
  color: #2c3e50;
}

.dimension-score {
  color: #666;
  font-size: 14px;
}

.dimension-description {
  margin-bottom: 15px;
}

.dimension-description p {
  margin: 0;
  line-height: 1.6;
  color: #555;
}

.scoring-criteria h4 {
  margin: 0 0 10px 0;
  color: #2c3e50;
}

/* 算法流程样式 */
.algorithm-flow {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.flow-steps {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.flow-step {
  display: flex;
  align-items: flex-start;
  gap: 15px;
}

.step-number {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #409eff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  flex-shrink: 0;
}

.step-content {
  flex: 1;
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.step-content h4 {
  margin: 0 0 8px 0;
  color: #2c3e50;
}

.step-content p {
  margin: 0 0 10px 0;
  line-height: 1.6;
  color: #555;
}

.step-formula {
  background: #e8f4fd;
  padding: 8px 12px;
  border-radius: 4px;
  border: 1px solid #d1ecf1;
}

.step-formula code {
  font-family: 'Courier New', monospace;
  color: #2c3e50;
  font-weight: 600;
}

.step-arrow {
  display: flex;
  justify-content: center;
  color: #409eff;
  font-size: 20px;
}

.calculation-example {
  background: #f0f9ff;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #e1f5fe;
}

.calculation-example h4 {
  margin: 0 0 15px 0;
  color: #2c3e50;
}

.example-card {
  background: white;
  border: 1px solid #e8e8e8;
}

.example-data h5,
.example-calculation h5 {
  margin: 0 0 10px 0;
  color: #2c3e50;
}

.example-data ul {
  margin: 0;
  padding-left: 20px;
}

.example-data li {
  margin: 5px 0;
}

.example-calculation p {
  margin: 8px 0;
  line-height: 1.6;
}

/* 规则配置样式 */
.threshold-config,
.weight-config {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.threshold-config div,
.weight-config div {
  font-size: 14px;
  color: #555;
}

/* 常见问题样式 */
.faq-answer {
  line-height: 1.6;
  color: #555;
}

.faq-answer p {
  margin: 0 0 10px 0;
}

.faq-example {
  background: #f8f9fa;
  padding: 10px 15px;
  border-radius: 4px;
  border-left: 4px solid #409eff;
  margin-top: 10px;
}

.faq-example h5 {
  margin: 0 0 8px 0;
  color: #2c3e50;
}

.contact-info .contact-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .level-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .dimension-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .conversion-formula {
    flex-direction: column;
    gap: 15px;
  }
  
  .flow-step {
    flex-direction: column;
    gap: 10px;
  }
  
  .step-number {
    align-self: flex-start;
  }
}
</style>