import os
import re
import json
from typing import Dict, List, Optional
from langchain.tools import tool
from pydantic import BaseModel, Field

try:
    from docx import Document
    DOCX_AVAILABLE = True
except ImportError:
    DOCX_AVAILABLE = False

try:
    from pypdf import PdfReader
    PDF_AVAILABLE = True
except ImportError:
    PDF_AVAILABLE = False

UPLOAD_DIR = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "uploads")
os.makedirs(UPLOAD_DIR, exist_ok=True)

class ParseResumeArgs(BaseModel):
    file_path: str = Field(description="简历文件路径，支持PDF和Word格式")
    position_name: Optional[str] = Field(description="招聘岗位名称", default=None)

def extract_text_from_pdf(file_path: str) -> str:
    text = ""
    if not PDF_AVAILABLE:
        return "未安装pypdf依赖，无法解析PDF文件"
    try:
        reader = PdfReader(file_path)
        for page in reader.pages:
            text += page.extract_text() or ""
    except Exception as e:
        print(f"解析PDF文件失败: {e}")
    return text

def extract_text_from_docx(file_path: str) -> str:
    text = ""
    if not DOCX_AVAILABLE:
        return "未安装python-docx依赖，无法解析Word文件"
    try:
        doc = Document(file_path)
        for paragraph in doc.paragraphs:
            text += paragraph.text + "\n"
    except Exception as e:
        print(f"解析Word文件失败: {e}")
    return text

def extract_text_from_file(file_path: str) -> str:
    ext = os.path.splitext(file_path)[1].lower()
    if ext == ".pdf":
        return extract_text_from_pdf(file_path)
    elif ext in [".docx", ".doc"]:
        return extract_text_from_docx(file_path)
    else:
        raise ValueError(f"不支持的文件格式: {ext}")

def extract_name(text: str, file_path: str = "") -> Optional[str]:
    patterns = [
        r"姓名[\s：:](.*?)\n",
        r"Name[\s：:](.*?)\n",
        r"姓\s*名[\s：:](.*?)\n",
        r"个人信息[\s\S]*?姓名[\s：:](.*?)\n",
    ]
    for pattern in patterns:
        match = re.search(pattern, text)
        if match:
            name = match.group(1).strip()
            if name and len(name) <= 10:
                return name
    
    if file_path:
        file_name = os.path.basename(file_path)
        file_name = file_name.replace(".pdf", "").replace(".docx", "").replace(".doc", "")
        
        name_patterns = [
            r".*[\-_（\(]([\u4e00-\u9fa5]{2,6})[\-_）\)]?",
            r"([\u4e00-\u9fa5]{2,6})[\-_]",
            r"^([\u4e00-\u9fa5]{2,6})$",
        ]
        for pattern in name_patterns:
            match = re.search(pattern, file_name)
            if match:
                name = match.group(1).strip()
                if name and len(name) <= 10 and not any(c.isdigit() for c in name):
                    return name
    return None

def extract_phone(text: str) -> Optional[str]:
    phone_pattern = r"1[3-9]\d{9}"
    match = re.search(phone_pattern, text)
    return match.group() if match else None

def extract_email(text: str) -> Optional[str]:
    email_pattern = r"[\w.-]+@[\w.-]+\.\w+"
    match = re.search(email_pattern, text)
    return match.group() if match else None

def extract_education(text: str) -> List[Dict]:
    education_list = []
    
    edu_keywords = ["教育背景", "教育经历", "学历", "毕业院校", "教育", "院校", "学校"]
    for keyword in edu_keywords:
        if keyword in text:
            start_idx = text.find(keyword)
            remaining_text = text[start_idx:start_idx+1000]
            lines = remaining_text.split('\n')
            
            school_patterns = [
                r"(\d{4})[.\-/年]\s*(\d{2,4})[.\-/年至今]*\s*([^\n]+?)\s*([本科|硕士|博士|专科|大专|高中|中专]+)\s*([^\n]*)",
                r"(\d{4})[.\-/年]\s*([^\n]+?)\s*([本科|硕士|博士|专科|大专|高中|中专]+)",
                r"([^\n]+?)\s*([本科|硕士|博士|专科|大专|高中|中专]+)\s*(\d{4})[\s年]",
                r"([^\n]+?)\s*([本科|硕士|博士|专科|大专|高中|中专]+)",
            ]
            
            for line in lines[:10]:
                for pattern in school_patterns:
                    match = re.search(pattern, line)
                    if match:
                        school_info = {}
                        groups = match.groups()
                        if len(groups) >= 2:
                            if groups[0].isdigit():
                                school_info["start_date"] = groups[0]
                                if len(groups) >= 2:
                                    if groups[1].isdigit() or groups[1] == "至今":
                                        school_info["end_date"] = groups[1]
                                        if len(groups) >= 3:
                                            school_info["school"] = groups[2].strip()
                                            if len(groups) >= 4:
                                                school_info["degree"] = groups[3].strip()
                                                if len(groups) >= 5:
                                                    school_info["major"] = groups[4].strip()
                                    else:
                                        school_info["school"] = groups[1].strip()
                                        school_info["degree"] = groups[2].strip()
                            else:
                                school_info["school"] = groups[0].strip()
                                school_info["degree"] = groups[1].strip()
                                if len(groups) >= 3 and groups[2].isdigit():
                                    school_info["start_date"] = groups[2]
                        if school_info and school_info.get("school"):
                            education_list.append(school_info)
                            break
                if education_list and len(education_list) >= 3:
                    break
            break
    
    return education_list

def extract_work_experience(text: str) -> List[Dict]:
    work_list = []
    
    work_keywords = ["工作经历", "工作经验", "职业经历", "工作", "实习经历"]
    for keyword in work_keywords:
        if keyword in text:
            start_idx = text.find(keyword)
            remaining_text = text[start_idx:start_idx+1500]
            lines = remaining_text.split('\n')
            
            company_patterns = [
                r"(\d{4})[.\-/年]\s*(\d{2,4}|至今)[.\-/年]*\s*([^\n]+?)\s*([^\n]+?)",
                r"(\d{4})[.\-/年]\s*([^\n]+?)\s*([^\n]+?)",
                r"([^\n]+?)\s*([^\n]+?)\s*(\d{4})[\s年]",
                r"([^\n]+?)\s*([^\n]+?)\s*(至今)",
            ]
            
            for line in lines[:15]:
                for pattern in company_patterns:
                    match = re.search(pattern, line)
                    if match:
                        work_info = {}
                        groups = match.groups()
                        if len(groups) >= 2:
                            if groups[0].isdigit():
                                work_info["start_date"] = groups[0]
                                if len(groups) >= 2:
                                    if groups[1].isdigit() or "至今" in groups[1]:
                                        work_info["end_date"] = groups[1].strip()
                                        if len(groups) >= 3:
                                            work_info["company"] = groups[2].strip()
                                            if len(groups) >= 4:
                                                work_info["position"] = groups[3].strip()
                                    else:
                                        work_info["company"] = groups[1].strip()
                                        work_info["position"] = groups[2].strip()
                            else:
                                work_info["company"] = groups[0].strip()
                                work_info["position"] = groups[1].strip()
                                if len(groups) >= 3 and (groups[2].isdigit() or "至今" in groups[2]):
                                    work_info["start_date"] = groups[2].strip()
                        if work_info and work_info.get("company"):
                            work_list.append(work_info)
                            break
                if work_list and len(work_list) >= 5:
                    break
            break
    
    return work_list

def extract_skills(text: str) -> List[str]:
    skills = []
    skill_patterns = [
        r"(技能|专业技能|技能特长|掌握技能)[\s：:](.*?)\n",
        r"(编程语言|技术栈|技术能力)[\s：:](.*?)\n",
    ]
    for pattern in skill_patterns:
        match = re.search(pattern, text)
        if match:
            skill_text = match.group(2).strip()
            skills.extend([s.strip() for s in re.split(r"[,，、\s]+", skill_text) if s.strip()])
    if not skills:
        tech_keywords = [
            "Java", "Python", "JavaScript", "TypeScript", "C++", "C#", "Go", "Rust",
            "Spring", "SpringBoot", "Django", "Flask", "Vue", "React", "Angular",
            "MySQL", "PostgreSQL", "MongoDB", "Redis", "Elasticsearch",
            "Docker", "Kubernetes", "Git", "Linux", "AWS", "阿里云", "腾讯云",
            "微服务", "分布式", "高并发", "大数据", "机器学习", "深度学习",
            "HTML", "CSS", "Node.js", "Webpack", "Vite", "Nginx", "Tomcat",
            "MyBatis", "Hibernate", "JPA", "RabbitMQ", "Kafka", "Zookeeper",
            "Spark", "Hadoop", "Hive", "Flink", "TensorFlow", "PyTorch",
        ]
        for keyword in tech_keywords:
            if keyword.lower() in text.lower():
                skills.append(keyword)
    return list(set(skills))

def save_candidate_to_db(resume_info: Dict, file_path: str, position_name: Optional[str]) -> bool:
    try:
        import httpx
        from nacos_util import sync_discover_java_agent as discover_java_agent
        
        java_url = discover_java_agent()
        url = f"{java_url}/recruitment/saveCandidate"
        print(f"🔌 正在调用JavaAgent保存候选人: {url}")
        
        candidate_name = resume_info.get("name", "")
        print(f"📝 候选人姓名: '{candidate_name}'")
        print(f"📱 候选人电话: '{resume_info.get('phone', '')}'")
        print(f"📧 候选人邮箱: '{resume_info.get('email', '')}'")
        print(f"💼 应聘岗位: '{position_name}'")
        
        if not candidate_name:
            print("❌ 候选人为空，尝试从文件名提取...")
            file_name = os.path.basename(file_path)
            import re
            name_patterns = [
                r".*[\-_（\(]([\u4e00-\u9fa5]{2,6})[\-_）\)]?",
                r"([\u4e00-\u9fa5]{2,6})[\-_]",
            ]
            for pattern in name_patterns:
                match = re.search(pattern, file_name)
                if match:
                    candidate_name = match.group(1).strip()
                    print(f"✅ 从文件名提取姓名: {candidate_name}")
                    break
        
        data = {
            "positionName": position_name or "",
            "candidateName": candidate_name,
            "candidatePhone": resume_info.get("phone", ""),
            "candidateEmail": resume_info.get("email", ""),
            "resumeSource": "系统上传",
            "resumeParseStatus": 2,
            "resumeFilePath": file_path,
            "educationBackground": json.dumps(resume_info.get("education", []), ensure_ascii=False),
            "workExperience": json.dumps(resume_info.get("work_experience", []), ensure_ascii=False),
            "skillTags": ",".join(resume_info.get("skills", [])),
        }
        
        print(f"📤 请求数据: {json.dumps(data, ensure_ascii=False)[:200]}...")
        
        with httpx.Client(timeout=30) as client:
            response = client.post(url, json=data)
            print(f"📥 响应状态码: {response.status_code}")
            response.raise_for_status()
            result = response.json()
            print(f"📥 响应结果: {json.dumps(result, ensure_ascii=False)}")
            if result.get("code") == 1:
                print("✅ 候选人保存成功")
                return True
            else:
                print(f"❌ 保存失败: {result.get('msg', '未知错误')}")
                return False
    except Exception as e:
        print(f"❌ 保存候选人信息异常: {type(e).__name__}: {e}")
        import traceback
        traceback.print_exc()
        return False

@tool("parse_resume", args_schema=ParseResumeArgs)
def parse_resume(file_path: str, position_name: Optional[str] = None) -> str:
    """
    解析简历文件，提取结构化信息并保存到候选人库
    
    Args:
        file_path: 简历文件路径，支持PDF和Word格式
        position_name: 招聘岗位名称（可选）
    
    Returns:
        解析结果的字符串
    """
    try:
        if not os.path.exists(file_path):
            return f"文件不存在: {file_path}"
        
        text = extract_text_from_file(file_path)
        if not text or "未安装" in text:
            return text if "未安装" in text else "无法从文件中提取文本内容"
        
        if not position_name:
            import re
            position_patterns = [
                r"岗位[是为：:]\s*([^\n。，,]+)",
                r"应聘[是为：:]\s*([^\n。，,]+)",
                r"招聘岗位[是为：:]\s*([^\n。，,]+)",
                r"应聘岗位[是为：:]\s*([^\n。，,]+)",
                r"申请[是为：:]\s*([^\n。，,]+)",
                r"投递[是为：:]\s*([^\n。，,]+)",
            ]
            for pattern in position_patterns:
                match = re.search(pattern, text)
                if match:
                    position_name = match.group(1).strip()
                    print(f"✅ 从文本中提取岗位名称: {position_name}")
                    break
        
        if not position_name:
            position_name = "后端开发"
            print(f"⚠️ 未提取到岗位名称，使用默认值: {position_name}")
        
        resume_info = {
            "name": extract_name(text, file_path) or "",
            "phone": extract_phone(text),
            "email": extract_email(text),
            "education": extract_education(text),
            "work_experience": extract_work_experience(text),
            "skills": extract_skills(text),
        }
        
        saved = save_candidate_to_db(resume_info, file_path, position_name)
        
        result_str = f"简历解析结果:\n"
        result_str += f"姓名: {resume_info['name']}\n"
        if resume_info['phone']:
            result_str += f"手机号: {resume_info['phone']}\n"
        if resume_info['email']:
            result_str += f"邮箱: {resume_info['email']}\n"
        result_str += f"教育背景: {len(resume_info['education'])}条\n"
        for edu in resume_info['education']:
            result_str += f"  - {edu.get('school', '')} {edu.get('degree', '')}\n"
        result_str += f"工作经历: {len(resume_info['work_experience'])}条\n"
        for work in resume_info['work_experience']:
            result_str += f"  - {work.get('company', '')} {work.get('position', '')}\n"
        result_str += f"技能标签: {', '.join(resume_info['skills'])}\n"
        result_str += f"保存状态: {'已保存到候选人库' if saved else '保存失败'}"
        
        return result_str
    except Exception as e:
        return f"解析简历失败: {str(e)}"