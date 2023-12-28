const fs=require('fs');
const datatype_1=require('C:/work/metacube/CDSHooks/pddi-cds/cql-execution-3.0.1/lib/datatypes/datatypes');
const valueSetFolder='C:/work/metacube/CDSHooks/pddi-cds/pddiCdsService/storage';

class ValueSetExchange{
  constructor(){}

  invoke(valueSetList={}){
    let valueSetJson={};
	
    if(valueSetList){
      const valueSets=new Map();
      valueSetList.forEach(vs => {
        const vsName=vs.id;
        const vsCodes=readFHIRValueSet(vs.id);
	    const obj= Object.fromEntries(vsCodes) 
	    //console.log(JSON.stringify(obj));  
        valueSets.set(vsName, Object.fromEntries(vsCodes));
      });
      
      const vsobj=Object.fromEntries(valueSets);
	valueSetJson=JSON.stringify(vsobj);
    }
    return valueSetJson;
  }
}
module.exports= { ValueSetExchange };

function readFHIRValueSet(url){
  let kv=new Map();
  // read ValueSet
  const fvs=readValueSetFile(url);
    //console.log(url);
    //console.log(fvs);
  if(fvs){
    // version
    let version='noVersion';
    if(fvs.version){
      version=fvs.version;
    }
    // get code values from fvs.
    const codes=getValueSetRecursive(fvs);
      const jc=JSON.stringify(codes);  
      //console.log('===='+jc);
    //const codeStr=JSON.stringify(codes);  
    //kv='{"'+version+'":'+codeStr+'}';
    kv.set(version, codes);
  }
  return kv;
}


function getValueSetRecursive(fvs){
  let codes=[];
  // get code from ValueSet.compose
  if(fvs && fvs.compose && fvs.compose.include){
    const incs=fvs.compose.include;
    incs.forEach(inc => {
	//console.log();	  
      // get version from ValueSet.compose.include
      let ccver='noVersion';
      if(inc.version){
        ccver=inc.version;
      }      
      // get system from ValueSet.compose.include
      let ccsys='noSystem';
      if(inc.system){
        ccsys = inc.system;
      }
      // get code from ValueSet.compose.include.concept
      if(inc.concept){
        const ccs=inc.concept.map(c => new datatype_1.Code(c.code, ccsys ,ccver, c.display)); 
        codes=codes.concat(ccs);
      }
      // get code from other ValueSet which is able to 
      if(inc.valueSet){
        inc.valueSet.forEach(vs => {
          const json=readValueSetFile(vs);
          const ccs=getValueSetRecursive(json);
          codes=codes.concat(ccs);
        });
      }
    });
  }
  // get code from ValueSet.expansion
  if(fvs && fvs.expansion && fvs.expansion.contains){
    const contains=fvs.expansion.contains;
    const ctnCodes=readValueSetContainsRecursive(contains);
    codes=codes.concat(ctnCodes);
  }

  return codes;
}

function readValueSetFile(url){
  // 
  // split url
  let urlArray=url.split('/');
  // make ValueSet file path
  let last=urlArray.length-1;
  let lastm1=urlArray.length-2;
  let json;
    //console.log(urlArray[last]);
    //console.log(urlArray[lastm1]);
    
  if(urlArray[lastm1]==='ValueSet'){
    // url is ValueSet url
    // create filepath using url
    const fname='ValueSet-'+urlArray[last]+'.json';
    const fpath=valueSetFolder+'/'+fname;
    // read ValueSet
    json=JSON.parse(fs.readFileSync(fpath));
  }else{
    // url is not meant ValueSet.
    json=null;
  }
  return json;
}

function readValueSetContainsRecursive(ctn){
  let codes=[];
  const ccs=ctn.map(c => new datatypes_1.Code(c.code, c.system, c.version, c.display));
  codes=codes.concat(ccs);
  if(ctn.contains){
    const ctnCodes=readValueSetContainsRecursive(ctn.contains);
    codes=codes.concat(ctnCodes);
  }
  return codes;
}



