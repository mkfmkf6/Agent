<script setup lang="ts">
import type { Message } from '../types'

defineProps<{
  message: Message
}>()
</script>

<template>
  <div :class="['message', message.type]">
    <div class="message-avatar">
      {{ message.type === 'user' ? '👤' : '🤖' }}
    </div>
    <div class="message-content">
      <p>{{ message.content }}</p>
      <span class="message-time">{{ message.timestamp }}</span>
    </div>
  </div>
</template>

<style scoped>
.message {
  display: flex;
  gap: 12px;
}

.message.user {
  flex-direction: row-reverse;
}

.message.user .message-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 16px 16px 4px 16px;
}

.message.agent .message-content {
  background: #f5f7fa;
  color: #303133;
  border-radius: 16px 16px 16px 4px;
}

.message-avatar {
  font-size: 32px;
  flex-shrink: 0;
}

.message-content {
  max-width: 70%;
  padding: 12px 16px;
}

.message-content p {
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 6px;
  white-space: pre-wrap;
  word-break: break-word;
}

.message-time {
  font-size: 12px;
  opacity: 0.6;
}
</style>