import axios from 'axios'
import type { ChatRequest, ChatResponse } from '../types'

const api = axios.create({
  baseURL: '/',
  timeout: 60000
})

export async function sendChatMessage(request: ChatRequest, file?: File): Promise<ChatResponse> {
  if (file) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('thread_id', request.thread_id)
    formData.append('query', request.query)
    if (request.emp_id) {
      formData.append('emp_id', request.emp_id.toString())
    }
    const response = await api.post('/agent/chat', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    return response.data
  } else {
    const response = await api.post<ChatResponse>('/agent/chat', request)
    return response.data
  }
}