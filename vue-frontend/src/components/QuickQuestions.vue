<script setup lang="ts">
const emit = defineEmits<{
  (e: 'question', question: string): void
}>()

const questions = [
  '查询招聘进度',
  '提交请假申请',
  '查询工资明细',
  '查询考勤记录',
  '计算个人所得税'
]

function handleClick(question: string) {
  emit('question', question)
}
</script>

<template>
  <div class="quick-questions">
    <button
      v-for="q in questions"
      :key="q"
      class="quick-btn"
      @click="handleClick(q)"
    >
      {{ q }}
    </button>
  </div>
</template>

<style scoped>
.quick-questions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.quick-btn {
  padding: 6px 14px;
  border: 1px solid #e4e7ed;
  border-radius: 20px;
  background: #f5f7fa;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.quick-btn:hover {
  background: #667eea;
  color: white;
  border-color: #667eea;
}
</style>