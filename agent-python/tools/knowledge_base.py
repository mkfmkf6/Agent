from langchain.tools import tool
from pydantic import BaseModel, Field

from knowledge_base.kb_service import query_knowledge

class QueryKnowledgeArgs(BaseModel):
    query: str = Field(description="查询问题，用于从知识库中检索相关制度依据")

@tool("query_knowledge_base", args_schema=QueryKnowledgeArgs)
def query_knowledge_base(query: str) -> str:
    """
    查询本地知识库，获取制度依据和相关政策信息
    
    知识库包含以下文档：
    - 员工手册：公司规章制度、入职离职流程、行为准则等
    - 考勤管理制度：工作时间、打卡规定、请假流程、加班管理等
    - 个税计算规则：个人所得税计算方法、税率表、专项附加扣除等
    
    Args:
        query: 查询问题，用于从知识库中检索相关内容
    
    Returns:
        知识库中与查询相关的内容，作为回答的制度依据
    """
    try:
        context = query_knowledge(query, k=3)
        if not context:
            return "知识库中未找到相关信息"
        
        result = f"【知识库检索结果】\n{context}\n\n以上内容可作为回答的制度依据，请结合具体情况参考使用。"
        return result
    except Exception as e:
        return f"调用知识库接口失败：{str(e)}"

knowledge_base_tools = [
    query_knowledge_base,
]