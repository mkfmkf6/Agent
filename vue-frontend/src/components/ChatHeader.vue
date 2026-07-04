<script setup lang="ts">
defineProps<{
  title?: string
}>()
</script>

<template>
  <header class="chat-header">
    <div class="logo">
      <span class="logo-icon">🤖</span>
      <span class="logo-text">{{ title || 'HR 数字员工' }}</span>
    </div>
  </header>
</template>

<style scoped>
.chat-header {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  font-size: 28px;
}

.logo-text {
  font-size: 20px;
  font-weight: 600;
}
</style>