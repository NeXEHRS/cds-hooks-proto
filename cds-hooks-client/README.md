### 実行方法

---

#### 前提

`java`コマンドが使用できる環境であること。

`java17`でビルドしています。

#### フォルダ構成

以下の通りに配置してください。

- case1：リクエストファイル（Warfarin + NSAID）
- case2：リクエストファイル（Digoxin + Cyclosporine）

```
cds-hooks-client.jar
config
  └ application.properties
scenario
  ├ case1
  │  ├ scenario1.json
  │  ├ scenario2.json
  │  ├ scenario3.json
  │  ├ scenario4.json
  │  └ scenario5.json
  └ case2
  　  ├ scenario1.json
  　  ├ scenario2.json
  　  ├ scenario3.json
  　  └ scenario4.json

```

#### 事前準備

必要に応じて設定ファイル（`application.properties`）の以下の項目を修正してください。

- リクエストURL
  - discovery.request.url：CDS Discovery URL
  - case1.hooks.request.url：CDS Hooks Sever URL（Warfarin + NSAID）
  - case2.hooks.request.url：CDS Hooks Sever URL（Digoxin + Cyclosporine）
- シナリオデータの配置フォルダ
  - シナリオデータのファイル名は、「`scenario1.json`」～「`scenario5.json`」としてください。

#### コマンド

以下のコマンドを実行した後、 http://localhost:8080 にアクセスすると初期画面（Discoveryタブ）が表示されます。

```
java -classpath ./config -jar cds-hooks-client.jar
```

