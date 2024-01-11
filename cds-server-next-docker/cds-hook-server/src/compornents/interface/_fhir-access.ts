//FHIRサーバへのアクセスとデータ取得
export default class FhirAccess{
  public url:string
  public access_token:string
  public token_type:string
  public expires_in:string
  public scope:string
  public subject:string

  constructor( url:string, fhirAuthorization:any )  {
    this.url = url
    this.access_token = fhirAuthorization.access_token
    this.token_type = fhirAuthorization.token_type
    this.expires_in = fhirAuthorization.expires_in
    this.scope = fhirAuthorization.scope
    this.subject = fhirAuthorization.subject
  }

  async get_fhir(){
    const response =  fetch(this.url ,
      {
        method: 'GET',
        headers: {
          'Authorization': 'Bearer '+this.access_token,
          'Content-Type': 'application/json',
        }
      });
  
      try{
        const result = await (await response).json()
        return result
      }catch(e){
        console.log('There was an error', e);
        const result = {"error": String(e)}
        return result
      }
  }

  async post_fhir(BODY: string ){
    const response =  fetch(this.url ,
      {
        method: 'POST',
        headers: {
          'Authorization': 'Bearer '+this.access_token,
          'Content-Type': 'application/json',
        },
        body: BODY
      });
  
      try{
        const result = await (await response).json()
        return result
      }catch(e){
        console.log('There was an error', e);
        const result = {"error": String(e)}
        return result
      }
  }

}
