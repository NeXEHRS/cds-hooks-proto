# LLM Service

このプロジェクトは、Cohere、OpenAI、Google Gemini を利用してテキスト要約を行う Flask ベースの API サービスです。

## 特徴
- **Flask** を使用した軽量な API サーバー
- **Cohere、ChatGPT（OpenAI）、Gemini（Google AI）** の3つのモデルを活用

---

## セットアップ手順

### 1. 必要なファイルを準備

このリポジトリには `.env` ファイルは含まれていません。  
APIキーを設定するために、プロジェクトのルートに `.env` ファイルを作成し、以下の内容を記入してください。

```env
COHERE_API_KEY=your-cohere-api-key
GEMINI_API_KEY=your-gemini-api-key
OPENAI_API_KEY=your-openai-api-key
```

> ⚠ **注意**: `.env` には機密情報が含まれるため、GitHub にアップロードしないでください。

### 2. Python 環境のセットアップ

Python 3.10 以上がインストールされていることを確認してください。

#### **仮想環境の作成と依存パッケージのインストール**

```bash
python -m venv venv  # 仮想環境を作成
source venv/bin/activate  # Mac/Linux
venv\Scripts\activate  # Windows
pip install -r requirements.txt  # 必要なライブラリをインストール
```

#### **仮想環境が有効な場合の見え方**
仮想環境を有効にすると、ターミナルのプロンプトが以下のようになります。

```bash
(venv) user@machine:~/your_project$
```

この `(venv)` の表示がある間は仮想環境が有効です。

#### **仮想環境を無効にする**
仮想環境を無効にする場合は、以下のコマンドを実行します。

```bash
deactivate
```

仮想環境を無効にすると、ターミナルの `(venv)` の表示が消えます。

---

### 3. サーバーの起動

仮想環境を有効にした状態で、以下のコマンドを実行します。

```bash
python LLMService.py
```

実行後、以下のエンドポイントでAPIが動作します。

```
http://localhost:5000/llm-service/llm-service
```

