pddiCdsService インストール

１，Docker版のインストール

1.1　事前準備
　あらかじめDocker（Windowsで動作するDocker Desktopなど）がインストールされ、利用可能な状態になっていること。
　Dockerのインストール方法はWeb上にある解説記事を参照のこと。



1.2　インストール

（１）pddiCdsServiceの取得
フォルダpddiCdsServiceとDockerFileをダウンロードし、同じフォルダに置く。以下、このフォルダへの絶対パスを<Folder>と記す

（２）pddiCdsServiceのビルド実行
端末（Windowsの場合、PowerShellまたはコマンドプロンプト）を起動し、以下を実行する

$ cd <Folder>
$ docker pull almalinux
$ docker build -t pddi-cds-service ./

* WindowsのPowerShellやコマンドプロンプトの場合上記「$」を「>」に読み替える。以下も同様。

（３）pddiCdsServiceの作成、起動
 （２）に引き続き以下を実行する

$ docker run -d -p 18080:8080 --name service1 pddi-cds-server:latest

これにより、pddiCdsServiceはDockerのコンテナとして起動する。
pddiCdsServiceのAPIは1.3を参照のこと。

（４）動作中コンテナの停止、再起動
 （３）で起動したコンテナを停止する場合

$ docker stop service1

停止したコンテナを再起動する場合

$ docker start service1 



1.3 リクエストAPI
●Warfarin+NSAIDs, order-signの場合

POST http://<Host>:18080/pddi-cds/warfarin-nsaids-cds-sign
RequestBodyにはCDSHooksRequestをJSON形式で設定する

●Digoxin+Cyclosporine, order-signの場合

POST http://<Host>:18080/pddi-cds/digoxin-cyclosporine-cds-sign
RequestBodyにはCDSHooksRequestをJSON形式で設定する

ただし、<Host>は、pddiCdsServiceを組み込んだDockerを動かしているマシンのホスト名またはIPアドレス。CDSHooksRequest送信元がこのDockerを動かているマシンと同一であれば「localhost」を使用してもよい。
