import { NextResponse } from 'next/server'
import { v4 as uuidv4 } from 'uuid';

export async function GET() {
  let card:any = {}
  card['summary']="このHookにはidが必要です。"
  card['indicator']="info"
  card['uuid'] = uuidv4()

  const source:any ={
    "label":"order-select",
    "url":process.env.MY_HOST+"/order-select/<id>"
  }

  card['source'] = source
  ////card['source'] = {"label": hook_id}
  const resjson = { "cards" : card }
  return NextResponse.json(resjson);
}

