## Discovary(/cds-selecct)
###Discovery
CDSクライアントはCDSサービスに対して公開されているDeicoveryエンドポイントを参照します。Discovery エンドポイントには、CDS サービスの説明、いつ呼び出す必要があるか、プリフェッチが要求されるデータなどの情報が含まれます。
```bash
GET https://example.com/cds-services
```
### リクエストサンプル
>[GET] curl --location 'http://localhost:3000/cds-services'

### レスポンスサンプル

※Service(enable=true)レコードから生成されたDiscovaryデータが返却されます。

```bash
{
    "services": [
        {
            "hook": "patient-view",
            "title": "静的 CDS サービスの例",
            "description": "静的なカードのセットを返す CDS サービスの例",
            "id": "static-patient-greeter",
            "prefetch": {
                "patient": "Patient/{{context.patientId}}"
            }
        },
        {
            "hook": "order-select",
            "title": "ワルファリン NSAID の推奨事項",
            "description": "NeXTHERS POC",
            "id": "warfarin-nsaids-cds-select",
            "prefetch": {
                "item1": "Patient?_id={{context.patientId}}",
                "item2": "MedicationRequest?patient={{context.patientId}}&authoredon={{datetime}}",
                "item3": "MedicationAdministration?patient={{context.patientId}}&effective-time={{datetime}}",
                "item4": "MedicationDispense?patient={{context.patientId}}&whenhandedover={{datetime}}",
                "item5": "MedicationStatement?patient={{context.patientId}}&effective={{datetime}}",
                "item6": "Condition?patient={{context.patientId}}"
            }
        },
        {
            "hook": "order-sign",
            "title": "ワルファリン NSAID の推奨事項",
            "description": "NeXTHERS POC",
            "id": "warfarin-nsaids-cds-sign",
            "prefetch": {
                "item1": "Patient?_id={{context.patientId}}",
                "item2": "MedicationRequest?patient={{context.patientId}}&authoredon={{datetime}}",
                "item3": "MedicationAdministration?patient={{context.patientId}}&effective-time={{datetime}}",
                "item4": "MedicationDispense?patient={{context.patientId}}&whenhandedover={{datetime}}",
                "item5": "MedicationStatement?patient={{context.patientId}}&effective={{datetime}}",
                "item6": "Condition?patient={{context.patientId}}"
            }
        },
        {
            "hook": "medication-prescribe",
            "title": "Warfarin　NSAID",
            "description": "NexTHRER POC",
            "id": "warfarin-nsaids-medication-prescribe",
            "prefetch": {
                "patient": "Patient/{{context.patientId}}",
                "MedicationRequest": "MedicationRequest?Patient/{{context.patientId}}&authoredon/{{datetime}}",
                "MedicationAdministration": "MedicationAdministration?Patient/{{context.patientId}}&effective-time/{{datetime}}",
                "MedicationDispense": "MedicationDispense?Patient/{{context.patientId}}&whenhandedover={{datetime}}",
                "MedicationStatement": "MedicationDispense?Patient/{{context.patientId}}&effective={{datetime}}",
                "Condition": "Condition?Patient/{{context.patientId}}"
            }
        },
        {
            "hook": "order-select",
            "title": "ジゴキシン シクロスポリンワルファリン NSAID の推奨事項",
            "description": "NexTHERS-POC",
            "id": "digoxin-cyclosporine-cds-select",
            "prefetch": {
                "item1": "Patient?_id={{context.patientId}}",
                "item2": "MedicationRequest?patient={{context.patientId}}&authoredon={{datetime}}",
                "item3": "MedicationAdministration?patient={{context.patientId}}&effective-time={{datetime}}",
                "item4": "MedicationDispense?patient={{context.patientId}}&whenhandedover={{datetime}}",
                "item5": "MedicationStatement?patient={{context.patientId}}&effective={{datetime}}",
                "item6": "Condition?patient={{context.patientId}}"
            }
        },
        {
            "hook": "order-sign",
            "title": "ジゴキシン シクロスポリンワルファリン NSAID の推奨事項",
            "description": "NexTHERS-POC",
            "id": "digoxin-cyclosporine-cds-sign",
            "prefetch": {
                "item1": "Patient?_id={{context.patientId}}",
                "item2": "MedicationRequest?patient={{context.patientId}}&authoredon={{datetime}}",
                "item3": "MedicationAdministration?patient={{context.patientId}}&effective-time={{datetime}}",
                "item4": "MedicationDispense?patient={{context.patientId}}&whenhandedover={{datetime}}",
                "item5": "MedicationStatement?patient={{context.patientId}}&effective={{datetime}}",
                "item6": "Condition?patient={{context.patientId}}"
            }
        }
    ]
}
```
### レスポンス一覧
| キー | 任意性 | タイプ | 説明 |
| --- | --- | --- | --- |
| hook | 必須 | string | このサービスを呼び出すフック。 |
| title | 推奨 | string | このサービスのわかりやすい名前。 |
| description | 必須 | string | このサービスの説明。 |
| id | 必須 | string | このサービスへの URL の {id} 部分。{baseUrl}/cds-services/{id} |
| prefetch | オプション | object | このサービスが各サービス呼び出しで実行および提供するように CDS クライアントに要求している FHIR クエリのキーと値のペアを含むオブジェクト。キーは要求されるデータのタイプを説明する文字列で、値はFHIR クエリを表す文字列です。 |
| usageRequirements | オプション | object | この CDS サービスを使用するための前提条件についての人にわかりやすい説明。 |
