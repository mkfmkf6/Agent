import os
from dotenv import load_dotenv
load_dotenv()

API_KEY = os.getenv('API_KEY')
BASE_URL = os.getenv('BASE_URL')

SYSTEM_PROMPT_FILE = "system_prompt.md"

NACOS_SERVER = os.getenv("NACOS_SERVER", "127.0.0.1:8848")
NACOS_NAMESPACE = os.getenv("NACOS_NAMESPACE", "public")
NACOS_SERVICE_NAME = os.getenv("NACOS_SERVICE_NAME", "ai-agent-service")
JAVA_AGENT_SERVICE_NAME = os.getenv("JAVA_AGENT_SERVICE_NAME", "agent-java")

def check_config():
    errors = []
    if not API_KEY:
        errors.append("❌ API_KEY 未配置")
    if not BASE_URL:
        errors.append("❌ BASE_URL 未配置")
    if errors:
        raise RuntimeError("配置错误：\n" + "\n".join(errors))
    print("✅ 配置检查通过")

DB_HOST = os.getenv("DB_HOST", "localhost")
DB_PORT = int(os.getenv("DB_PORT", 3306))
DB_USER = os.getenv("DB_USER", "root")
DB_PASSWORD = os.getenv("DB_PASSWORD")
DB_NAME = os.getenv("DB_NAME", "ecommerce_admin")

if __name__ == "__main__":
    check_config()