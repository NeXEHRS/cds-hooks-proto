import { NextResponse } from 'next/server'
import prisma from '@/lib/prisma'


export async function GET() {
  //services[enable:true]を取得
  const services = await prisma.service.findMany({
    where:{
      enable:true,
    },
    select :{
      hook: true,
      title: true,
      description: true,
      id: true,
      usageRequirements: true,
      prefetchs:{
        select:{
          label: true,
          parameta:true,
        }
      },
      extentions:{
        select:{
          label: true,
          parameta:true,
        }
      },
    }
  })

  //取得リストの処理
  for ( const i in services ) {
    const service: any = services[i]
    //prefetch
    const prefetchs = service['prefetchs']
    if( Object.keys(prefetchs).length  > 0 ){
      service["prefetch"]={}
      for ( const i2 in prefetchs ){
        const keystr = prefetchs[i2]['label']
        const prameta = prefetchs[i2]['parameta']
        service["prefetch"][keystr]=prameta
      }
    }

    //prefetch
    const extentions = service['extentions']
    if( Object.keys(extentions).length  > 0 ){
      service["extention"]={}
      for ( const i2 in extentions ){
        const keystr = extentions[i2]['label']
        const prameta = extentions[i2]['parameta']
        service["extention"][keystr]=prameta
      }
    }

    //fetchで一時的に利用した配列要素を削除
    delete service.prefetchs
    delete service.extentions  
    if ( service['usageRequirements'].length === 0 ){
      delete service['usageRequirements'] 
    }
  }
  
  const result ={ "services": services }
  return NextResponse.json(result)
}


