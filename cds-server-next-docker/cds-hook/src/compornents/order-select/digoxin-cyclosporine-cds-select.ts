import BaseInterface from '../interface/_interface-base'
import FhirAccess from '../interface/_fhir-access';
import { v4 as uuidv4 } from 'uuid';

export default class DigoxinCyclosporineCdsSelect extends BaseInterface
{
  init(): void {
    super.init();
  }

  async Execute( data:any, hook_id:string, url:string ){
    const cards:any = []
    let FHIR_access = false
    let patient:any = null
    let ermsg:any = null

    const patientid = data.context.patientId
    if( patientid === undefined )
      return {"summary":"contextデータにpatientidが見当たりません。(FHIRサーバに問合せできませんでした。)"}
    //console.log("patientid:", patientid )
  
    
    //patientリソース見つからない場合はFHIRServerから取得
    if( FHIR_access && 'fhirAuthorization' in data ){
      try{
        /*fhirから取得token利用(未実証)*/
        //const fhirserver = data.fhirServer
        //const url = fhirserver+"/Patient/"+patientid
        //const fhirAuthorization:any = data.fhirAuthorization
        //console.log("url:", url)
        //console.log("fhirAuthorization:>", fhirAuthorization)
        //const fhir = new FhirAccess(url, fhirAuthorization)
        //patient = await fhir.get_fhir()
        
        //本体ならBearer認証で送信する
        //FHIR_SERVERは.env環境設定ファイルに記載
        //const fhirserver = process.env.FHIR_SERVER

        
        /*ダミー認証（tokenなし）*/
        //const fhirserver = data.fhirServer
        //const url = fhirserver+"/Patient/"+patientid
        //const response = await fetch(url)
        //patient = await response.json()
        
      }catch(e){
        ermsg = "FHIREサーバーに関するAuthorization情報が無いためpatientリソースを取得できませんでした。"
      }
    }
    
    
    let card:any = {}
    if ( ermsg !== null ){
      card['summary']=ermsg
    }else{
      //サンプルデモデータ
      card =[
        {
          "uuid": uuidv4(),
          "summary": "ジゴキシン (ジゴキシン 0.2 MG 経口カプセル) とシクロスポリン (シクロスポリン 100 MG) の間の潜在的な薬物相互作用",
          "indicator": "warning",
          "detail": "ジゴキシン毒性のリスクが増加します。 リスクを評価し、必要に応じて措置を講じます。 \nジゴキシンの毒性は深刻な可能性があります。 臨床結果には、食欲不振、吐き気、嘔吐、視覚的変化、不整脈などが含まれる場合があります。 \nこの相互作用のメカニズムは、シクロスポリンによる P 糖タンパク質の阻害を介して媒介されるようです。 P-糖タンパク質は、ジゴキシン流出の主要な輸送体です。",
          "source": {
            "label": "潜在的な薬物間相互作用の臨床意思決定のサポート",
            "url": url
          },
          "suggestions": [
            {
              "label": "Consultation",
              "actions": [
                {
                  "type": "create",
                  "description": "ジゴキシン処方者とのコミュニケーションをリクエストする",
                  "resource": {
                    "resourceType": "ProcedureRequest",
                    "id": "70ef559c-a02d-463d-8505-9beb9c2629c3",
                    "status": "draft",
                    "intent": "order",
                    "code": {
                      "coding": [
                        {
                          "system": "http://snomed.info/sct",
                          "code": "11429006",
                          "display": "Consultation"
                        }
                      ],
                      "text": "Consultation"
                    },
                    "subject": {
                      "reference": "f001"
                    }
                  }
                }
              ]
            },
            {
              "label": "Cancel digoxin",
              "actions": [
                {
                  "type": "delete",
                  "description": "Discontinue digoxin order"
                }
              ]
            }
          ]
        },
        {
          "summary": "患者には過去 30 日以内に記録上のジゴキシンレベルがありません。",
          "indicator": "warning",
          "detail": "シクロスポリンの投与を開始すると、ジゴキシンレベルが上昇すると予想されます。 信頼できる血漿ジゴキシン濃度が正常範囲にない患者の場合は、リスクを上回る利益がある場合にのみ使用してください。 細心の注意と厳重な監視が必要です。",
          "source": {
            "label": "潜在的な薬物間相互作用の臨床意思決定のサポート",
            "url": url
          },
          "suggestions": [
            {
              "label": "Digoxin Level",
              "actions": [
                {
                  "type": "create",
                  "description": "シクロスポリンの投与開始から 24 時間以内にジゴキシントラフを注文する",
                  "resource": {
                    "resourceType": "ProcedureRequest",
                    "id": "79d64211-25f6-4e8c-babe-856816127d1b",
                    "status": "draft",
                    "intent": "order",
                    "code": {
                      "coding": [
                        {
                          "system": "http://snomed.info/sct",
                          "code": "269872007",
                          "display": "血清ジゴキシン測定"
                        }
                      ],
                      "text": "血清ジゴキシン測定"
                    },
                    "subject": {
                      "reference": "f001"
                    }
                  }
                }
              ]
            },
            {
              "label": "New Digoxin",
              "actions": [
                {
                  "type": "create",
                  "description": "新しいオーダーでジゴキシンの用量を先制的に減らす ",
                  "resource": {
                    "resourceType": "MedicationRequest",
                    "id": "3c428362-5646-4131-9f96-937a7df5dbf4",
                    "intent": "order",
                    "medicationCodeableConcept": {
                      "coding": [
                        {
                          "system": "http://www.nlm.nih.gov/research/umls/rxnorm",
                          "code": "315819",
                          "display": "Digoxin 0.125 MG"
                        }
                      ],
                      "text": "Digoxin 0.125 MG"
                    },
                    "subject": {
                      "reference": "f001"
                    }
                  }
                }
              ]
            }
          ],
          "links": [
            {
              "label": "ジゴキシン-シクロスポリン PDDI 知識成果物",
              "url": "http://hl7.org/fhir/ig/PDDI-CDS/derived-from#digoxin-cyclosporine-knowledge-artifact"
            },
            {
              "label": "(Dorian et al. Clin Invest Med 1988; 11(2):108-112) ",
              "url": "http://hl7.org/fhir/ig/PDDI-CDS/citation#dorian1988"
            },
            {
              "label": "(Dorian et al. Transplant Proc. 1987; 19(1):1825-1827)",
              "url": "http://hl7.org/fhir/ig/PDDI-CDS/citation#dorian#1987"
            }
          ]
        },
        {
          "summary": "100 日以内に患者は電解質と血清クレアチニンのレベルを検査されており、カリウム節約薬やループ利尿薬を服用していません。",
          "indicator": "info",
          "detail": "(カリウム: 3.6mEq/L および 2020-04-28)\n (マグネシウム: 0.8mmol/L および 2020-04-28)\n (カルシウム: 8.6mg/dL および 2020-04-28)\n",
          "source": {
            "label": "潜在的な薬物間相互作用の臨床意思決定のサポート",
            "url": url
          },
          "suggestions": [
            {
              "label": "セラム・クレアチン",
              "actions": [
                {
                  "type": "create",
                  "description": "血清クレアチニンの注文",
                  "resource": {
                    "resourceType": "ProcedureRequest",
                    "id": "85d53af2-22ec-4441-8ecf-f71879779f2a",
                    "status": "draft",
                    "intent": "order",
                    "code": {
                      "coding": [
                        {
                          "system": "http://snomed.info/sct",
                          "code": " 313822004",
                          "display": "補正された血清クレアチニン"
                        }
                      ],
                      "text": "セラム・クレアチン"
                    },
                    "subject": {
                      "reference": "f001"
                    }
                  }
                }
              ]
            },
            {
              "label": "電解質パネル",
              "actions": [
                {
                  "type": "create",
                  "description": "電解液パネルのご注文",
                  "resource": {
                    "resourceType": "ProcedureRequest",
                    "id": "8c6514e3-1d47-47b6-87b3-fc53ce8f6eb2",
                    "status": "draft",
                    "intent": "order",
                    "code": {
                      "coding": [
                        {
                          "system": "http://snomed.info/sct",
                          "code": "271236005",
                          "display": "血清カリウム値"
                        },
                        {
                          "system": "http://snomed.info/sct",
                          "code": "312475002",
                          "display": "血漿マグネシウムレベル"
                        },
                        {
                          "system": "http://snomed.info/sct",
                          "code": "390963002",
                          "display": "血漿カルシウム濃度"
                        }
                      ],
                      "text": "電解質パネル"
                    },
                    "subject": {
                      "reference": "f001"
                    }
                  }
                }
              ]
            }
          ],
          "links": [
            {
              "label": "ジゴキシン-シクロスポリン PDDI 知識成果物",
              "url": "http://hl7.org/fhir/ig/PDDI-CDS/derived-from#digoxin-cyclosporine-knowledge-artifact"
            },
            {
              "label": "(Lip et al. Postgrad Med J. 1993; 69(811):337)",
              "url": "http://hl7.org/fhir/ig/PDDI-CDS/citation#lip1993"
            },
            {
              "label": "(Digoxin-FDA [prescribing information] NDA 20405/S-004)",
              "url": "http://hl7.org/fhir/ig/PDDI-CDS/citation#nda20405"
            }
          ]
        }
      ]
    }
    
    return card
  }
}