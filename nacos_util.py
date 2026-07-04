import time
import random
import asyncio
import httpx
from config import NACOS_SERVER, NACOS_NAMESPACE, NACOS_SERVICE_NAME, JAVA_AGENT_SERVICE_NAME

_java_agent_url = None
_registered = False
_heartbeat_count = 0

async def register_service(ip: str, port: int):
    global _registered
    try:
        async with httpx.AsyncClient() as client:
            url = f"http://{NACOS_SERVER}/nacos/v1/ns/instance"
            params = {
                "serviceName": NACOS_SERVICE_NAME,
                "ip": ip,
                "port": port,
                "namespaceId": NACOS_NAMESPACE,
                "metadata": '{"version": "1.0", "language": "python"}',
                "ephemeral": "true"
            }
            response = await client.post(url, params=params)
            if response.status_code == 200 and response.text == "ok":
                _registered = True
                print(f"✅ 服务注册成功: {NACOS_SERVICE_NAME} -> {ip}:{port}")
            else:
                _registered = False
                print(f"⚠️ 服务注册失败: {response.text}")
    except Exception as e:
        _registered = False
        print(f"⚠️ Nacos连接失败（使用本地地址）: {e}")

async def send_heartbeat(ip: str, port: int):
    global _heartbeat_count, _registered
    try:
        async with httpx.AsyncClient(timeout=10.0) as client:
            url = f"http://{NACOS_SERVER}/nacos/v1/ns/instance/beat"
            params = {
                "serviceName": NACOS_SERVICE_NAME,
                "ip": ip,
                "port": port,
                "namespaceId": NACOS_NAMESPACE,
                "beat": '{"serviceName":"' + NACOS_SERVICE_NAME + '","ip":"' + ip + '","port":' + str(port) + ',"metadata":{"version":"1.0","language":"python"}}'
            }
            response = await client.put(url, params=params)
            if response.status_code == 200:
                _heartbeat_count += 1
                data = response.json()
                if data.get("code") == 1024:
                    print(f"🔄 心跳检测到服务已注销，重新注册...")
                    await register_service(ip, port)
                elif _heartbeat_count % 30 == 0:
                    print(f"💓 心跳正常 ({_heartbeat_count}次)")
    except Exception as e:
        _registered = False
        if _heartbeat_count % 10 == 0:
            print(f"⚠️ 心跳发送失败: {e}")

async def discover_java_agent() -> str:
    global _java_agent_url
    if _java_agent_url is not None:
        return _java_agent_url
    
    try:
        async with httpx.AsyncClient(timeout=10.0) as client:
            url = f"http://{NACOS_SERVER}/nacos/v1/ns/instance/list"
            params = {
                "serviceName": JAVA_AGENT_SERVICE_NAME,
                "namespaceId": NACOS_NAMESPACE,
                "healthyOnly": "true"
            }
            response = await client.get(url, params=params)
            if response.status_code == 200:
                data = response.json()
                if data.get("hosts"):
                    hosts = data["hosts"]
                    if hosts:
                        host = random.choice(hosts)
                        ip = host["ip"]
                        port = host["port"]
                        _java_agent_url = f"http://{ip}:{port}"
                        print(f"✅ 发现JavaAgent服务: {_java_agent_url}")
                        return _java_agent_url
                    else:
                        print(f"⚠️ 未找到健康的JavaAgent实例")
                else:
                    print(f"⚠️ JavaAgent服务未注册到Nacos")
            else:
                print(f"⚠️ 服务发现失败: {response.text}")
    except Exception as e:
        print(f"⚠️ Nacos连接失败（使用本地地址）: {e}")
    
    _java_agent_url = "http://localhost:8081"
    return _java_agent_url

async def start_heartbeat():
    print("💓 启动心跳线程...")
    while True:
        await asyncio.sleep(5)
        if _registered:
            await send_heartbeat("127.0.0.1", 8000)

def sync_discover_java_agent() -> str:
    global _java_agent_url
    if _java_agent_url is not None:
        return _java_agent_url
    
    try:
        import requests
        url = f"http://{NACOS_SERVER}/nacos/v1/ns/instance/list"
        params = {
            "serviceName": JAVA_AGENT_SERVICE_NAME,
            "namespaceId": NACOS_NAMESPACE,
            "healthyOnly": "true"
        }
        response = requests.get(url, params=params, timeout=10)
        if response.status_code == 200:
            data = response.json()
            if data.get("hosts"):
                hosts = data["hosts"]
                if hosts:
                    host = random.choice(hosts)
                    ip = host["ip"]
                    port = host["port"]
                    _java_agent_url = f"http://{ip}:{port}"
                    print(f"✅ 发现JavaAgent服务: {_java_agent_url}")
                    return _java_agent_url
        _java_agent_url = "http://localhost:8081"
        return _java_agent_url
    except Exception as e:
        print(f"⚠️ Nacos同步发现失败: {e}")
        _java_agent_url = "http://localhost:8081"
        return _java_agent_url
