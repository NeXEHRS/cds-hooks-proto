
import { NextResponse } from 'next/server'
import CdsHostReruest from '@/compornents/interface/_cds-host'
import InitFactory from '@/compornents/interface/_interface-fact'
import Common from '@/compornents/interface/common'

type Params = {
  params: any ,
}

type Service = {
  hook: string,
  title: string,
  description: string,
  id: string,
  usageRequirements: string,
  prefetch : any[],
  prefetchs : string[],
}

export async function POST(request: Request, {params}:Params) {
  const com = new Common()
  const cards:any = []
  const body =await request.json()
  
  
  //IPアドレス(CDSホストを確認するための処理)
  const networkInfo = await fetch('https://ipinfo.io?callback')
     .then(res => res.json())
  const ip = networkInfo.ip
  let url = request.url.replaceAll('localhost',ip)

  let hook = body.hook
  try{//body.hookはタイプミスの可能性があるのでurlのパスからhookを調査
    
    let path:any = url.split('/')
    hook = path[3]
  }catch(e){
    console.log('Error', e);
  }

  const hook_id = params.slug 
  
  //CDSエンジンの確認と転送
  let Trans:boolean = false
  try{
    const cds_host = new CdsHostReruest(hook, hook_id,)
    const cds_info:any = await cds_host.check_host()
    if (Object.keys(cds_info).length > 0){ 
      Trans = true 
    }
    
    if(Trans){ //cds-hostに転送
      let result = await cds_host.post_host(cds_info, body)
      let cardAry = result['cards']
      for(let i in cardAry){
        cards.push(cardAry[i])
      }
      console.log('hook_id:['+hook_id+']は['+cds_info.url+']に転送されました。')
    }
  }catch(e){
    //console.log('Error', e);
  }

  if( !Trans ){
    console.log("hook", hook+"/"+hook_id)
    try{
      const className = com.toCamelCase(hook_id)
      const factory: any = new InitFactory( className );
      let cardAry = await factory.Execute( body, hook_id, url )
      for(let i in cardAry){
        cards.push(cardAry[i])
      }
      console.log('['+className+']compornentsが実行されました。')
    }catch(e){
      console.log('e:'+e)
    }
  }
  //cards最終系：CDS サービスにユーザーに対する意思決定サポートがない場合、サービスは{ "cards" : [] }のように空のカード配列を含んだ 200 HTTP 応答を返す。
  const resjson = { "cards" : cards }
  return NextResponse.json(resjson)
}
