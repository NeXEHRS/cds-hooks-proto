import BaseInterface from "./_interface-base"

export default class ClassInterface extends BaseInterface
{
  init(): void {
    super.init();
    this.siteId = 1
  }

  execute() {
    super.execute();
    console.log("BaseInterface:class-interface")
    return "......BaseInterface:class-interface"
  }

}