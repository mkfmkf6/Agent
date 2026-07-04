from langchain.agents import create_agent
from langgraph.checkpoint.memory import InMemorySaver
from langchain_core.messages import HumanMessage, SystemMessage
from langchain.chat_models import init_chat_model
from langchain.tools import tool
from config import API_KEY, BASE_URL
from prompts import build_system_prompt
from pydantic import BaseModel
from tools.recruitment import recruitment_tools
from tools.resume import parse_resume
from tools.employee import employee_tools
from tools.attendance import attendance_tools
from tools.payroll import payroll_tools
from tools.employee_info import employee_info_tools
from tools.knowledge_base import knowledge_base_tools
from knowledge_base.kb_service import init_knowledge_base

init_knowledge_base()

model = init_chat_model(
    model="qwen3.7-plus",
    model_provider="openai",
    api_key=API_KEY,
    base_url=BASE_URL,
    temperature=0.3,
    streaming=False,
)

system_prompt = build_system_prompt()

tools = recruitment_tools + [parse_resume] + employee_tools + attendance_tools + payroll_tools + employee_info_tools + knowledge_base_tools

checkpointer = InMemorySaver()

agent = create_agent(
    model=model,
    tools=tools,
    system_prompt=system_prompt,
    checkpointer=checkpointer,
    debug=True,
)


class AgentChatReq(BaseModel):
    thread_id: str
    query: str


def call_agent(thread_id: str, query: str, emp_id: int = None, file_path: str = None) -> str:
    config = {"configurable": {"thread_id": thread_id}}
    
    import re
    position_name = None
    position_patterns = [
        r"岗位[是为：:]\s*([^\n。，,]+)",
        r"应聘[是为：:]\s*([^\n。，,]+)",
        r"招聘岗位[是为：:]\s*([^\n。，,]+)",
        r"应聘岗位[是为：:]\s*([^\n。，,]+)",
        r"申请[是为：:]\s*([^\n。，,]+)",
        r"投递[是为：:]\s*([^\n。，,]+)",
    ]
    for pattern in position_patterns:
        match = re.search(pattern, query)
        if match:
            position_name = match.group(1).strip()
            break
    
    if file_path:
        if position_name:
            query = f"文件路径：{file_path}\n\n应聘岗位：{position_name}\n\n{query}"
        else:
            query = f"文件路径：{file_path}\n\n{query}"
    
    try:
        input_data = {"messages": [HumanMessage(content=query)]}
        result = agent.invoke(input=input_data, config=config)
        messages = result.get("messages", [])
        if messages:
            last_msg = messages[-1]
            if hasattr(last_msg, 'content') and last_msg.content:
                return last_msg.content
            elif isinstance(last_msg, dict) and 'content' in last_msg:
                return last_msg['content']
        return "未获取到回答"
    except Exception as e:
        return f"智能体调用失败：{str(e)}"
