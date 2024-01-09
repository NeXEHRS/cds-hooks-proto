

# cds-hooks-proto
<!-- Written by Msis(Akiyama) -->
## [cds-hooks-client](./cds-hooks-client/実行モジュール/実行方法.md)

## [cds-server-nect-docker](./cds-server-next-docker/README.md)
- CDSHOOKサーバの開始方法
	- [Docker仮想コンテナでのビルド方法](./cds-server-next-docker/docs/build_docker.md)
	- [ローカル環境でのビルド方法](./cds-server-next-docker/docs/build_localhost.md)
	- [接続テスト方法](./cds-server-next-docker/docs/build_test.md)
- 基本仕様・機能概要
	- [CDSサービスとDatabaseの管理](./cds-server-next-docker/docs/extention01.md)
	- [Discovary(/cds-selecctの例)](./cds-server-next-docker/docs/extention02.md)
	- [CDSサービスからのFHIRリソースの取得](./cds-server-next-docker/docs/extention03.md)
	- [CDSサービスの追加方法](./cds-server-next-docker/docs/extention04.md)
	- [他ホスト（CDS実行エンジン）へのリレー方法](./cds-server-next-docker/docs/extention05.md)
- API仕様書
	- [共通仕様](./cds-server-next-docker/docs/api_000.md)
	- /cds-service
		- [/cds-service（Didcovery）](./cds-server-next-docker/docs/api_001.md)
		- [/cds-service/static-patient-greeter（prefetchなし）](./cds-server-next-docker/docs/api_002.md)
		- [/cds-service/static-patient-greeter（prefetchあり）](./cds-server-next-docker/docs/api_003.md)
	- /order-select
		- [order-select/warfarin-nsaids-cds-select](./cds-server-next-docker/docs/api_004.md)
		- [/order-select/digoxin-cyclosporine-cds-select](./cds-server-next-docker/docs/api_005.md)
	- /order-sign
		- [order-sign/warfarin-nsaids-cds-sign](./cds-server-next-docker/docs/api_005.md)
		- [/order-sign/digoxin-cyclosporine-cds-sign](./cds-server-next-docker/docs/api_006.md)

## [pddi-cds-service-proto](./pddi-cds-service-proto/INSTALL-docker.txt)
