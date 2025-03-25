from flask import Flask, request, render_template, jsonify
import requests
import uuid

app = Flask(__name__)

@app.route('/')
def home():
    return render_template('index.html')

@app.route('/submit', methods=['POST'])
def submit():
    try:
        data = request.json
        input_text = data.get('input-text', '')
        summary_length = data.get('summary-length', 300)  # デフォルト値300

        # 数値変換
        try:
            summary_length = int(summary_length)
        except ValueError:
            return jsonify({"error": "要約文字数は数値で指定してください。"}), 400

        hook_instance = str(uuid.uuid4())
        hook_resource = {
            "hook": "llm-service",
            "hookInstance": hook_instance,
            "context": {
                "question": f"{input_text} この内容を{summary_length}文字以内でまとめてください。"
            }
        }

        api_url = "http://localhost:3000/llm-service/llm-service"
        response = requests.post(api_url, json=hook_resource)
        response.raise_for_status()  # HTTPエラーが発生した場合、例外を発生させる

        result = response.json()
        return jsonify(result)

    except requests.exceptions.RequestException as e:
        return jsonify({"error": f"外部APIエラー: {str(e)}"}), 500

    except Exception as e:
        return jsonify({"error": f"サーバー内部エラー: {str(e)}"}), 500

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5001)
