# CDS-HOOKS-SERVER(NeXEHRS-POC)
このプロジェクトは「CDS Hooks(プロトタイプ版)実証」[NeXEHRS-POC](https://www.nexehrs-cpc.jp/)によるCDS実装検証モデルです。

CDS Hookは、ベンダーに依存しないリモート意思決定支援標準です。 このリポジトリは、CDS Hooks プロジェクトの仕様と Web サイトの両方として機能します。


# cds-hooks-proto

<center><img src="cds-server-next-docker/docs/img/cds-hook-img.png" width="70%" ></center> 

### 機能イメージ・処理フロー
<center><img src="cds-server-next-docker/docs/img/cds-hook-img0.png" width="70%" ></center> 

## [cds-hooks-client](./cds-hooks-client/README.md)
- 動作環境
- フォルダ構成
- 事前準備
- 実行コマンド


## [cds-hooks-server](./cds-server-next-docker/README.md)
- CDSHOOKサーバの開始方法
- 基本仕様・機能概要
- API仕様書
	- 共通仕様
	- /cds-service
	- /order-select
	- /order-sign

## [pddi-cds-service](./pddi-cds-service-proto/README.md)
- PDDI CDSサービス（Docker版）のインストール
	- 事前準備
	- インストール手順
- リクエストAPI
	- アクセスURL
	- リクエストヘッダ
	- リクエストボディ

