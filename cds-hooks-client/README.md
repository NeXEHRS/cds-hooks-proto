### 実行方法

---

#### 動作環境
`java17`実行環境が必要です。

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

##### 実行モジュール/へ移動
```
cd ./実行モジュール/
```
#### cds-hooks-client.jarを起動
```
java -classpath ./config -jar cds-hooks-client.jar
```

