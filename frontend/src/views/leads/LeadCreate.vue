<template>
  <div class="lead-create">
    <div class="page-header">
      <h2>发布线索</h2>
      <p class="page-description">发布新的招商线索信息</p>
    </div>
    
    <el-card>
      <div class="coming-soon">
        <el-icon :size="80" color="#67C23A">
          <Plus />
        </el-icon>
        <h3>线索发布功能开发中...</h3>
        <p>该功能将包括线索信息填写、附件上传、预览发布等功能。</p>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { Plus } from '@element-plus/icons-vue'
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