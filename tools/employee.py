import httpx
from langchain.tools import tool
from pydantic import BaseModel, Field
from nacos_util import sync_discover_java_agent as discover_java_agent

def get_java_agent_url():
    return discover_java_agent()

class CreateOnboardingArgs(BaseModel):
    emp_no: str = Field(description="工号")
    name: str = Field(description="姓名")
    gender: int = Field(description="性别：1-男 2-女")
    department: str = Field(description="部门")
    position: str = Field(description="岗位")
    phone: str = Field(description="联系电话")
    email: str = Field(description="邮箱")

class GenerateOnboardingMaterialsArgs(BaseModel):
    emp_id: int = Field(description="员工ID")

class OpenEmployeeAccountsArgs(BaseModel):
    emp_id: int = Field(description="员工ID")

class CreateOffboardingArgs(BaseModel):
    emp_id: int = Field(description="员工ID")
    resign_type: int = Field(description="离职类型：1-自愿离职 2-合同到期 3-辞退")

class CreateOffboardingByNameArgs(BaseModel):
    name: str = Field(description="员工姓名")
    resign_type: int = Field(description="离职类型：1-自愿离职 2-合同到期 3-辞退")

class GenerateOffboardingListArgs(BaseModel):
    emp_id: int = Field(description="员工ID")

class ConfirmHandoverArgs(BaseModel):
    emp_id: int = Field(description="员工ID")

@tool("create_onboarding", args_schema=CreateOnboardingArgs)
def create_onboarding(emp_no: str, name: str, gender: int, department: str, position: str, phone: str, email: str) -> str:
    """
    创建新员工入职记录
    :param emp_no: 工号
    :param name: 姓名
    :param gender: 性别：1-男 2-女
    :param department: 部门
    :param position: 岗位
    :param phone: 联系电话
    :param email: 邮箱
    :return: 入职创建结果
    """
    try:
        url = f"{get_java_agent_url()}/employee/onboarding"
        data = {
            "empNo": emp_no,
            "name": name,
            "gender": gender,
            "department": department,
            "position": position,
            "jobLevel": "P0",
            "phone": phone,
            "email": email
        }
        with httpx.Client(timeout=30) as client:
            response = client.post(url, json=data)
            response.raise_for_status()
            result = response.json()
            if result.get("code") == 1:
                return f"新员工入职创建成功！工号：{emp_no}，姓名：{name}"
            else:
                return f"创建失败：{result.get('msg', '未知错误')}"
    except Exception as e:
        return f"调用创建入职接口失败：{str(e)}"

@tool("generate_onboarding_materials", args_schema=GenerateOnboardingMaterialsArgs)
def generate_onboarding_materials(emp_id: int) -> str:
    """
    生成入职材料清单（Word文档）
    :param emp_id: 员工ID
    :return: 入职材料生成结果
    """
    try:
        url = f"{get_java_agent_url()}/employee/onboarding/materials"
        params = {"empId": emp_id}
        with httpx.Client(timeout=30) as client:
            response = client.post(url, params=params)
            response.raise_for_status()
            result = response.json()
            if result.get("code") == 1:
                return result.get("data", "入职材料生成成功")
            else:
                return f"生成失败：{result.get('msg', '未知错误')}"
    except Exception as e:
        return f"调用生成入职材料接口失败：{str(e)}"

@tool("open_employee_accounts", args_schema=OpenEmployeeAccountsArgs)
def open_employee_accounts(emp_id: int) -> str:
    """
    为员工开通账号
    :param emp_id: 员工ID
    :return: 账号开通结果
    """
    try:
        url = f"{get_java_agent_url()}/employee/onboarding/openAccounts"
        params = {"empId": emp_id}
        with httpx.Client(timeout=30) as client:
            response = client.post(url, params=params)
            response.raise_for_status()
            result = response.json()
            if result.get("code") == 1:
                return result.get("data", "账号开通成功")
            else:
                return f"开通失败：{result.get('msg', '未知错误')}"
    except Exception as e:
        return f"调用开通账号接口失败：{str(e)}"

@tool("create_offboarding", args_schema=CreateOffboardingArgs)
def create_offboarding(emp_id: int, resign_type: int) -> str:
    """
    创建员工离职记录
    :param emp_id: 员工ID
    :param resign_type: 离职类型：1-自愿离职 2-合同到期 3-辞退
    :return: 离职创建结果
    """
    try:
        url = f"{get_java_agent_url()}/employee/offboarding"
        data = {"empId": emp_id, "resignType": resign_type}
        with httpx.Client(timeout=30) as client:
            response = client.post(url, json=data)
            response.raise_for_status()
            result = response.json()
            if result.get("code") == 1:
                return f"员工离职记录创建成功！员工ID：{emp_id}"
            else:
                return f"创建失败：{result.get('msg', '未知错误')}"
    except Exception as e:
        return f"调用创建离职接口失败：{str(e)}"

@tool("create_offboarding_by_name", args_schema=CreateOffboardingByNameArgs)
def create_offboarding_by_name(name: str, resign_type: int) -> str:
    """
    根据员工姓名创建离职记录
    :param name: 员工姓名
    :param resign_type: 离职类型：1-自愿离职 2-合同到期 3-辞退
    :return: 离职创建结果
    """
    try:
        url = f"{get_java_agent_url()}/employee/offboardingByName"
        data = {"name": name, "resignType": resign_type}
        with httpx.Client(timeout=30) as client:
            response = client.post(url, json=data)
            response.raise_for_status()
            result = response.json()
            if result.get("code") == 1:
                return f"员工离职记录创建成功！姓名：{name}"
            else:
                return f"创建失败：{result.get('msg', '未知错误')}"
    except Exception as e:
        return f"调用创建离职接口失败：{str(e)}"

@tool("generate_offboarding_list", args_schema=GenerateOffboardingListArgs)
def generate_offboarding_list(emp_id: int) -> str:
    """
    生成离职清单（Word文档）
    :param emp_id: 员工ID
    :return: 离职清单生成结果
    """
    try:
        url = f"{get_java_agent_url()}/employee/offboarding/generateList"
        params = {"empId": emp_id}
        with httpx.Client(timeout=30) as client:
            response = client.post(url, params=params)
            response.raise_for_status()
            result = response.json()
            if result.get("code") == 1:
                return result.get("data", "离职清单生成成功")
            else:
                return f"生成失败：{result.get('msg', '未知错误')}"
    except Exception as e:
        return f"调用生成离职清单接口失败：{str(e)}"

@tool("confirm_handover", args_schema=ConfirmHandoverArgs)
def confirm_handover(emp_id: int) -> str:
    """
    确认离职交接完成
    :param emp_id: 员工ID
    :return: 交接确认结果
    """
    try:
        url = f"{get_java_agent_url()}/employee/offboarding/confirmHandover"
        params = {"empId": emp_id}
        with httpx.Client(timeout=30) as client:
            response = client.post(url, params=params)
            response.raise_for_status()
            result = response.json()
            if result.get("code") == 1:
                return result.get("data", "交接确认完成")
            else:
                return f"确认失败：{result.get('msg', '未知错误')}"
    except Exception as e:
        return f"调用确认交接接口失败：{str(e)}"

employee_tools = [
    create_onboarding,
    generate_onboarding_materials,
    open_employee_accounts,
    create_offboarding,
    create_offboarding_by_name,
    generate_offboarding_list,
    confirm_handover,
]