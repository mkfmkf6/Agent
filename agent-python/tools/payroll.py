import httpx
from langchain.tools import tool
from pydantic import BaseModel, Field
from nacos_util import sync_discover_java_agent as discover_java_agent

def get_java_agent_url():
    return discover_java_agent()

class QueryPayrollArgs(BaseModel):
    emp_id: int = Field(description="员工ID")
    salary_month: str = Field(description="薪资月份，格式YYYY-MM")

class CalculateTaxArgs(BaseModel):
    gross_salary: float = Field(description="应发工资")
    social_security: float = Field(description="社保个人部分，可选")
    housing_fund: float = Field(description="公积金个人部分，可选")

@tool("query_payroll", args_schema=QueryPayrollArgs)
def query_payroll(emp_id: int, salary_month: str) -> str:
    """
    查询员工工资明细（基本工资、绩效、补贴、扣款）
    
    Args:
        emp_id: 员工ID
        salary_month: 薪资月份，格式YYYY-MM
    
    Returns:
        工资明细信息
    """
    try:
        url = f"{get_java_agent_url()}/payroll/query"
        params = {"empId": emp_id, "salaryMonth": salary_month}
        with httpx.Client(timeout=30) as client:
            response = client.get(url, params=params)
            response.raise_for_status()
            result = response.json()
            if result.get("code") == 1:
                data = result.get("data", {})
                result_str = f"【{salary_month}月工资明细】\n"
                result_str += f"┌─────────────────────────────────────\n"
                result_str += f"│ 收入项\n"
                result_str += f"│ 基本工资：{data.get('baseSalary', 0):.2f}元\n"
                result_str += f"│ 绩效工资：{data.get('performanceSalary', 0):.2f}元\n"
                result_str += f"│ 岗位补贴：{data.get('postAllowance', 0):.2f}元\n"
                result_str += f"│ 交通补贴：{data.get('transportAllowance', 0):.2f}元\n"
                result_str += f"│ 餐补：{data.get('mealAllowance', 0):.2f}元\n"
                result_str += f"│ 其他补贴：{data.get('otherAllowance', 0):.2f}元\n"
                result_str += f"│ ─────────────────────────────────\n"
                result_str += f"│ 应发工资：{data.get('grossSalary', 0):.2f}元\n"
                result_str += f"├─────────────────────────────────────\n"
                result_str += f"│ 扣款项\n"
                result_str += f"│ 社保个人：{data.get('socialSecurityPersonal', 0):.2f}元\n"
                result_str += f"│ 公积金个人：{data.get('housingFundPersonal', 0):.2f}元\n"
                result_str += f"│ 个人所得税：{data.get('personalIncomeTax', 0):.2f}元\n"
                result_str += f"│ 其他扣款：{data.get('otherDeduction', 0):.2f}元\n"
                result_str += f"│ ─────────────────────────────────\n"
                result_str += f"│ 实发工资：{data.get('netSalary', 0):.2f}元\n"
                result_str += f"└─────────────────────────────────────\n"
                result_str += f"发放状态：{data.get('payStatus', '')}"
                return result_str
            else:
                return f"查询工资明细失败：{result.get('msg', '未知错误')}"
    except Exception as e:
        return f"调用查询工资接口失败：{str(e)}"

@tool("calculate_tax", args_schema=CalculateTaxArgs)
def calculate_tax(gross_salary: float, social_security: float = 0, housing_fund: float = 0) -> str:
    """
    计算个人所得税（按最新起征点和税率）
    
    Args:
        gross_salary: 应发工资
        social_security: 社保个人部分（可选，默认0）
        housing_fund: 公积金个人部分（可选，默认0）
    
    Returns:
        个人所得税计算结果
    """
    try:
        url = f"{get_java_agent_url()}/payroll/calculateTax"
        data = {
            "grossSalary": gross_salary,
            "socialSecurityPersonal": social_security,
            "housingFundPersonal": housing_fund,
        }
        with httpx.Client(timeout=30) as client:
            response = client.post(url, json=data)
            response.raise_for_status()
            result = response.json()
            if result.get("code") == 1:
                return result.get("data", "")
            else:
                return f"计算个人所得税失败：{result.get('msg', '未知错误')}"
    except Exception as e:
        return f"调用计算个税接口失败：{str(e)}"

@tool("calculate_tax_simple", args_schema=CalculateTaxArgs)
def calculate_tax_simple(gross_salary: float) -> str:
    """
    简化计算个人所得税（仅需应发工资）
    
    Args:
        gross_salary: 应发工资
    
    Returns:
        个人所得税计算结果（含税率表）
    """
    try:
        url = f"{get_java_agent_url()}/payroll/calculateTaxSimple"
        params = {"grossSalary": gross_salary}
        with httpx.Client(timeout=30) as client:
            response = client.get(url, params=params)
            response.raise_for_status()
            result = response.json()
            if result.get("code") == 1:
                return result.get("data", "")
            else:
                return f"计算个人所得税失败：{result.get('msg', '未知错误')}"
    except Exception as e:
        return f"调用计算个税接口失败：{str(e)}"

payroll_tools = [
    query_payroll,
    calculate_tax,
    calculate_tax_simple,
]