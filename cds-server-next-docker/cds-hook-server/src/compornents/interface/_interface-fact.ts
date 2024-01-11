import SelectList from "./_interfaces-list"
import BaseInterface from "./_interface-base"

export default class InitFactory extends BaseInterface{
  constructor( className: string )  {
    super();
    if (SelectList[className] === undefined || SelectList[className] === null) {
      throw new Error(`Class type of \'${className}\' is not in the store`)
    }
    return new SelectList[className]()
  }
}

//スネークケース → キャメルケース
export function toCamelCase(str:string) {
  return str.split(/[-_]/).map(function(word,index){
    return word.charAt(0).toUpperCase() + word.slice(1).toLowerCase();
  }).join('')
}

//キャメルケース → スコア（スネーク）ケース
export function toUnderscoreCase(str:string) {
  return str.split(/(?=[A-Z])/).join('-').toLowerCase()
}