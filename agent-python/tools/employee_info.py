import httpx
from langchain.tools import tool
from pydantic import BaseModel, Field
from nacos_util import sync_discover_java_agent as discover_java_agent

def get_java_agent_url():
    return discover_java_agent()

class GetEmployeeInfoByNameArgs(BaseModel):
    name: str = Field(description="员工姓名")

class GetEmployeeInfoArgs(BaseModel):
    emp_id: int = Field(description="员工ID")

class UpdateEmployeeInfoArgs(BaseModel):
    emp_id: int = Field(description="员工ID")
    name: str = Field(description="姓名", required=False)
    department: str = Field(description="部门", required=False)
    position: str = Field(description="岗位", required=False)
    job_level: str = Field(description="职别，如P0、P1、P2、M0、M1等", required=False)
    phone: str = Field(description="电话", required=False)
    email: str = Field(description="邮箱", required=False)

@tool("get_employee_id_by_name", args_schema=GetEmployeeInfoByNameArgs)
def get_employee_id_by_name(name: str) -> str:
    """
    通过员工姓名查询员工信息（获取员工ID）
    
    Args:
        name: 员工姓名
    
    Returns:
        员工信息，包含员工ID、姓名、部门、岗位等
    """
    try:
        url = f"{get_java_agent_url()}/employee/searchByName"
        params = {"name": name}
        with httpx.Client(timeout=30) as client:
            response = client.get(url, params=params)
            response.raise_for_status()
            result = response.json()
            if result.get("code") == 1:
                data = result.get("data", {})
                if data:
                    return f"查询到员工信息：\n员工ID：{data.get('empId')}\n工号：{data.get('empNo')}\n姓名：{data.get('name')}\n部门：{data.get('department')}\n岗位：{data.get('position')}"
                else:
                    return "未找到该员工"
            else:
                return f"查询失败：{result.get('msg', '未知错误')}"
    except Exception as e:
        return f"调用查询接口失败：{str(e)}"

@tool("get_employee_info", args_schema=GetEmployeeInfoArgs)
def get_employee_info(emp_id: int) -> str:
    """
    查询员工详细信息
    
    Args:
        emp_id: 员工ID
    
    Returns:
        员工详细信息
    """
    try:
        url = f"{get_java_agent_url()}/employee/info"
        params = {"empId": emp_id}
        with httpx.Client(timeout=30) as client:
            response = client.get(url, params=params)
            response.raise_for_status()
            result = response.json()
            if result.get("code") == 1:
                data = result.get("data", {})
                gender_map = {1: "男", 2: "女"}
                return f"员工信息：\n姓名：{data.get('name')}\n工号：{data.get('empNo')}\n性别：{gender_map.get(data.get('gender'), '未知')}\n部门：{data.get('department')}\n岗位：{data.get('position')}\n职级：{data.get('jobLevel')}\n联系电话：{data.get('phone')}\n邮箱：{data.get('email')}\n入职日期：{data.get('hireDate')}"
            else:
                return f"查询失败：{result.get('msg', '未知错误')}"
    except Exception as e:
        return f"调用查询接口失败：{str(e)}"

@tool("update_employee_info", args_schema=UpdateEmployeeInfoArgs)
def update_employee_info(emp_id: int, name: str = None, department: str = None, position: str = None, job_level: str = None, phone: str = None, email: str = None) -> str:
    """
    更新员工信息字段
    
    Args:
        emp_id: 员工ID
        name: 姓名（可选）
        department: 部门（可选）
        position: 岗位（可选）
        job_level: 职别，如P0、P1、P2、M0、M1等（可选）
        phone: 电话（可选）
        email: 邮箱（可选）
    
    Returns:
        更新结果
    """
    try:
        url = f"{get_java_agent_url()}/employee/update"
        data = {"empId": emp_id}
        if name:
            data["name"] = name
        if department:
            data["department"] = department
        if position:
            data["position"] = position
        if job_level:
            data["jobLevel"] = job_level
        if phone:
            data["phone"] = phone
        if email:
            data["email"] = email
        with httpx.Client(timeout=30) as client:
            response = client.post(url, json=data)
            response.raise_for_status()
            result = response.json()
            if result.get("code") == 1:
                return "员工信息更新成功"
            else:
                return f"更新失败：{result.get('msg', '未知错误')}"
    except Exception as e:
        return f"调用更新接口失败：{str(e)}"

@tool("get_all_departments")
def get_all_departments() -> str:
    """
    查询所有部门列表
    
    Returns:
        部门列表
    """
    try:
        url = f"{get_java_agent_url()}/employee/departments"
        with httpx.Client(timeout=30) as client:
            response = client.get(url)
            response.raise_for_status()
            result = response.json()
            if result.get("code") == 1:
                data = result.get("data", [])
                if not data:
                    return "暂无部门信息"
                result_str = "公司部门列表：\n"
                for dept in data:
                    result_str += f"• {dept}\n"
                return result_str
            else:
                return f"查询失败：{result.get('msg', '未知错误')}"
    except Exception as e:
        return f"调用查询接口失败：{str(e)}"

@tool("get_department_members")
def get_department_members(department: str) -> str:
    """
    查询部门成员信息
    
    Args:
        department: 部门名称
    
    Returns:
        部门成员列表
    """
    try:
        url = f"{get_java_agent_url()}/employee/departmentMembers"
        params = {"department": department}
        with httpx.Client(timeout=30) as client:
            response = client.get(url, params=params)
            response.raise_for_status()
            result = response.json()
            if result.get("code") == 1:
                data = result.get("data", [])
                if not data:
                    return f"{department}暂无成员信息"
                result_str = f"{department}成员列表：\n"
                for emp in data:
                    result_str += f"• {emp.get('name', '')} - {emp.get('position', '')}\n"
                return result_str
            else:
                return f"查询失败：{result.get('msg', '未知错误')}"
    except Exception as e:
        return f"调用查询接口失败：{str(e)}"

employee_info_tools = [
    get_employee_id_by_name,
    get_employee_info,
    update_employee_info,
    get_all_departments,
    get_department_members,
]