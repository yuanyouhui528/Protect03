<template>
  <div class="exchange-rules">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>交换规则</h1>
      <p class="page-description">了解线索交换的规则和流程</p>
    </div>

    <!-- 规则内容 -->
    <div class="rules-content">
      <!-- 基本规则 -->
      <div class="rule-section">
        <h2>基本规则</h2>
        <div class="rule-cards">
          <div class="rule-card">
            <div class="rule-icon">
              <el-icon><User /></el-icon>
            </div>
            <h3>实名认证</h3>
            <p>所有用户必须完成实名认证才能参与线索交换</p>
          </div>
          
          <div class="rule-card">
            <div class="rule-icon">
              <el-icon><Star /></el-icon>
            </div>
            <h3>信用评级</h3>
            <p>系统根据用户行为自动评定信用等级，影响交换权限</p>
          </div>
          
          <div class="rule-card">
            <div class="rule-icon">
              <el-icon><Shield /></el-icon>
            </div>
            <h3>质量保证</h3>
            <p>所有线索必须经过质量审核，确保信息真实有效</p>
          </div>
        </div>
      </div>

      <!-- 交换流程 -->
      <div class="rule-section">
        <h2>交换流程</h2>
        <div class="process-steps">
          <div class="step">
            <div class="step-number">1</div>
            <div class="step-content">
              <h4>浏览线索</h4>
              <p>在交换市场中浏览可交换的线索</p>
            </div>
          </div>
          
          <div class="step">
            <div class="step-number">2</div>
            <div class="step-content">
              <h4>发起申请</h4>
              <p>选择目标线索，提供自己的线索进行交换申请</p>
            </div>
          </div>
          
          <div class="step">
            <div class="step-number">3</div>
            <div class="step-content">
              <h4>等待审核</h4>
              <p>线索所有者审核交换申请，可以同意或拒绝</p>
            </div>
          </div>
          
          <div class="step">
            <div class="step-number">4</div>
            <div class="step-content">
              <h4>完成交换</h4>
              <p>双方确认后，系统自动完成线索权限转移</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 价值计算 -->
      <div class="rule-section">
        <h2>价值计算</h2>
        <div class="value-rules">
          <div class="value-item">
            <h4>评级权重</h4>
            <div class="rating-weights">
              <div class="weight-item">
                <span class="rating">A级</span>
                <span class="weight">权重: 1.0</span>
              </div>
              <div class="weight-item">
                <span class="rating">B级</span>
                <span class="weight">权重: 0.8</span>
              </div>
              <div class="weight-item">
                <span class="rating">C级</span>
                <span class="weight">权重: 0.6</span>
              </div>
              <div class="weight-item">
                <span class="rating">D级</span>
                <span class="weight">权重: 0.4</span>
              </div>
            </div>
          </div>
          
          <div class="value-item">
            <h4>计算公式</h4>
            <div class="formula">
              <code>线索价值 = 基础分值 × 评级权重 × 行业系数</code>
            </div>
            <p class="formula-desc">系统会根据线索的评级、行业类型等因素自动计算价值</p>
          </div>
        </div>
      </div>

      <!-- 积分系统 -->
      <div class="rule-section">
        <h2>积分系统</h2>
        <div class="credit-rules">
          <div class="credit-item">
            <h4>积分获取</h4>
            <ul>
              <li>成功完成交换：+10积分</li>
              <li>提供高质量线索：+5积分</li>
              <li>及时响应申请：+2积分</li>
              <li>完善个人信息：+1积分</li>
            </ul>
          </div>
          
          <div class="credit-item">
            <h4>积分扣除</h4>
            <ul>
              <li>提供虚假线索：-20积分</li>
              <li>恶意取消交换：-10积分</li>
              <li>长期不响应：-5积分</li>
              <li>违反平台规则：-15积分</li>
            </ul>
          </div>
          
          <div class="credit-item">
            <h4>积分等级</h4>
            <div class="credit-levels">
              <div class="level-item">
                <span class="level gold">金牌用户</span>
                <span class="requirement">≥ 100积分</span>
              </div>
              <div class="level-item">
                <span class="level silver">银牌用户</span>
                <span class="requirement">50-99积分</span>
              </div>
              <div class="level-item">
                <span class="level bronze">铜牌用户</span>
                <span class="requirement">20-49积分</span>
              </div>
              <div class="level-item">
                <span class="level normal">普通用户</span>
                <span class="requirement">0-19积分</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 注意事项 -->
      <div class="rule-section">
        <h2>注意事项</h2>
        <div class="notice-list">
          <div class="notice-item warning">
            <el-icon><Warning /></el-icon>
            <div class="notice-content">
              <h4>交换申请有效期</h4>
              <p>交换申请的有效期为7天，过期后将自动取消</p>
            </div>
          </div>
          
          <div class="notice-item info">
            <el-icon><InfoFilled /></el-icon>
            <div class="notice-content">
              <h4>线索质量要求</h4>
              <p>所有参与交换的线索必须是真实、有效、完整的商业信息</p>
            </div>
          </div>
          
          <div class="notice-item success">
            <el-icon><CircleCheck /></el-icon>
            <div class="notice-content">
              <h4>交换记录保存</h4>
              <p>所有交换记录将永久保存，可在交换历史中查看</p>
            </div>
          </div>
          
          <div class="notice-item error">
            <el-icon><CircleClose /></el-icon>
            <div class="notice-content">
              <h4>违规处理</h4>
              <p>违反交换规则的用户将被限制交换权限或封禁账号</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 常见问题 -->
      <div class="rule-section">
        <h2>常见问题</h2>
        <div class="faq-list">
          <el-collapse>
            <el-collapse-item title="如何提高线索交换成功率？" name="1">
              <p>1. 确保线索信息真实完整</p>
              <p>2. 选择价值匹配的线索进行交换</p>
              <p>3. 在申请中详细说明交换理由</p>
              <p>4. 及时响应对方的交换申请</p>
            </el-collapse-item>
            
            <el-collapse-item title="交换后发现线索质量问题怎么办？" name="2">
              <p>1. 立即联系客服举报问题线索</p>
              <p>2. 提供相关证据材料</p>
              <p>3. 系统会进行调查并给予相应补偿</p>
              <p>4. 问题线索提供者将受到相应处罚</p>
            </el-collapse-item>
            
            <el-collapse-item title="为什么我的交换申请被拒绝？" name="3">
              <p>可能的原因包括：</p>
              <p>1. 提供的线索价值不匹配</p>
              <p>2. 线索质量不符合要求</p>
              <p>3. 申请理由不够充分</p>
              <p>4. 对方暂时不需要此类线索</p>
            </el-collapse-item>
            
            <el-collapse-item title="如何查看我的积分和等级？" name="4">
              <p>1. 进入个人中心查看积分详情</p>
              <p>2. 在交换市场页面查看当前等级</p>
              <p>3. 积分变动会有系统通知</p>
              <p>4. 可在积分排行榜中查看排名</p>
            </el-collapse-item>
          </el-collapse>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { User, Star, Shield, Warning, InfoFilled, CircleCheck, CircleClose } from '@element-plus/icons-vue'
</script>

<style scoped>
.exchange-rules {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 32px;
}

.page-description {
  margin: 0;
  color: #909399;
  font-size: 16px;
}

.rules-content {
  display: grid;
  gap: 40px;
}

.rule-section {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.rule-section h2 {
  margin: 0 0 24px 0;
  color: #303133;
  font-size: 24px;
  border-bottom: 2px solid #409eff;
  padding-bottom: 8px;
}

/* 基本规则卡片 */
.rule-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.rule-card {
  text-align: center;
  padding: 24px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  transition: all 0.3s;
}

.rule-card:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.rule-icon {
  font-size: 48px;
  color: #409eff;
  margin-bottom: 16px;
}

.rule-card h3 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 18px;
}

.rule-card p {
  margin: 0;
  color: #606266;
  line-height: 1.6;
}

/* 交换流程 */
.process-steps {
  display: grid;
  gap: 20px;
}

.step {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.step-number {
  width: 40px;
  height: 40px;
  background: #409eff;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 18px;
  flex-shrink: 0;
}

.step-content h4 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 16px;
}

.step-content p {
  margin: 0;
  color: #606266;
  line-height: 1.5;
}

/* 价值计算 */
.value-rules {
  display: grid;
  gap: 24px;
}

.value-item h4 {
  margin: 0 0 16px 0;
  color: #303133;
  font-size: 18px;
}

.rating-weights {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 12px;
}

.weight-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f5f7fa;
  border-radius: 6px;
}

.rating {
  font-weight: bold;
  color: #409eff;
}

.weight {
  color: #606266;
  font-size: 14px;
}

.formula {
  background: #f5f7fa;
  padding: 16px;
  border-radius: 6px;
  margin-bottom: 8px;
}

.formula code {
  color: #e6a23c;
  font-family: 'Courier New', monospace;
  font-size: 16px;
}

.formula-desc {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

/* 积分系统 */
.credit-rules {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}

.credit-item h4 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 16px;
}

.credit-item ul {
  margin: 0;
  padding-left: 20px;
  color: #606266;
}

.credit-item li {
  margin-bottom: 8px;
  line-height: 1.5;
}

.credit-levels {
  display: grid;
  gap: 8px;
}

.level-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  border-radius: 4px;
  background: #f5f7fa;
}

.level {
  font-weight: bold;
  padding: 2px 8px;
  border-radius: 4px;
  color: white;
}

.level.gold {
  background: #f7ba2a;
}

.level.silver {
  background: #c0c4cc;
}

.level.bronze {
  background: #cd7f32;
}

.level.normal {
  background: #909399;
}

.requirement {
  color: #606266;
  font-size: 14px;
}

/* 注意事项 */
.notice-list {
  display: grid;
  gap: 16px;
}

.notice-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  border-radius: 8px;
  border-left: 4px solid;
}

.notice-item.warning {
  background: #fdf6ec;
  border-color: #e6a23c;
}

.notice-item.info {
  background: #f4f4f5;
  border-color: #909399;
}

.notice-item.success {
  background: #f0f9ff;
  border-color: #67c23a;
}

.notice-item.error {
  background: #fef0f0;
  border-color: #f56c6c;
}

.notice-item .el-icon {
  font-size: 20px;
  margin-top: 2px;
}

.notice-item.warning .el-icon {
  color: #e6a23c;
}

.notice-item.info .el-icon {
  color: #909399;
}

.notice-item.success .el-icon {
  color: #67c23a;
}

.notice-item.error .el-icon {
  color: #f56c6c;
}

.notice-content h4 {
  margin: 0 0 4px 0;
  color: #303133;
  font-size: 16px;
}

.notice-content p {
  margin: 0;
  color: #606266;
  line-height: 1.5;
}

/* 常见问题 */
.faq-list {
  margin-top: 16px;
}

.faq-list :deep(.el-collapse-item__header) {
  font-size: 16px;
  font-weight: bold;
}

.faq-list :deep(.el-collapse-item__content) {
  padding: 16px 20px;
  line-height: 1.6;
}

.faq-list p {
  margin: 4px 0;
  color: #606266;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .exchange-rules {
    padding: 16px;
  }
  
  .rule-section {
    padding: 20px;
  }
  
  .rule-cards {
    grid-template-columns: 1fr;
  }
  
  .credit-rules {
    grid-template-columns: 1fr;
  }
  
  .rating-weights {
    grid-template-columns: 1fr;
  }
}
</style>