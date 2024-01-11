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