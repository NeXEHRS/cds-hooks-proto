//BaseCrawler.ts
export default class BaseInterface
{
  public siteId: number = 0
  
  constructor() {
    this.init()
  }

  public init() {
    //console.log('BaseInterface:init')
  }

  public execute(){
    //console.log('BaseInterface:execute')
  }
}