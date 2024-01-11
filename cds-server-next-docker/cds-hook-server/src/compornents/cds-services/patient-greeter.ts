import BaseInterface from '../interface/_interface-base'
import FhirAccess from '../interface/_fhir-access';
import { v4 as uuidv4 } from 'uuid';

export default class PatientGreeter extends BaseInterface
{
  init(): void {
    super.init();
  }

  async Execute(data:any, hook_id:string){
    const cards:any = []
    let FHIR_access = false
    let patient:any = null
    let ermsg:any = null

    const patientid = data.context.patientId
    if( patientid === undefined )
      return {"summary":"contextデータにpatientidが見当たりません。(FHIRサーバに問合せできませんでした。)"}
    //console.log("patientid:", patientid )
  
    //patientリソースの内容を確認
    try{ 
      if ( 'prefetch' in data ){
        const prefetch = data.prefetch
        //patientリソースの確認
        //console.log("prefetch:>",prefetch)
        if ( 'patientToGreet' in prefetch ){ //DiscoverでpatientToGreetした場合
          patient = prefetch.patientToGreet
          //console.log("patientToGreet:>", Object.keys(patient).length )
        }else if ( 'patient' in prefetch ){ //Discoverでpatientた場合(推奨）)
          patient = prefetch.patient
          //console.log("patient:>", Object.keys(patient).length )
        }else{
          FHIR_access=true
        }
      }else{
        FHIR_access=true
      }
    }catch(e){
      console.log("e:>",e )
    }
    //console.log("patient:>",patient)
    //console.log("FHIR_access:>",FHIR_access)
    
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
        const fhirserver = data.fhirServer
        const url = fhirserver+"/Patient/"+patientid
        const response = await fetch(url)
        patient = await response.json()
        
      }catch(e){
        ermsg = "FHIREサーバーに関するAuthorization情報が無いためpatientリソースを取得できませんでした。"
      }
    }
    
    //console.log("ermsg:>", ermsg)
    //console.log("patient:>", patient)

    //cardに補足データを加える
    let card:any = {}
    if ( ermsg !== null ){
      card['summary']=ermsg
    }else{
      const name:any = patient.name
      //console.log("patient:>", name)
      const patientName = name[0].family+" "+name[0].given[0]
      //console.log("patientName:>", patientName)
      card['summary']="診察中："+patientName
      card['indicator']="info"
      card['uuid'] = uuidv4()
      card['source'] = {"label": hook_id}
    }
    //console.log("card:>", card)
    cards.push(card)
    return cards
  }
}