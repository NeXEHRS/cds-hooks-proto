//共有funcion

export default class Common{
  //スネークケース → キャメルケース
  toCamelCase(str:string) {
    return str.split(/[-_]/).map(function(word,index){
      return word.charAt(0).toUpperCase() + word.slice(1).toLowerCase();
    }).join('')
  }
  
  //キャメルケース → スコア（スネーク）ケース
  toUnderscoreCase(str:string) {
    return str.split(/(?=[A-Z])/).join('-').toLowerCase()
  }
}
