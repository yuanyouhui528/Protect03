<template>
  <div class="rating-badge" :class="ratingClass">
    <div class="rating-level">{{ rating }}</div>
    <div class="rating-score">{{ formattedScore }}</div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

// 组件属性
const props = defineProps({
  // 评级等级 (A/B/C/D)
  rating: {
    type: String,
    required: true,
    validator: (value) => ['A', 'B', 'C', 'D'].includes(value)
  },
  // 评级分数
  score: {
    type: Number,
    default: 0
  },
  // 显示模式 (normal/compact)
  mode: {
    type: String,
    default: 'normal',
    validator: (value) => ['normal', 'compact'].includes(value)
  },
  // 是否显示分数
  showScore: {
    type: Boolean,
    default: true
  }
})

// 计算属性

/**
 * 格式化分数显示
 */
const formattedScore = computed(() => {
  if (!props.showScore) return ''
  return props.score ? props.score.toFixed(1) : '0.0'
})

/**
 * 评级样式类
 */
const ratingClass = computed(() => {
  const classes = [`rating-${props.rating.toLowerCase()}`, `mode-${props.mode}`]
  if (!props.showScore) {
    classes.push('no-score')
  }
  return classes
})
</script>

<style scoped>
.rating-badge {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  font-weight: 600;
  text-align: center;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 正常模式 */
.mode-normal {
  min-width: 60px;
  padding: 8px 12px;
}

.mode-normal .rating-level {
  font-size: 18px;
  line-height: 1;
  margin-bottom: 2px;
}

.mode-normal .rating-score {
  font-size: 12px;
  opacity: 0.8;
}

/* 紧凑模式 */
.mode-compact {
  min-width: 45px;
  padding: 4px 8px;
}

.mode-compact .rating-level {
  font-size: 14px;
  line-height: 1;
  margin-bottom: 1px;
}

.mode-compact .rating-score {
  font-size: 10px;
  opacity: 0.8;
}

/* 不显示分数时的样式 */
.no-score {
  flex-direction: row;
}

.no-score .rating-level {
  margin-bottom: 0;
  font-size: 16px;
}

/* A级评级样式 */
.rating-a {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  color: white;
  border: 2px solid #52c41a;
}

.rating-a:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(82, 196, 26, 0.3);
}

/* B级评级样式 */
.rating-b {
  background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%);
  color: white;
  border: 2px solid #1890ff;
}

.rating-b:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(24, 144, 255, 0.3);
}

/* C级评级样式 */
.rating-c {
  background: linear-gradient(135deg, #faad14 0%, #ffc53d 100%);
  color: white;
  border: 2px solid #faad14;
}

.rating-c:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(250, 173, 20, 0.3);
}

/* D级评级样式 */
.rating-d {
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
  color: white;
  border: 2px solid #ff4d4f;
}

.rating-d:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(255, 77, 79, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .mode-normal {
    min-width: 50px;
    padding: 6px 10px;
  }
  
  .mode-normal .rating-level {
    font-size: 16px;
  }
  
  .mode-normal .rating-score {
    font-size: 11px;
  }
  
  .mode-compact {
    min-width: 40px;
    padding: 3px 6px;
  }
  
  .mode-compact .rating-level {
    font-size: 12px;
  }
  
  .mode-compact .rating-score {
    font-size: 9px;
  }
}
</style>