from fastapi import FastAPI, HTTPException, File, UploadFile
from agent import call_agent
from pydantic import BaseModel
import os
import asyncio
from nacos_util import register_service, start_heartbeat

LOCAL_PORT = 8000
LOCAL_HOST = "127.0.0.1"
UPLOAD_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "uploads")
os.makedirs(UPLOAD_DIR, exist_ok=True)

app = FastAPI(title="AI Agent Service", version="1.0")

heartbeat_task = None

@app.on_event("startup")
async def startup_event():
    global heartbeat_task
    await register_service(LOCAL_HOST, LOCAL_PORT)
    
    heartbeat_task = asyncio.create_task(start_heartbeat())
    
    print(f"🚀 AI Agent Service 启动完成... http://{LOCAL_HOST}:{LOCAL_PORT}")

class ChatRequest(BaseModel):
    thread_id: str = "default"
    query: str = ""
    emp_id: int = None

@app.post("/agent/chat")
async def agent_chat(request: ChatRequest):
    try:
        thread_id = request.thread_id
        query = request.query
        emp_id = request.emp_id
        
        if not query:
            return {
                "code": 400,
                "msg": "query参数不能为空",
                "data": None
            }
        
        resp = call_agent(thread_id, query, emp_id, None)
        return {
            "code": 200,
            "msg": "success",
            "data": {
                "answer": resp,
                "threadId": thread_id
            }
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Agent调用异常：{str(e)}")

@app.post("/agent/chat_with_file")
async def agent_chat_with_file(
    thread_id: str = "default",
    query: str = "",
    emp_id: int = None,
    file: UploadFile = None
):
    try:
        file_path = None
        if file:
            file_path = os.path.join(UPLOAD_DIR, file.filename)
            with open(file_path, "wb") as f:
                f.write(await file.read())
        
        if not query and file:
            query = f"请帮我解析这份{file.filename}文件"
        
        if not query:
            return {
                "code": 400,
                "msg": "query参数不能为空",
                "data": None
            }
        
        resp = call_agent(thread_id, query, emp_id, file_path)
        return {
            "code": 200,
            "msg": "success",
            "data": {
                "answer": resp,
                "threadId": thread_id
            }
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Agent调用异常：{str(e)}")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host=LOCAL_HOST, port=LOCAL_PORT)
