import BaseInterface from '../interface/_interface-base'
import FhirAccess from '../interface/_fhir-access';
import { v4 as uuidv4 } from 'uuid';

export default class WarfarinNsaidsCdsSign extends BaseInterface
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
          "summary": "Potential Drug-Drug Interaction between warfarin (Warfarin Sodium 0.5 MG Oral Tablet) and NSAID (Ketorolac Tromethamine 10 MG Oral Tablet).",
          "detail": "Increased risk of bleeding. \nBleeding is a serious potential clinical consequence because it can result in death, life-threatening hospitalization, and disability. \nNon-steroidal anti-inflammatory drugs (NSAIDs) have antiplatelet effects which increase the bleeding risk when combined with oral anticoagulants such as warfarin. The antiplatelet effect of NSAIDs lasts only as long as the NSAID is present in the circulation, unlike aspirin�fs antiplatelet effect, which lasts for up to 2 weeks after aspirin is discontinued. NSAIDs also can cause peptic ulcers and most of the evidence for increased bleeding risk with NSAIDs plus warfarin is due to upper gastrointestinal bleeding (UGIB). \nunknown. \n unknown.",
          "indicator": "warning",
          "source": {
              "label": "Warfarin-NSAIDs clinical decision support algorithm",
              "url": url
          },
          "suggestions": [
              {
                  "label": "Assess risk and take action if necessary.",
                  "actions": [
                      {
                          "type": "delete",
                          "description": "If the NSAID is being used as an analgesic or antipyretic, it would be prudent to use an alternative such as acetaminophen. In some people, acetaminophen can increase the anticoagulant effect of warfarin, so monitor the INR if acetaminophen is used in doses over 2 g/day for a few days. For more severe pain consider short-term opioids in place of the NSAID."
                      }
                  ]
              },
              {
                  "label": "Substitute NSAID (Ketorolac Tromethamine 10 MG Oral Tablet) with APAP (Acetaminophen 325 MG Oral Tablet).",
                  "actions": [
                      {
                          "type": "create",
                          "description": "Order for APAP <2g per day (APAP 500 mg every 4-6 hours prn).",
                          "resource": {
                              "resourceType": "MedicationRequest",
                              "id": "9694a60c-59c2-496a-998e-acba5affd225",
                              "status": "draft",
                              "intent": "order",
                              "doNotPerform": false,
                              "medicationCodeableConcept": {
                                  "coding": [
                                      {
                                          "system": "http://www.nlm.nih.gov/research/umls/rxnorm",
                                          "code": "313782",
                                          "display": "Acetaminophen 325 MG Oral Tablet"
                                      }
                                  ],
                                  "text": "Acetaminophen 325 MG Oral Tablet"
                              },
                              "subject": {
                                  "identifier": {
                                      "value": "f101"
                                  }
                              }
                          }
                      }
                  ]
              },
              {
                  "label": "Substitute NSAID (Ketorolac Tromethamine 10 MG Oral Tablet) with APAP (Acetaminophen 500 MG Oral Tablet).",
                  "actions": [
                      {
                          "type": "create",
                          "description": "Order for APAP <2g per day (APAP 500 mg every 4-6 hours prn).",
                          "resource": {
                              "resourceType": "MedicationRequest",
                              "id": "6ba290f2-00ce-4875-ae01-66f408fb09f2",
                              "status": "draft",
                              "intent": "order",
                              "doNotPerform": false,
                              "medicationCodeableConcept": {
                                  "coding": [
                                      {
                                          "system": "http://www.nlm.nih.gov/research/umls/rxnorm",
                                          "code": "198440",
                                          "display": "Acetaminophen 500 MG Oral Tablet"
                                      }
                                  ],
                                  "text": "Acetaminophen 500 MG Oral Tablet"
                              },
                              "subject": {
                                  "identifier": {
                                      "value": "f101"
                                  }
                              }
                          }
                      }
                  ]
              }
          ]
        },
        {
          "uuid": uuidv4(),
          "summary": "Patient is not taking a proton pump inhibitor or misoprostol.",
            "detail": "Proton pump inhibitors and misoprostol may reduce the risk of UGIB in patients receiving NSAIDs and warfarin.",
            "indicator": "critical",
            "source": {
                "label": "Warfarin-NSAIDs clinical decision support algorithm",
                "url": url
            },
            "suggestions": [
                {
                    "label": "Use only if benefit outweighs risk."
                }
            ]
        },
        {
          "uuid": uuidv4(),
          "summary": "Patient is 65 y/o or does have a history of upper gastrointestinal bleed (\"Acute duodenal ulcer with hemorrhage\" and 2023-12-01).",
            "detail": "Patients with a history of UGIB or peptic ulcer may have an increased risk of UGIB from this interaction. The extent to which older age is an independent risk factor for UGIB due to these interactions is not firmly established, but UGIB in general is known to increase with age.",
            "indicator": "warning",
            "source": {
                "label": "Warfarin-NSAIDs clinical decision support algorithm",
                "url": url
            },
            "suggestions": [
                {
                    "label": "Use only if benefit outweighs risk."
                }
            ]
        },
        {
          "uuid": uuidv4(),  
          "summary": "Patient is concomitantly taking high dose or multiple NSAIDs (Ketorolac Tromethamine 10 MG Oral Tablet).",
            "detail": "Both corticosteroids and aldosterone antagonists have been shown to subsetantially increase the risk of UGIB in patients on NSAIDs, with relative risks of 12.8 and 11 respectively compared to a risk of 4.3 with NSAIDs alone (Masclee et al. Gastroenterology 2014; 147:784-92.)",
            "indicator": "warning",
            "source": {
                "label": "Warfarin-NSAIDs clinical decision support algorithm",
                "url": url
            },
            "suggestions": [
                {
                    "label": "Use only if benefit outweighs risk."
                }
            ]
        }
      ]


      /*サンプルデモデータ
      card =[
        {
          "uuid": uuidv4(),
          "summary": "ワルファリン (ワルファリンナトリウム 0.5 MG 経口錠剤) と NSAID (ケトロラック トロメタミン 10 MG 経口錠剤) の間の潜在的な薬物相互作用。",
          "indicator": "warning",
          "detail": "出血のリスクが増加します。 \n出血は、死亡、生命を脅かす入院、障害を引き起こす可能性があるため、重大な臨床結果を引き起こす可能性があります。 \n非ステロイド性抗炎症薬 (NSAID) には抗血小板作用があり、ワルファリンなどの経口抗凝固薬と組み合わせると出血リスクが高まります。 NSAID の抗血小板効果は、アスピリンの抗血小板効果がアスピリン中止後 2 週間持続するのとは異なり、NSAID が循環中に存在する限り持続します。 NSAID は消化性潰瘍を引き起こす可能性もあり、NSAID とワルファリンの併用による出血リスクの増加に関する証拠のほとんどは上部消化管出血 (UGIB) によるものです。",
          "source": {
            "label": "ワルファリン-NSAIDs 臨床意思決定支援アルゴリズム",
            "url": url
          },
          "suggestions": [
            {
              "label": "リスクを評価し、必要に応じて措置を講じます。",
              "actions": [
                {
                  "type": "delete",
                  "description": "NSAID が鎮痛薬または解熱薬として使用されている場合は、アセトアミノフェンなどの代替薬を使用するのが賢明です。 人によっては、アセトアミノフェンがワルファリンの抗凝固作用を高める可能性があるため、アセトアミノフェンを 2 g/日を超える用量で数日間使用する場合は、INR を監視してください。 より重度の痛みの場合は、NSAID の代わりに短期オピオイドを検討してください。"
                }
              ]
            },
            {
              "label": "NSAID (ケトロラック トロメタミン 10 MG 経口錠剤) を APAP (アセトアミノフェン 325 MG 経口錠剤) に置き換えます。",
              "actions": [
                {
                  "type": "create",
                  "description": "APAP 1 日あたり 2g 未満 (APAP 500 mg を 4 ～ 6 時間ごとに) で注文してください。",
                  "resource": {
                    "resourceType": "MedicationRequest",
                    "id": "3f6b9b43-9354-4512-9883-a915417e2936",
                    "intent": "order",
                    "medicationCodeableConcept": {
                      "coding": [
                        {
                          "system": "http://www.nlm.nih.gov/research/umls/rxnorm",
                          "code": "313782",
                          "display": "アセトアミノフェン 325 MG 経口錠"
                        }
                      ],
                      "text": "アセトアミノフェン 325 MG 経口錠"
                    },
                    "subject": {
                      "reference": "f101"
                    }
                  }
                }
              ]
            },
            {
              "label": "NSAID (ケトロラック トロメタミン 10 MG 経口錠剤) を APAP (アセトアミノフェン 500 MG 経口錠剤) に置き換えます。",
              "actions": [
                {
                  "type": "create",
                  "description": "APAP 1 日あたり 2g 未満 (APAP 500 mg を 4 ～ 6 時間ごとに) で注文してください。",
                  "resource": {
                    "resourceType": "MedicationRequest",
                    "id": "d54e75a7-90ce-4ee7-90b6-63333c052648",
                    "intent": "order",
                    "medicationCodeableConcept": {
                      "coding": [
                        {
                          "system": "http://www.nlm.nih.gov/research/umls/rxnorm",
                          "code": "198440",
                          "display": "アセトアミノフェン 500 MG 経口錠"
                        }
                      ],
                      "text": "アセトアミノフェン 500 MG 経口錠"
                    },
                    "subject": {
                      "reference": "f101"
                    }
                  }
                }
              ]
            }
          ]
        },
        {
          "summary": "患者はプロトンポンプ阻害剤またはミソプロストールを服用していません。",
          "indicator": "critical",
          "detail": "プロトンポンプ阻害剤とミソプロストールは、NSAID とワルファリンを受けている患者における UGIB のリスクを軽減する可能性があります。",
          "source": {
            "label": "ワルファリン-NSAIDs 臨床意思決定支援アルゴリズム",
            "url": url
          },
          "suggestions": [
            {
              "label": "利益がリスクを上回る場合にのみ使用してください。"
            }
          ]
        },
        {
          "summary": "患者は65歳、または上部消化管出血の病歴がある（「出血を伴う急性十二指腸潰瘍」および2020年3月1日）。",
          "indicator": "warning",
          "detail": "UGIB または消化性潰瘍の病歴のある患者は、この相互作用により UGIB のリスクが増加する可能性があります。 これらの相互作用により、高齢がUGIBの独立した危険因子となる程度はしっかりと確立されていませんが、一般にUGIBは年齢とともに増加することが知られています。",
          "source": {
            "label": "ワルファリン-NSAIDs 臨床意思決定支援アルゴリズム",
            "url": url
          },
          "suggestions": [
            {
              "label": "利益がリスクを上回る場合にのみ使用してください。"
            }
          ]
        },
        {
          "summary": "患者は全身性コルチコステロイド、アルドステロン拮抗薬、または高用量または複数のNSAIDを併用していない。",
          "indicator": "info",
          "detail": "コルチコステロイドとアルドステロン拮抗薬はどちらも、NSAIDs を服用している患者の UGIB のリスクを実質的に増加させることが示されており、NSAIDs 単独の場合のリスク 4.3 と比較して相対リスクはそれぞれ 12.8 および 11 です (Masclee et al. Gastroenterology 2014; 147:784-92) 。）",
          "source": {
            "label": "ワルファリン-NSAIDs 臨床意思決定支援アルゴリズム",
            "url": url
          },
          "suggestions": [
            {
              "label": "リスクを評価し、必要に応じて措置を講じます。"
            }
          ]
        }
      ]
       */
    }
    
    return card
  }
}