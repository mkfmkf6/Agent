export interface Message {
  id: number
  type: 'user' | 'agent'
  content: string
  timestamp: string
}

export interface ChatResponse {
  code: number
  msg: string
  data: {
    answer: string
    threadId: string
  }
}

export interface ChatRequest {
  thread_id: string
  query: string
  emp_id?: number
}

export interface QuickQuestion {
  label: string
  action: string
}