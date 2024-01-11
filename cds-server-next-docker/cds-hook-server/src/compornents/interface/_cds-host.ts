//CDS他サーバの確認と転送
import prisma from '@/lib/prisma'

export default class CdsHostReruest{
  public hook:string
  public hook_id:string

  constructor( hook:string, hook_id:string )  {
    this.hook = hook
    this.hook_id = hook_id
  }

  async check_host(){
    try{
      const hosts = await prisma.service.findMany({
        where:{
          hook:this.hook,
          id:this.hook_id
        },
        select :{
          cdshost:{
            select:{
              url:true,
              username:true,
              password:true,
            }
          }
        }
      })
      
      if( Object.keys(hosts[0].cdshost).length > 0 ){
        return hosts[0].cdshost[0]
      }else{
        return {}
      }
    }catch(e){
      //console.log('Error', e);
    }
  }
  
  async post_host( host_data:any, BODY: object  ){
    const url:string = host_data.url+"/"+this.hook+"/"+this.hook_id
    const user:string =  host_data.user
    const password:string =  host_data.password

    const response =  fetch(url ,
      {
        method: 'POST',
        headers: {
        //  'Authorization': 'Bearer '+this.access_token,
          'Content-Type': 'application/text',
        },
        body: JSON.stringify(BODY)
      });
  
      try{
        const result = await (await response).json()
        return result
      }catch(e){
        //console.log('Error', e);
        const result = {"Error": String(e)}
        return result
      }
  }

}
