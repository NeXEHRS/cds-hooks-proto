### ローカル環境でのビルド方法
ローカル環境に本プロジェクトを構築します。macOS,Windows,LinuxそれぞれのOSで作業は同じです。この場合もgitリポジトリは同じですが、Docker関連ファイルは利用せず、ダウンロードしたアーカイブに含まれる「cds-hook-server/」をルートディレクトリとしてプロジェクトをビルドします。

[Node.js](https://nodejs.org/en)実行環境は必須です。(本プロジェクトは18.xLTS版を利用しています。)
Node環境のインストール方法は[https://github.com/nodesource/distributions](https://github.com/nodesource/distributions)を参照し最新のLTS版を利用するようにしてください。（多くのWebサイトの方法は非推奨となっています。）　　
例えばUbuntu22.04LTSにNode18.xをインストールするには以下のようなコマンドを使います。
```bash
$ sudo apt-get update
$ sudo apt-get install -y ca-certificates curl gnupg
$ sudo mkdir -p /etc/apt/keyrings
$ curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key | sudo gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg
$ NODE_MAJOR=18
$ echo "deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_$NODE_MAJOR.x nodistro main" | sudo tee /etc/apt/sources.list.d/nodesource.list
$ sudo apt-get update
$ sudo apt-get install nodejs -y
```


1. Nodeバージョン確認
```bash
$node --version
v18.16.1
```

2. リポジトリのクローンを取得
```bash
$ git clone https://github.com/msis-net/cds-next-on-docker.git
```

3. cds-hook-server/に移動します。
```bash
$ cd cds-next-on-docker/cds-hook-server
[/cds-hook-server]> 
```
4. src/prisma/schema.prismaの「generator client.binaryTargets」をコメントアウトします。
```bash
generator client {
  provider = "prisma-client-js"
  //binaryTargets =  ["debian-openssl-3.0.x", "linux-arm64-openssl-3.0.x"]
  ↑↑
}
```

5. Next.jsのインストールとベースプロジェクトのビルド
```bash
-- package-lock.jsonを参照して依存パッケージをインストールします
[/cds-hook-server]> npm ci
※処理が完了するまで待機
-- 既存データベース(sqlLite)のgenerate
[/cds-hook-server]> cd src/
[/src]> npx prisma generate
-- 元のディレクトリに戻る
[/src]> cd ../
[/cds-hook-server]>
```

※generateでエラーが発生する場合は以下の２つのコマンドを実行
```bash
-- uuidライブラリ
[/cds-hook-server]> npm i --save-dev @types/uuid
-- prismaクライアント
[/cds-hook-server]> npm install @prisma/client@dev prisma@dev
```

6. サーバー起動
```bash
[/cds-hook-server]> npm run dev
> cds-hook@0.1.0 dev
> next dev

  ▲ Next.js 13.5.x
  - Local:        http://localhost:3000
  ...
-- http://localhost:3000へ接続してページを確認
-- 停止は「ctrl」+「C」
```
