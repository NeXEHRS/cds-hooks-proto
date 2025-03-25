# cds-hooks-client(python版)

このプロジェクトは、CDS Hooksサーバとのやり取りを行う Flask ベースの API サービスです。

---

## セットアップ手順

### 1. Python 環境のセットアップ

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

### 2. サーバーの起動

仮想環境を有効にした状態で、以下のコマンドを実行します。

```bash
python client.py
```

実行後、以下のエンドポイントにアクセスすると初期画面が表示されます。

```
http://localhost:5001
```

