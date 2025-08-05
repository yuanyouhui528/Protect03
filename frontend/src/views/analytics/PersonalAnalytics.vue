<template>
  <div class="personal-analytics">
    <div class="page-header">
      <h2>个人看板</h2>
      <p class="page-description">查看个人线索和交换数据分析</p>
    </div>
    
    <el-card>
      <div class="coming-soon">
        <el-icon :size="80" color="#67C23A">
          <DataAnalysis />
        </el-icon>
        <h3>个人数据分析功能开发中...</h3>
        <p>该功能将包括个人线索统计、交换成功率、收益分析等数据可视化。</p>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { DataAnalysis } from '@element-plus/icons-vue'
</script>

<style scoped>
.personal-analytics {
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