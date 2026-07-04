import httpx
from langchain.tools import tool
from pydantic import BaseModel, Field
from datetime import datetime
from nacos_util import sync_discover_java_agent as discover_java_agent

def get_java_agent_url():
    return discover_java_agent()

class QueryAttendanceArgs(BaseModel):
    emp_id: int = Field(description="员工ID")
    start_date: str = Field(description="开始日期，格式：YYYY-MM-DD")
    end_date: str = Field(description="结束日期，格式：YYYY-MM-DD")

class QueryOvertimeArgs(BaseModel):
    emp_id: int = Field(description="员工ID")

class ApplyLeaveArgs(BaseModel):
    emp_id: int = Field(description="员工ID")
    leave_type: int = Field(description="请假类型：1-年假 2-事假 3-病假 4-婚假 5-产假 6-丧假")
    start_time: str = Field(description="开始时间，格式：YYYY-MM-DD HH:mm")
    end_time: str = Field(description="结束时间，格式：YYYY-MM-DD HH:mm")
    remark: str = Field(description="备注", required=False)

class QueryLeaveArgs(BaseModel):
    emp_id: int = Field(description="员工ID")

class CalculateRemainingLeaveArgs(BaseModel):
    emp_id: int = Field(description="员工ID")

@tool("query_attendance", args_schema=QueryAttendanceArgs)
def query_attendance(emp_id: int, start_date: str, end_date: str) -> str:
    """
    查询员工考勤记录
    :param emp_id: 员工ID
    :param start_date: 开始日期
    :param end_date: 结束日期
    :return: 考勤记录信息
    """
    try:
        url = f"{get_java_agent_url()}/attendance/query"
        params = {"empId": emp_id, "startDate": start_date, "endDate": end_date}
        with httpx.Client(timeout=30) as client:
            response = client.get(url, params=params)
            response.raise_for_status()
            result = response.json()
            if result.get("code") == 1:
                records = result.get("data", [])
                if not records:
                    return "该时间段内无考勤记录"
                result_str = f"考勤记录查询结果（{start_date} 至 {end_date}）：\n"
                for record in records:
                    status = record.get("attendanceStatus", "未知")
                    result_str += f"日期：{record.get('attendanceDate')}，状态：{status}\n"
                return result_str
            else:
                return f"查询失败：{result.get('msg', '未知错误')}"
    except Exception as e:
        return f"调用查询考勤接口失败：{str(e)}"

@tool("query_overtime", args_schema=QueryOvertimeArgs)
def query_overtime(emp_id: int) -> str:
    """
    查询员工加班记录
    :param emp_id: 员工ID
    :return: 加班记录信息
    """
    try:
        url = f"{get_java_agent_url()}/attendance/queryOvertime"
        params = {"empId": emp_id}
        with httpx.Client(timeout=30) as client:
            response = client.get(url, params=params)
            response.raise_for_status()
            result = response.json()
            if result.get("code") == 1:
                data = result.get("data", [])
                if not data:
                    return "无加班记录"
                result_str = "加班记录查询结果：\n"
                for record in data:
                    result_str += f"日期：{record.get('attendanceDate')}，加班时长：{record.get('overtimeHours', 0)}小时\n"
                return result_str
            else:
                return f"查询失败：{result.get('msg', '未知错误')}"
    except Exception as e:
        return f"调用查询加班接口失败：{str(e)}"

@tool("apply_leave", args_schema=ApplyLeaveArgs)
def apply_leave(emp_id: int, leave_type: int, start_time: str, end_time: str, remark: str = "") -> str:
    """
    提交请假申请
    :param emp_id: 员工ID
    :param leave_type: 请假类型：1-年假 2-事假 3-病假 4-婚假 5-产假 6-丧假
    :param start_time: 开始时间，格式：YYYY-MM-DD HH:mm
    :param end_time: 结束时间，格式：YYYY-MM-DD HH:mm
    :param remark: 备注
    :return: 请假申请结果
    """
    try:
        url = f"{get_java_agent_url()}/leave/apply"
        
        start_time_iso = start_time.replace(" ", "T") + ":00"
        end_time_iso = end_time.replace(" ", "T") + ":00"
        
        data = {
            "empId": emp_id,
            "leaveType": leave_type,
            "startTime": start_time_iso,
            "endTime": end_time_iso,
            "remark": remark
        }
        with httpx.Client(timeout=30) as client:
            response = client.post(url, json=data)
            response.raise_for_status()
            result = response.json()
            if result.get("code") == 1:
                return "请假申请提交成功，等待审批"
            else:
                return f"提交失败：{result.get('msg', '未知错误')}"
    except Exception as e:
        return f"调用提交请假接口失败：{str(e)}"

@tool("query_leave", args_schema=QueryLeaveArgs)
def query_leave(emp_id: int) -> str:
    """
    查询员工请假记录
    :param emp_id: 员工ID
    :return: 请假记录信息
    """
    try:
        url = f"{get_java_agent_url()}/leave/query"
        params = {"empId": emp_id}
        with httpx.Client(timeout=30) as client:
            response = client.get(url, params=params)
            response.raise_for_status()
            result = response.json()
            if result.get("code") == 1:
                data = result.get("data", [])
                if not data:
                    return "无请假记录"
                result_str = "请假记录查询结果：\n"
                type_map = {1: "年假", 2: "事假", 3: "病假", 4: "婚假", 5: "产假", 6: "丧假"}
                status_map = {0: "待审批", 1: "已批准", 2: "已拒绝"}
                for record in data:
                    leave_type = type_map.get(record.get("leaveType"), "未知")
                    status = status_map.get(record.get("applyStatus"), "未知")
                    result_str += f"类型：{leave_type}，时间：{record.get('startTime')} 至 {record.get('endTime')}，状态：{status}\n"
                return result_str
            else:
                return f"查询失败：{result.get('msg', '未知错误')}"
    except Exception as e:
        return f"调用查询请假接口失败：{str(e)}"

@tool("calculate_remaining_leave", args_schema=CalculateRemainingLeaveArgs)
def calculate_remaining_leave(emp_id: int) -> str:
    """
    计算剩余假期天数
    :param emp_id: 员工ID
    :return: 剩余假期天数
    """
    try:
        url = f"{get_java_agent_url()}/leave/calculateRemaining"
        params = {"empId": emp_id}
        with httpx.Client(timeout=30) as client:
            response = client.get(url, params=params)
            response.raise_for_status()
            result = response.json()
            if result.get("code") == 1:
                data = result.get("data", {})
                return f"剩余假期计算结果：\n年假剩余：{data.get('annualLeaveRemaining', 0)}天\n事假剩余：{data.get('sickLeaveRemaining', 0)}天"
            else:
                return f"计算失败：{result.get('msg', '未知错误')}"
    except Exception as e:
        return f"调用计算剩余假期接口失败：{str(e)}"

attendance_tools = [
    query_attendance,
    query_overtime,
    apply_leave,
    query_leave,
    calculate_remaining_leave,
]