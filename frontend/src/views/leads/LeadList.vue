<template>
  <div class="lead-list">
    <div class="page-header">
      <h2>线索列表</h2>
      <p class="page-description">管理和查看所有线索信息</p>
    </div>
    
    <el-card>
      <div class="coming-soon">
        <el-icon :size="80" color="#409EFF">
          <Document />
        </el-icon>
        <h3>线索列表功能开发中...</h3>
        <p>该功能将包括线索搜索、筛选、排序和详情查看等功能。</p>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { Document } from '@element-plus/icons-vue'
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