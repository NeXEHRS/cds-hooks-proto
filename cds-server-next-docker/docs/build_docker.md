# Docker仮想コンテナでのビルド方法
Docker仮想コンテナ上にプロジェクトをビルドします。
この仮想コンテナにはNode.js実行環境（[Docker Node image](https://hub.docker.com/_/node/)）を基盤とした**Next.js**フレームワークで構築されています。

利用するにはあらかじめ[Docker](https://www.docker.com/)利用環境がインストールされている必要があります。

1. リポジトリのクローンを取得
```bash
$ git clone https://github.com/msis-net/cds-next-on-docker.git
```

2. プロジェクトのDockerコンテナの作成と起動
```bash
$ cd cds-next-on-docker
(Linux/mac)$ docker compose up --build -d
(Win)$ docker-compose up --build -d
```

3. 作成したDocker仮想コンテナのIDを確認しコンテナに入る
```bash
コンテナIDを確認
$ docker ps -a
CONTAINER ID   IMAGE               COMMAND                
c0558962e2c6   cds-next-on-docker-hook   "docker-entrypoint.s…"  
...

コンテナIDを使ってコンテナに入る(4.コンテナ内での作業参照)
$ docker exec -it c0558962e2c6 bash
root@c0558962e2c6:/cds-hook#
```

4. （コンテナ内での作業）Next.jsのインストールとベースプロジェクトのビルド
```bash
-- package-lock.jsonを参照して依存パッケージをインストールします
root@*:/cds-hook# npm ci

-- uuidが利用できるように以下のコマンドを実行
root@*:/cds-hook# npm i --save-dev @types/uuid

-- prismaクライアントをインストール
root@*:/cds-hook# npm install @prisma/client@dev prisma@dev

-- 既存データベース(sqlLite)のgenerate
root@*:/cds-hook# cd src/
root@*:/cds-hook/src# npx prisma generate
root@*:/cds-hook/src# cd ../ 
root@*:/cds-hook#
```

5. サーバー起動テスト(コンテナ内)
```bash
root@*:/cds-hook# npm run dev
> cds-hook@0.1.0 dev
> next dev

  ▲ Next.js 13.5.x
  - Local:        http://localhost:3000
  ...
- http://localhost:3000へ接続してページが表示される事を確認
- 確認できたら「ctrl」+「C」でサーバーを一旦停止
```

6. Docker仮想コンテナ起動時に同時に開始するように設定(コンテナ外)

```bash
コンテナから出る
root@*:/cds-hook# exit
[/cds-next-on-docker]>

docker-compose.ymlの内容を一部修正
#command: /bin/sh -c 'npm run dev'
↓
command: /bin/sh -c 'npm run dev' 
```

7. 起動確認：仮想コンテナをデーモンモード(-d)で起動(コンテナ外)
```bash
 以後はこのコマンドで実行します
[/cds-next-on-docker]> docker-compose up -d 
[+] Running 1/1
 ✔ Container cds-next-on-docker-hook-1  Started

 - http://localhost:3000へ接続してページが表示される事を確認
確認できたらコンテナを停止する
[/cds-next-on-docker]> docker-compose stop
[+] Stopping 1/1
 ✔ Container cds-next-on-docker-hook-1  Stopped