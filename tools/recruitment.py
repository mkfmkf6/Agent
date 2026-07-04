import httpx
from langchain.tools import tool
from langchain_core.tools import StructuredTool
from pydantic import BaseModel, Field
from nacos_util import sync_discover_java_agent as discover_java_agent

def get_java_agent_url():
    return discover_java_agent()

class QueryRecruitStatusArgs(BaseModel):
    recruit_id: int = Field(description="招聘ID，用于查询招聘进度")

class QueryInterviewArgs(BaseModel):
    recruit_id: int = Field(description="招聘ID，用于查询面试安排")

class ScheduleInterviewArgs(BaseModel):
    recruit_id: int = Field(description="招聘ID")
    interview_round: int = Field(description="面试轮次：1-初面 2-复面 3-终面")
    interview_time: str = Field(description="面试时间，格式：YYYY-MM-DD HH:mm")
    interviewer: str = Field(description="面试官姓名")

class QueryRecruitStatusByNameArgs(BaseModel):
    candidate_name: str = Field(description="候选人姓名，用于查询招聘进度")

class QueryInterviewByNameArgs(BaseModel):
    candidate_name: str = Field(description="候选人姓名，用于查询面试安排")

class UpdateRecruitStatusArgs(BaseModel):
    recruit_id: int = Field(description="招聘ID")
    status: int = Field(description="招聘状态：1-简历筛选 2-初面 3-复面 4-终面 5-Offer 6-已入职 7-淘汰")

class UpdateRecruitStatusByNameArgs(BaseModel):
    candidate_name: str = Field(description="候选人姓名")
    status: int = Field(description="招聘状态：1-简历筛选 2-初面 3-复面 4-终面 5-Offer 6-已入职 7-淘汰")

@tool("query_recruit_status", args_schema=QueryRecruitStatusArgs)
def query_recruit_status(recruit_id: int) -> str:
    """
    查询岗位招聘进度
    :param recruit_id: 招聘ID
    :return: 招聘进度信息，包含招聘ID、候选人姓名、招聘状态（1-简历筛选 2-初面 3-复面 4-终面 5-Offer 6-已入职 7-淘汰）
    """
    try:
        url = f"{get_java_agent_url()}/recruitment/queryRecruitStatus"
        params = {"recruitId": recruit_id}
        with httpx.Client(timeout=30) as client:
            response = client.get(url, params=params)
            response.raise_for_status()
            result = response.json()
            if result.get("code") == 1:
                data = result.get("data", {})
                return f"招聘进度查询结果：招聘ID={data.get('recruitId')}, 候选人姓名={data.get('candidateName')}, 招聘状态={data.get('recruitStatus')}"
            else:
                return f"查询失败：{result.get('msg', '未知错误')}"
    except Exception as e:
        return f"调用查询招聘进度接口失败：{str(e)}"

@tool("query_interview", args_schema=QueryInterviewArgs)
def query_interview(recruit_id: int) -> str:
    """
    查询面试安排
    :param recruit_id: 招聘ID
    :return: 面试安排信息，包含招聘ID、候选人姓名、当前面试轮次、面试时间、面试官
    """
    try:
        url = f"{get_java_agent_url()}/recruitment/queryInterview"
        params = {"recruitId": recruit_id}
        with httpx.Client(timeout=30) as client:
            response = client.get(url, params=params)
            response.raise_for_status()
            result = response.json()
            if result.get("code") == 1:
                data = result.get("data", {})
                return f"面试安排查询结果：招聘ID={data.get('recruitId')}, 候选人姓名={data.get('candidateName')}, 当前面试轮次={data.get('currentInterviewRound')}, 面试时间={data.get('interviewTime')}, 面试官={data.get('interviewer')}"
            else:
                return f"查询失败：{result.get('msg', '未知错误')}"
    except Exception as e:
        return f"调用查询面试安排接口失败：{str(e)}"

@tool("schedule_interview", args_schema=ScheduleInterviewArgs)
def schedule_interview(recruit_id: int, interview_round: int, interview_time: str, interviewer: str) -> str:
    """
    安排面试并发送通知
    :param recruit_id: 招聘ID
    :param interview_round: 面试轮次：1-初面 2-复面 3-终面
    :param interview_time: 面试时间，格式：YYYY-MM-DD HH:mm
    :param interviewer: 面试官姓名
    :return: 面试安排结果
    """
    try:
        url = f"{get_java_agent_url()}/recruitment/scheduleInterview"
        import re
        time_parts = re.match(r'(\d{4}-\d{2}-\d{2})\s*(\d{2}:\d{2})', interview_time)
        if time_parts:
            formatted_time = f"{time_parts.group(1)}T{time_parts.group(2)}:00"
        else:
            formatted_time = interview_time
        data = {
            "recruitId": recruit_id,
            "interviewRound": interview_round,
            "interviewTime": formatted_time,
            "interviewer": interviewer
        }
        print(f"📤 安排面试数据: {data}")
        with httpx.Client(timeout=30) as client:
            response = client.post(url, json=data)
            print(f"📥 响应状态码: {response.status_code}")
            response.raise_for_status()
            result = response.json()
            print(f"📥 响应结果: {result}")
            if result.get("code") == 1:
                round_map = {1: "初面", 2: "复面", 3: "终面"}
                return f"面试安排成功！\n招聘ID：{recruit_id}\n面试轮次：{round_map.get(interview_round, '未知轮次')}\n面试时间：{interview_time}\n面试官：{interviewer}\n\n已向候选人发送面试通知。"
            else:
                return f"安排失败：{result.get('msg', '未知错误')}"
    except Exception as e:
        return f"调用安排面试接口失败：{str(e)}"

@tool("query_recruit_status_by_name", args_schema=QueryRecruitStatusByNameArgs)
def query_recruit_status_by_name(candidate_name: str) -> str:
    """
    根据候选人姓名查询招聘进度（支持模糊查询）
    :param candidate_name: 候选人姓名
    :return: 招聘进度信息列表
    """
    try:
        url = f"{get_java_agent_url()}/recruitment/queryRecruitStatusByName"
        params = {"candidateName": candidate_name}
        with httpx.Client(timeout=30) as client:
            response = client.get(url, params=params)
            response.raise_for_status()
            result = response.json()
            if result.get("code") == 1:
                data = result.get("data", [])
                if not data:
                    return f"未找到姓名包含'{candidate_name}'的候选人招聘信息"
                status_map = {1: "简历筛选", 2: "初面", 3: "复面", 4: "终面", 5: "Offer", 6: "已入职", 7: "淘汰"}
                result_str = f"找到 {len(data)} 条招聘记录：\n\n"
                for item in data:
                    status_text = status_map.get(item.get('recruitStatus'), '未知')
                    result_str += f"招聘ID：{item.get('recruitId')}\n"
                    result_str += f"候选人姓名：{item.get('candidateName')}\n"
                    result_str += f"招聘状态：{status_text}\n"
                    result_str += "---\n"
                return result_str
            else:
                return f"查询失败：{result.get('msg', '未知错误')}"
    except Exception as e:
        return f"调用查询招聘进度接口失败：{str(e)}"

@tool("query_interview_by_name", args_schema=QueryInterviewByNameArgs)
def query_interview_by_name(candidate_name: str) -> str:
    """
    根据候选人姓名查询面试安排（支持模糊查询）
    :param candidate_name: 候选人姓名
    :return: 面试安排信息列表
    """
    try:
        url = f"{get_java_agent_url()}/recruitment/queryInterviewByName"
        params = {"candidateName": candidate_name}
        with httpx.Client(timeout=30) as client:
            response = client.get(url, params=params)
            response.raise_for_status()
            result = response.json()
            if result.get("code") == 1:
                data = result.get("data", [])
                if not data:
                    return f"未找到姓名包含'{candidate_name}'的候选人面试安排"
                result_str = f"找到 {len(data)} 条面试记录：\n\n"
                for item in data:
                    round_text = f"第{item.get('currentInterviewRound')}轮" if item.get('currentInterviewRound') else "未安排"
                    result_str += f"招聘ID：{item.get('recruitId')}\n"
                    result_str += f"候选人姓名：{item.get('candidateName')}\n"
                    result_str += f"当前面试轮次：{round_text}\n"
                    result_str += f"面试时间：{item.get('interviewTime') or '未安排'}\n"
                    result_str += f"面试官：{item.get('interviewer') or '未安排'}\n"
                    result_str += "---\n"
                return result_str
            else:
                return f"查询失败：{result.get('msg', '未知错误')}"
    except Exception as e:
        return f"调用查询面试安排接口失败：{str(e)}"

@tool("update_recruit_status", args_schema=UpdateRecruitStatusArgs)
def update_recruit_status(recruit_id: int, status: int) -> str:
    """
    更新招聘状态
    :param recruit_id: 招聘ID
    :param status: 招聘状态：1-简历筛选 2-初面 3-复面 4-终面 5-Offer 6-已入职 7-淘汰
    :return: 更新结果
    """
    try:
        url = f"{get_java_agent_url()}/recruitment/updateRecruitStatus"
        params = {"recruitId": recruit_id, "status": status}
        with httpx.Client(timeout=30) as client:
            response = client.post(url, params=params)
            response.raise_for_status()
            result = response.json()
            if result.get("code") == 1:
                status_map = {1: "简历筛选", 2: "初面", 3: "复面", 4: "终面", 5: "Offer", 6: "已入职", 7: "淘汰"}
                return f"状态更新成功！\n招聘ID：{recruit_id}\n新状态：{status_map.get(status, '未知')}"
            else:
                return f"更新失败：{result.get('msg', '未知错误')}"
    except Exception as e:
        return f"调用更新状态接口失败：{str(e)}"

@tool("update_recruit_status_by_name", args_schema=UpdateRecruitStatusByNameArgs)
def update_recruit_status_by_name(candidate_name: str, status: int) -> str:
    """
    根据姓名更新招聘状态
    :param candidate_name: 候选人姓名
    :param status: 招聘状态：1-简历筛选 2-初面 3-复面 4-终面 5-Offer 6-已入职 7-淘汰
    :return: 更新结果
    """
    try:
        url = f"{get_java_agent_url()}/recruitment/updateRecruitStatusByName"
        params = {"candidateName": candidate_name, "status": status}
        with httpx.Client(timeout=30) as client:
            response = client.post(url, params=params)
            response.raise_for_status()
            result = response.json()
            if result.get("code") == 1:
                status_map = {1: "简历筛选", 2: "初面", 3: "复面", 4: "终面", 5: "Offer", 6: "已入职", 7: "淘汰"}
                return f"状态更新成功！\n候选人：{candidate_name}\n新状态：{status_map.get(status, '未知')}"
            else:
                return f"更新失败：{result.get('msg', '未知错误')}"
    except Exception as e:
        return f"调用更新状态接口失败：{str(e)}"

recruitment_tools = [
    query_recruit_status,
    query_recruit_status_by_name,
    query_interview,
    query_interview_by_name,
    schedule_interview,
    update_recruit_status,
    update_recruit_status_by_name,
]