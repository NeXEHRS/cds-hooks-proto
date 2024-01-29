## CDSサービスの追加方法
このプロジェクトはNext.jsを基盤として実装されており、CDSサービスを追加するには以下の5つのステップで追加できるようになります。　
特徴として

新しいhookサービスを追加するのは簡単ですが、プログラム本体の内容は相当の書き込みやノウハウが必要になります。ここではサービスを追加しRestAPIによるレスポンスが戻るようになるまでを主に解説しています。


プログラムを追加する手順（括弧内は解説の為に利用する仮名称）
- [x] CDSサービス名を決める（「<span style="color: blue; ">demo-hook</span>」）
- [x] リクエストコンテキストパス名を決める（「<span style="color: red; ">process-test</span>」）
- [ ] src/に必要なフォルダとファイルを配備する
- [ ] プログラムをコーディングする
- [ ] 追加したプログラムをインタフェースに追加する
- [ ] テストする
- [ ] データベースに新しいサービスを登録して使えるようにする

#### src/に必要なフォルダとファイルを配備する

プロジェクトフォルダは以下のような構造になっています。
- cds-next-on docker/
    - cds-hook/
        - （略）
        - src/
            - app/
                - [sluug]/
                - <span style="color: skyblue; ">cds-services/
                    - route.ts
                    - [slug]/
                        - route.ts
                - <span style="color: skyblue; ">order-select/
                    - route.ts
                    - [slug]/
                        - route.ts
                - <span style="color: skyblue; ">order-sgin/
                    - route.ts
                    - [slug]/
                        - route.ts
                - <span style="color: blue; ">demo-hook/
                    - route.ts
                    - [slug]/
                        - route.ts
                - favicon.ico
                - globals.css
                - layout.tsx
                - page.tsx
            - compornents/
                - interfaces/
                    - <span style="color: magenta; ">_interfaces-list.ts
                - <span style="color: plum; ">cds-services/
                    - patient-greeter.ts
                    - static-patient-greeter.ts
                - <span style="color: plum; ">order-select/
                    - digoxin-cyclosporine-cds-select.ts
                    - warfarin-nsaids-cds-select.ts
                - <span style="color: plum; ">order-sgin/
                    - digoxin-cyclosporine-cds-sign.ts
                    - warfarin-nsaids-cds-select.ts
                - <span style="color: red; ">demo-hook/
                    - process-test.s
            - (略)

デフォルトではこのような構造とファイルが配置されていますが、よく見ると「app/」フォルダと「compornents/」の両方にhookサービス名と同じフォルダが存在しているのが判ります。

また、「app/」以下の構造はフォルダ名が違うだけで、その内容はどれも同じように見えます。

そして、「compornents/」以下には「app/」内と同じフォルダ名が配置してあり、その下にはhook_idのようなファイルがあります。

Next.jsの経験者や、感のいい方なら既にお分かりかもしれませんが、

「compornents/<hook名>/<hook_id>.ts」

という配置で新しいHookサービスの追加ができるようになっています。
プログラムの詳細は「<hook_id>.ts」ファイル内に書き込みます。言語はTypeScriptです。

「app/」以下にも「compornents/」以下に配備したものと同じ「<hook名>/」フォイルダが必要ですが、中身は他のフォルダと同じで構いません。

「app/」以下はユーザーからのリクエストが発生した際、そのURLコンテキストパスを確認し、それを「compornents/」以下のプログラムに引き継ぐ働きを行います。また、引き継いだプログラムからカード配列をユーザーに返却するのみの実装なので、機能は他の内容と同じで問題ありません。

また、app/」以下ではCDS実行エンジンによる処理が他ホストで行わている場合も、受け取ったデータをCDS実行エンジンのあるホストにリレーします。その場合は、そこから受け取ったカード情報をユーザーに返却するので、「compornents/」への引継ぎは行いません。（※CDSエンジンの登録はDiscoveryデータベースで行います。）


なので、青と赤に示した様に

- app/
    - <span style="color: blue; ">demo-hook/
         - route.ts
            - [slug]/
                - route.ts

- compornents/
    - <span style="color: red; ">demo-hook/
        - process-testt.ts

のように配置します。
「app/demo-hook/」のファイルは他と同じ（コピー）でかまいません。
「compornents/demo-hook/process-test.ts」にはプログラム本体を書き込んでゆきます。

> process-test.tsへプログラムを書き込む

process-test.tsには、他プログラムらファイルを参考にごく簡単なプログラムを書いてみます。

[重要]class名をキャメルケースに変換してください。

※プログラムではスコア(スネーク)ケースをキャメルケースに変換してプログラムを実行するように設定されています。
process-test(スコアケース) → ProcessTest(キャメルケース)

```bash
import BaseInterface from '../interface/_interface-base'

export default class ProcessTest extends BaseInterface
{
  init(): void {
    super.init();
  }

  async Execute( data:any, hook_id:string, url:string ){
    const cards:any = []
    
    let card:any = {
      "test":"これはテストです。",
      "url":url 
    }
    
    cards.push(card)
    return cards
  }
}

※「cards :[ {"test":"これはテストです。"} ]」という簡単なJSONが返却されるという事になります。
```

> 追加したプログラムをインタフェースに「interfaces/_interfaces-list.ts」追記します。

このプログラムが使えるようにするには「interfaces/_interfaces-list.ts」に登録します。
```bash
import ClassInterface from "./_interface_test"
import PatientGreeter from "../cds-services/patient-greeter"
import StaticPatientGreeter from "../cds-services/static-patient-greeter"
import WarfarinNsaidsCdsSelect from "../order-select/warfarin-nsaids-cds-select"
import WarfarinNsaidsCdsSign from "../order-sign/warfarin-nsaids-cds-sign"
import DigoxinCyclosporineCdsSelect from "../order-select/digoxin-cyclosporine-cds-select"
import DigoxinCyclosporineCdsSign from "../order-sign/digoxin-cyclosporine-cds-sign"
import ProcessTest from "../demo-hook/process-test"　←追加

const SelectList: any = {
    ClassInterface,
    PatientGreeter,
    StaticPatientGreeter,
    WarfarinNsaidsCdsSelect,
    WarfarinNsaidsCdsSign,
    DigoxinCyclosporineCdsSelect,
    DigoxinCyclosporineCdsSign,
    ProcessTest ←追加
}

export default SelectList

```
> http://{{serverAddress}}:3000/demo-hook/process-test に接続してテストしてみます。

postmanなどで確認してみてください。このように新しいHookサービスが正常に動作しました。

```bash
{
    "cards": [
        {
            "test": "これはテストです。",
            "url": "http://localhost:3000/demo-hook/process-test"
        }
    ]
}
```
最後にユーザーがこのこのフックを呼び出せるようにDatabaseに項目を追加しDiscoveryのサービスリストに表示されるようになれば完了です。

実際のプログラムはもっと複雑なはずです。Nextの資料等を参考に必要なHookを追加してください。

