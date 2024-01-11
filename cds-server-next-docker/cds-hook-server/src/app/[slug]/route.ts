import { NextResponse } from 'next/server'
import { v4 as uuidv4 } from 'uuid'

type Params = {
  params: any ,
}

export async function GET(request: Request, {params}:Params) {
  
  const slug = params.slug 

  let card:any = {}
  
  card['summary']="コンテキストパス「"+slug+"」は実行エンジンの実装がありません。"
  card['indicator']="info"
  card['uuid'] = uuidv4()

  const source:any ={
    "label":slug,
    "url":process.env.MY_HOST+"/"+slug
  }

  card['source'] = source
  ////card['source'] = {"label": hook_id}
  const resjson = { "cards" : card }
  return NextResponse.json(resjson);
}