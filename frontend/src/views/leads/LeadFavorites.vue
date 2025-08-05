<template>
  <div class="lead-favorites">
    <div class="page-header">
      <h2>我的收藏</h2>
      <p class="page-description">查看和管理收藏的线索</p>
    </div>
    
    <el-card>
      <div class="coming-soon">
        <el-icon :size="80" color="#E6A23C">
          <Star />
        </el-icon>
        <h3>收藏功能开发中...</h3>
        <p>该功能将包括收藏线索查看、分类管理、批量操作等功能。</p>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { Star } from '@element-plus/icons-vue'
</script>

<style scoped>
.lead-favorites {
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