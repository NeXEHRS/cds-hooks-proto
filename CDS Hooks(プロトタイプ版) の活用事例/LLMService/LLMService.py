import os
import json
from flask import Flask, request, jsonify
from flask_cors import CORS
from dotenv import load_dotenv
import cohere
import openai
import google.generativeai as genai

# Flaskアプリケーションの初期化
app = Flask(__name__)
CORS(app)  # CORS（クロスオリジンリソース共有）を有効化

# .envファイルから環境変数を読み込む
load_dotenv()

# 各APIのキーを環境変数から取得
cohere_api_key = os.getenv("COHERE_API_KEY")
openai_api_key = os.getenv("OPENAI_API_KEY")
gemini_api_key = os.getenv("GEMINI_API_KEY")

# 設定ファイルからモデル情報を読み込む
CONFIG_PATH = "config.json"

def load_config():
    if os.path.exists(CONFIG_PATH):
        with open(CONFIG_PATH, "r", encoding="utf-8") as f:
            return json.load(f)
    return {
        "cohere_model": "command-r-plus",
        "openai_model": "gpt-4o-mini",
        "gemini_model": "gemini-2.0-flash"
    }

config = load_config()

# Cohere APIのクライアントを初期化（APIキーがある場合のみ）
cohere_client = cohere.Client(cohere_api_key) if cohere_api_key else None

# OpenAI APIのクライアントを初期化（APIキーがある場合のみ）
openai_client = openai.Client() if openai_api_key else None

# Gemini APIの設定（APIキーがある場合のみ）
if gemini_api_key:
    genai.configure(api_key=gemini_api_key)

# Cohereを使用して要約を生成する関数
def generate_summary_with_cohere(prompt):
    if not cohere_client:
        return "Cohere APIキーが設定されていません。"
    try:
        response = cohere_client.generate(
            model=config["cohere_model"],
            prompt=prompt,
            max_tokens=500,
            temperature=0.0
        )
        return response.generations[0].text.strip()
    except Exception as e:
        return f"Cohereエラー: {str(e)}"

# ChatGPT（OpenAI）を使用して要約を生成する関数
def generate_summary_with_chatgpt(prompt):
    if not openai_api_key:
        return "OpenAI APIキーが設定されていません。"
    try:
        response = openai_client.chat.completions.create(
            model=config["openai_model"],
            messages=[{"role": "user", "content": prompt}],
            max_tokens=500,
            temperature=0.0
        )
        return response.choices[0].message.content.strip()
    except Exception as e:
        return f"ChatGPTエラー: {str(e)}"

# Gemini（Google AI）を使用して要約を生成する関数
def generate_summary_with_gemini(prompt):
    if not gemini_api_key:
        return "Gemini APIキーが設定されていません。"
    try:
        model = genai.GenerativeModel(config["gemini_model"])
        generation_config = {
            "temperature": 0.0,
            "max_output_tokens": 500
        }
        response = model.generate_content(prompt, generation_config=generation_config)
        return response.text.strip()
    except Exception as e:
        return f"Geminiエラー: {str(e)}"

# LLMサービスのエンドポイント
@app.route("/llm-service/llm-service", methods=["POST"])
def pddi_cds_service():
    try:
        # クライアントからのリクエストを取得
        data = request.get_json()
        print("Received Hook Request:", data)
        question = data["context"]["question"]  # クエリの取得

        # 各APIを使用して要約を生成
        cohere_summary = generate_summary_with_cohere(question)
        chatgpt_summary = generate_summary_with_chatgpt(question)
        gemini_summary = generate_summary_with_gemini(question)

        # 3つの要約をそれぞれ個別のカードとしてレスポンスを作成
        response = {
            "cards": [
                {
                    "summary": "Command R+による回答",
                    "detail": cohere_summary,
                    "indicator": "info",
                    "source": {"label": "http://localhost:5000/llm-service/llm-service"}
                },
                {
                    "summary": "ChatGPTによる回答",
                    "detail": chatgpt_summary,
                    "indicator": "info",
                    "source": {"label": "http://localhost:5000/llm-service/llm-service"}
                },
                {
                    "summary": "Geminiによる回答",
                    "detail": gemini_summary,
                    "indicator": "info",
                    "source": {"label": "http://localhost:5000/llm-service/llm-service"}
                }
            ]
        }
        return jsonify(response)  # JSON形式でクライアントに返す
    except Exception as e:
        return jsonify({"error": str(e)}), 500  # エラーハンドリング

# アプリケーションのエントリポイント
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True, threaded=True)
