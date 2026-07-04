<script setup lang="ts">
import { ref, computed } from 'vue'
import QuickQuestions from './QuickQuestions.vue'

const props = defineProps<{
  isLoading: boolean
}>()

const emit = defineEmits<{
  (e: 'send', message: string, file: File | null): void
}>()

const inputMessage = ref('')
const uploadedFile = ref<File | null>(null)
const fileInputRef = ref<HTMLInputElement | null>(null)

const canSend = computed(() => {
  return !props.isLoading && (inputMessage.value.trim() || uploadedFile.value)
})

function handleQuickQuestion(question: string) {
  inputMessage.value = question
}

function handleFileUpload(event: Event) {
  const target = event.target as HTMLInputElement
  if (target.files && target.files.length > 0) {
    uploadedFile.value = target.files[0]
    if (!inputMessage.value) {
      inputMessage.value = `请帮我解析这份${uploadedFile.value.name}文件`
    }
  }
}

function handleSend() {
  if (!canSend.value) return
  emit('send', inputMessage.value.trim(), uploadedFile.value)
  inputMessage.value = ''
  uploadedFile.value = null
}

function handleKeyup(event: KeyboardEvent) {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    handleSend()
  }
}
</script>

<template>
  <div class="chat-input-area">
    <QuickQuestions @question="handleQuickQuestion" />
    <div class="input-row">
      <label class="upload-btn">
        <input
          ref="fileInputRef"
          type="file"
          accept=".pdf,.doc,.docx,.txt"
          @change="handleFileUpload"
          hidden
        />
        📎 上传文件
      </label>
      <div v-if="uploadedFile" class="uploaded-file">
        ✅ {{ uploadedFile.name }}
      </div>
      <input
        v-model="inputMessage"
        type="text"
        placeholder="请输入您的问题..."
        @keyup="handleKeyup"
      />
      <button class="send-btn" @click="handleSend" :disabled="!canSend">
        {{ isLoading ? '发送中...' : '发送' }}
      </button>
    </div>
  </div>
</template>

<style scoped>
.chat-input-area {
  background: white;
  border-radius: 16px;
  padding: 16px 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.input-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.upload-btn {
  padding: 10px 16px;
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
  background: #fafafa;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.upload-btn:hover {
  border-color: #667eea;
  background: #f0f5ff;
}

.uploaded-file {
  padding: 8px 12px;
  background: #f0f9eb;
  color: #67c23a;
  border-radius: 6px;
  font-size: 13px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 150px;
}

.input-row input[type="text"] {
  flex: 1;
  padding: 10px 16px;
  border: 1px solid #e4e7ed;
  border-radius: 24px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s;
}

.input-row input[type="text"]:focus {
  border-color: #667eea;
}

.send-btn {
  padding: 10px 28px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 24px;
  font-size: 14px;
  cursor: pointer;
  transition: opacity 0.3s;
}

.send-btn:hover:not(:disabled) {
  opacity: 0.9;
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>