<template>
  <div class="exchange-rules">
    <div class="page-header">
      <h2>交换规则</h2>
      <p class="page-description">了解线索交换的规则和流程</p>
    </div>
    
    <el-card>
      <div class="coming-soon">
        <el-icon :size="80" color="#F56C6C">
          <InfoFilled />
        </el-icon>
        <h3>交换规则功能开发中...</h3>
        <p>该功能将包括交换规则说明、价值计算方式、流程指导等内容。</p>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { InfoFilled } from '@element-plus/icons-vue'
</script>

<style scoped>
.exchange-rules {
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

.coming-soon {
  text-align: center;
  padding: 60px 20px;
}

.coming-soon h3 {
  margin: 20px 0 10px 0;
  color: #303133;
}

.coming-soon p {
  color: #909399;
  margin: 0;
}
</style>