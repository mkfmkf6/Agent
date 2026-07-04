import os
import chromadb
from langchain_community.document_loaders import PyPDFLoader, Docx2txtLoader, TextLoader
try:
    from langchain.text_splitter import RecursiveCharacterTextSplitter
except ImportError:
    from langchain_text_splitters import RecursiveCharacterTextSplitter

DOCUMENTS_DIR = os.path.join(os.path.dirname(__file__), "documents")
CHROMA_DB_DIR = os.path.join(os.path.dirname(__file__), "chroma_db")
MODEL_CACHE_DIR = os.path.join(os.path.dirname(__file__), "models")

os.environ["TRANSFORMERS_CACHE"] = MODEL_CACHE_DIR
os.environ["HF_HOME"] = MODEL_CACHE_DIR
os.environ["HF_HUB_DISABLE_SYMLINKS_WARNING"] = "1"
os.environ["TRANSFORMERS_OFFLINE"] = "1"
os.environ["HF_HUB_OFFLINE"] = "1"

local_model_path = os.path.join(MODEL_CACHE_DIR, "models--sentence-transformers--all-MiniLM-L6-v2", "snapshots", "1110a243fdf4706b3f48f1d95db1a4f5529b4d41")

class KnowledgeBaseService:
    def __init__(self):
        self.embedding_function = None
        self.client = None
        self.vectorstore = None
        self.collection = None
        self.initialized = False

    def init_embedding(self):
        if self.embedding_function is not None:
            return True
        
        try:
            from langchain_huggingface import HuggingFaceEmbeddings
            self.embedding_function = HuggingFaceEmbeddings(
                model_name=local_model_path
            )
        except ImportError:
            from langchain_community.embeddings.sentence_transformer import SentenceTransformerEmbeddings
            self.embedding_function = SentenceTransformerEmbeddings(
                model_name=local_model_path
            )
        print(f"✅ 嵌入模型加载成功，路径: {local_model_path}")
        return True

    def init_client(self):
        if self.client is None:
            self.client = chromadb.PersistentClient(path=CHROMA_DB_DIR)
        return True

    def load_documents(self):
        documents = []
        if not os.path.exists(DOCUMENTS_DIR):
            return documents
        
        for filename in os.listdir(DOCUMENTS_DIR):
            filepath = os.path.join(DOCUMENTS_DIR, filename)
            if not os.path.isfile(filepath):
                continue
            
            try:
                if filename.endswith(".pdf"):
                    loader = PyPDFLoader(filepath)
                elif filename.endswith(".docx"):
                    loader = Docx2txtLoader(filepath)
                elif filename.endswith(".md") or filename.endswith(".txt"):
                    loader = TextLoader(filepath, encoding="utf-8")
                else:
                    continue
                
                docs = loader.load()
                for doc in docs:
                    doc.metadata["source"] = filename
                documents.extend(docs)
            except Exception as e:
                print(f"加载文档 {filename} 失败: {e}")
        
        return documents

    def split_documents(self, documents):
        text_splitter = RecursiveCharacterTextSplitter(
            chunk_size=500,
            chunk_overlap=50,
            length_function=len,
            separators=["\n\n", "\n", "。", "！", "？", "；", "、", " ", ""]
        )
        return text_splitter.split_documents(documents)

    def init_vectorstore(self):
        if not self.init_embedding():
            print("嵌入模型初始化失败，跳过知识库初始化")
            return
        
        if not self.init_client():
            print("数据库客户端初始化失败，跳过知识库初始化")
            return
        
        if os.path.exists(os.path.join(CHROMA_DB_DIR, "chroma.sqlite3")):
            from langchain_community.vectorstores import Chroma
            self.vectorstore = Chroma(
                persist_directory=CHROMA_DB_DIR,
                embedding_function=self.embedding_function
            )
            self.collection = self.client.get_collection("langchain")
            self.initialized = True
            print("✅ 向量数据库从本地加载完成")
            return
        
        documents = self.load_documents()
        if not documents:
            print("没有找到任何文档")
            return
        
        splits = self.split_documents(documents)
        print(f"文档切分完成，共 {len(splits)} 个文本块")
        
        from langchain_community.vectorstores import Chroma
        self.vectorstore = Chroma.from_documents(
            documents=splits,
            embedding=self.embedding_function,
            persist_directory=CHROMA_DB_DIR
        )
        self.collection = self.client.get_collection("langchain")
        self.initialized = True
        print("✅ 向量数据库初始化完成")

    def search(self, query, k=3):
        if not self.initialized:
            return []
        
        if self.vectorstore is None:
            try:
                from langchain_community.vectorstores import Chroma
                self.vectorstore = Chroma(
                    persist_directory=CHROMA_DB_DIR,
                    embedding_function=self.embedding_function
                )
            except Exception as e:
                print(f"加载向量数据库失败: {e}")
                return []
        
        try:
            results = self.vectorstore.similarity_search(query, k=k)
            return results
        except Exception as e:
            print(f"搜索失败: {e}")
            return []

    def search_with_score(self, query, k=3):
        if not self.initialized:
            return []
        
        if self.vectorstore is None:
            try:
                from langchain_community.vectorstores import Chroma
                self.vectorstore = Chroma(
                    persist_directory=CHROMA_DB_DIR,
                    embedding_function=self.embedding_function
                )
            except Exception as e:
                print(f"加载向量数据库失败: {e}")
                return []
        
        try:
            results = self.vectorstore.similarity_search_with_score(query, k=k)
            return results
        except Exception as e:
            print(f"搜索失败: {e}")
            return []

    def get_knowledge_context(self, query, k=3):
        if not self.initialized:
            return ""
        
        results = self.search_with_score(query, k=k)
        if not results:
            return ""
        
        context_parts = []
        for doc, score in results:
            source = doc.metadata.get("source", "未知来源")
            content = doc.page_content.strip()
            context_parts.append(f"【来源：{source}】\n{content}\n")
        
        return "\n".join(context_parts)

kb_service = KnowledgeBaseService()

def init_knowledge_base():
    try:
        kb_service.init_vectorstore()
    except Exception as e:
        print(f"知识库初始化失败: {e}")

def query_knowledge(query, k=3):
    return kb_service.get_knowledge_context(query, k=3)
