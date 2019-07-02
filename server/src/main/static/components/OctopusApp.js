import { LitElement, html } from '../web_modules/lit-element.js';
import  "./OctopusServer.js";

class OctopusApp extends LitElement {
    static get properties() {
        return {
          lastSyncDate: { type: Date },
          timeElapsed: { type: String },
          servers: { type: Object},
          headsets: {type: Object },
          experience: {type: Object },
          serverWebAPI: { type : String },
          serverWebSocket: { type : String },
          endpointsWebApi: {type: Object },
          slaves:{type: Object },
          clients : {type: Object },
          hours: { type: Array },
          headsetsCount: { type: String }
        };
    }

    constructor(){
        super();
        this.lastSyncDate= new Date();
        this.timeElapsed= "";
        this.servers=[];
        this.experience=[ ];
        this.startFlag = 0;;
        this.headsetsCount = 0;
        this.serverWebAPI="http://localhost:9998/rest";
        this.serverWebSocket = "ws://localhost:9998";
        this.endpointsWebApi = {
           list: '/ws/admin',
           start: '/admin/experience/start',
           stop: '/admin/experience/stop',
           trigger : '/admin/trigger',
           generateReport: '/report/generate',
           gerReport: '/report/get'
        }
        this.slaves = []
        this.clients = [ ]
        this.headsets = []
        this.hours = this.getHours()
        this.init();
    }
    init(){
      this.connectWebSocket( 'master', this.serverWebSocket, 0  )
      setInterval(()=>{
        const d=new Date();
        this.timeElapsed=(d.getHours()+':'+d.getMinutes()+':'+d.getSeconds()+".").replace(/(^|:)(\d)(?=:|\.)/g, '$10$2');
      },1000);
    }
    
    setManualTrigger(e){
        let xhttp = new XMLHttpRequest();
        let self = this
        let apiExperience = this.endpointsWebApi.trigger 
        let params =  e.target.getAttribute('data-args')
        xhttp.open("POST", this.serverWebAPI+apiExperience );
        xhttp.send( JSON.stringify({ body : params } ) )
        xhttp.onreadystatechange = function ( res ) {
          if (this.readyState === 4) {
              if (this.status === 200) {
                  let res =  JSON.parse( this.response  );
                  console.log( this.response)
              } else if (this.response == null && this.status === 0) {
                  document.body.className = 'error offline';
                  console.log("The computer appears to be offline.");
              } else {
                  document.body.className = 'error';
              }
          }
      }
    }
    getHours(){
      let hours = []
      for( let i = 0; i <= 23; i++) {
        let hour = i
        let mins , temh , ampm  , hourText
        for( let x = 0; x <=  59; x ++ ){
              if( x < 10){
                  x = '0'+x
              }
              mins = x
              if( i > 11  ){
                temh = ( i < 10 ? '0'+i : ( i > 12 ? i -12 : i )  )
                ampm = 'PM'
              } else {
                temh =  ( i < 10 ? '0'+i : ( i > 12 ? i -12 : i )  )
                ampm = 'AM'
              }
              hour = i+':'+mins
              hourText = temh+':'+mins+' '+ampm
              hours.push({ value : hour, valueText : hourText })
        }
      }
      return hours 
   }
    generateReport(){
      let from=  this.shadowRoot.getElementById("sel-from") 
      let fromTime = from.options[from.selectedIndex].value 

      let to =  this.shadowRoot.getElementById("sel-to") 
      let toTime = to.options[to.selectedIndex].value

      try{
        let xhttp = new XMLHttpRequest();
        let self = this
        let apiExperience = this.endpointsWebApi.generateReport 

        xhttp.open("GET", this.serverWebAPI+apiExperience );
        xhttp.send()
        
        xhttp.onreadystatechange = function ( res ) {
          if (this.readyState === 4) {
              if (this.status === 200) {
                  let res =  JSON.parse( this.response  );
                  let reports = Object.entries( res )
                  for( let [ key, report ] of reports ) {
                      self.generateReportHeadset( key )
                    }
              } else if (this.response == null && this.status === 0) {
                  document.body.className = 'error offline';
                  console.log("The computer appears to be offline.");
              } else {
                  document.body.className = 'error';
              }
          }
      };        
        xhttp.onload = function(response ) {
          if (xhttp.status != 200) { 
            alert(`Error ${xhttp.status}: ${xhttp.statusText}`);
            self.startFlag = false
          }
        };
        xhttp.onerror = function( message ) {
          alert( message );
        };
      } catch( e ){
        console.log( e )
      }
    }
    generateReportHeadset( headsetID ){
      try{
          let xhttp = new XMLHttpRequest();
          let self = this
          let apiExperience = this.endpointsWebApi
          
          xhttp.onreadystatechange = function ( res ) {
              if (this.readyState === 4) {
                  if (this.status === 200) {
                      let res =  JSON.parse( this.response  );
                      let reports = Object.entries( res )
                      for( let [ key, report ] of reports ) {
                          console.log( key, report )
                          window.open( self.serverWebAPI+'/report/get/'+report )
                        }
                  } else if (this.response == null && this.status === 0) {
                      document.body.className = 'error offline';
                      console.log("The computer appears to be offline.");
                  } else {
                      document.body.className = 'error';
                  }
              }
          };
          xhttp.open("GET", 'rest'+this.endpointsWebApi.generateReport+'?headset_id='+headsetID+'&from='+fromTime+'&to='+toTime );
          xhttp.send()
          xhttp.onload = function(response ) {
            if (xhttp.status != 200) { 
                alert(`Error ${xhttp.status}: ${xhttp.statusText}`);
                self.startFlag = false
            }
          };
          xhttp.onerror = function( message ) {
          alert( message );
          };
  
      } catch( e ){
          console.log( e )
          }
      }
    setExperience( experience ){
      try{
        let xhttp = new XMLHttpRequest();
        let self = this
        let apiExperience = ( !self.startFlag  ? this.endpointsWebApi.start : this.endpointsWebApi.stop )

        xhttp.open("POST", this.serverWebAPI+apiExperience );
        xhttp.send()
        xhttp.onload = function() {
          if (xhttp.status != 200) { 
            alert(`Error ${xhttp.status}: ${xhttp.statusText}`);
            self.startFlag = false
          } else {
            self.startFlag = !self.startFlag
          }
        };
        xhttp.onerror = function( message ) {
          alert( message );
        };
      } catch( e ){
        console.log( e )
      }
    }

    connectWebSocket( type , ip , index ){
      let connection = ( type !='slave' ?  this.serverWebSocket+this.endpointsWebApi.list  : 'ws://'+ip+':9999'+this.endpointsWebApi.list )
      let websocket = new WebSocket( connection  );
      let headsets = []
      let experience = []
      let clients = []
      let self = this

      websocket.onmessage = function (event) {
        let eventData = JSON.parse( event.data ) 
        console.log( 's', eventData)
        switch( eventData.type ){
          case 'slaves':
            self.slaves = eventData.slaves 
            if( self.slaves.length > 0 ){
              self.slaves.forEach( function( item, slaveIndex ){
                self.connectWebSocket( 'slave', item , slaveIndex + 1  )
              })
            }
          break;
          case 'clients':
          let clientsid =  Object.entries( eventData.syncResultsByHeadsetId )
          let clientsArray = []
          if( clientsid.length > 0  ){
            for( let [ client, status ] of clientsid ) {
              self.clients[client] =  { name: client, status: status[0], experience : { state : 0 } }
            }
          }
          break;
          case "clientstates":
            let experienceStates =  Object.entries( eventData.statesByHeadsetId )
            let experienceArray = []
            for( let [ experience, status ] of experienceStates ) {
              experienceArray.push( { name: experience, status: status[0] })
            }
            if ( experienceArray.length > 0  ){
                experience = experienceArray
                self.experience = experience
            }
          break;
          case "headsets":
            let headsetId =  Object.entries( eventData.statusByHeadsetId )
            let headsetIdArray = []
            for( let [ headset, status ] of headsetId) {
              if( !status.info ) {
                status.info = {}
              } 
              /* //Uncomment this block for random impedence value
              else {
                status.info =  {
                  "battery" : 4,
                  "signal" : 2,
                  "af3" : Math.floor(Math.random() * 4) + 1,
                  "f7" : Math.floor(Math.random() * 4) + 1,
                  "f3" : Math.floor(Math.random() * 4) + 1,
                  "fc5" : Math.floor(Math.random() * 4) + 1,
                  "t7" : Math.floor(Math.random() * 4) + 1,
                  "p7" :Math.floor(Math.random() * 4) + 1,
                  "o1" : Math.floor(Math.random() * 4) + 1,
                  "o2" : Math.floor(Math.random() * 4) + 1,
                  "p8" : Math.floor(Math.random() * 4) + 1,
                  "t8" :Math.floor(Math.random() * 4) + 1,
                  "fc6" : Math.floor(Math.random() * 4) + 1,
                  "f4" : Math.floor(Math.random() * 4) + 1,
                  "f8" : Math.floor(Math.random() * 4) + 1,
                  "af4" : Math.floor(Math.random() * 4) + 1,
                 } }   */
                let globalImpedenceTotal = 0
                Object.entries(status.info).map( (value, index ) =>  ( value[0] != 'battery' && value[0] != 'signal' ? globalImpedenceTotal += value[1] : globalImpedenceTotal = globalImpedenceTotal ) )
                status.globalImpedence =  Math.round( ( globalImpedenceTotal / 56 ) * 100 ) 
                headsetIdArray.push( { name: headset, status: status })
            }
            if ( headsetIdArray.length > 0  ){
                headsets =  headsetIdArray
                self.headsets = headsetIdArray
            }
          break;
        }

        let headsetsCountTemp = 0
        Object.values( headsets ).map( ( index, value ) =>  {
          if( index.status.connected ){
            headsetsCountTemp += 1 

          }
          Object.values( experience).map( ( indexApp, valueApp ) =>  {
            if ( index.name == indexApp.name ){
              headsets[value].status.app = indexApp.status
            }
          })
        })
        
        let clients =  Object.values( self.clients ) 
        if( clients.length > 0 ){
            Object.values( self.clients ).map( ( indexClient, valueClients ) =>  {
              let headsets =  Object.values( self.headsets )
              if ( headsets.length > 0  ){
                  Object.values(headsets).map( ( indexHeadset, valueHeadset ) =>  {
                      if ( indexClient.name == indexHeadset.name ){
                          self.clients[indexHeadset.name].status.headsets = indexHeadset.status
                      }
                  })
              } else {
                self.clients[indexClient.name] =  { name: indexClient.name, status : { headsets : { globalImpedence: 0 } } }
              }

              let experience =  Object.values( self.experience)
              if ( experience.length > 0  ){
                  experience.map( ( indexExperience, valueExperience ) =>  { 
                      if ( indexClient.name == indexExperience.name ){
                          self.clients[indexExperience.name].status.experience = indexExperience.status
                      }
                  }) 
              }            
          })
        }
       // self.headsetsCount = headsetsCountTemp;
        self.servers[index] = { name: type , headsets : headsets,  headsetsCount: headsetsCountTemp, experience : experience, clients: self.clients } 
        console.log( 'servers', eventData )
      }
    }
    render(){
      return html`
      <link rel="stylesheet" href="./css/style.css">
      <div class="leftMenu">
        <div class="card">
          <div class="header octopus">
            <div class="title"> 
              Octopus'Sync
            </div>
          </div>
          <div class="body">
            <p>Live status of connected devices</p>
            <p>${this.servers.length} servers</p>
            <p>${ this.headsetsCount } headsets</p>
            <p>${ Object.keys( this.clients ) .length } mobile apps</p>
          </div>
        </div>
        <div class="card">
         <div class="header">
          <div class="title">Last global synchronisation time </div>
        </div>
         <div class="body center">${this.timeElapsed}</div>
        </div>
        <div class="card">
          <div class="header">
            <div class="title">Manual trigers</div>
          </div>
          <div class="body center" >
            <button @click="${ this.setManualTrigger }" data-args="chef1">Chef Orch 1</button>
            <button @click="${ this.setManualTrigger }" data-args="chef2">Chef Orch 2</button>
            <button @click="${ this.setManualTrigger }" data-args="chef3">Chef Orch 3</button>
            <button @click="${ this.setManualTrigger }" data-args="chef4">Chef Orch 4</button>
          </div>
        </div>
        <div class="card hide">
          <div class="header">
            <div class="title"> Experience</div>
          </div>
          <div class="body center">
            ${ !this.startFlag ?
              html `<button @click="${this.setExperience}" >Exp. start</button>
                    <button disabled>Exp. end</button>` :
              html `<button disabled>Exp. start</button>
                    <button @click="${this.setExperience}">Exp. end</button>`
            }
          </div>
        </div>
        <div class="card" ">
            <div class="header">
              <div class="title">Exports</div>
            </div>
            <div class="body center"> 
              <div class="w50 pull-left"> From </div>
              <div class="w50 pull-left"> 
                <select id="sel-from">${ this.hours.map( u => html `<option value="${u.value}"> ${ u.valueText }</option>`) }</select>
              </div>
              <div class="w50 pull-left"> To </div>
              <div class="w50 pull-left"> 
                <select id="sel-to">${ this.hours.map( u => html `<option value="${u.value}" > ${ u.valueText }</option>`) }</select>
              </div>
              <button @click="${ this.generateReport } ">Export all data</button>
            </div>
          </div>
        </div>
      </div>
      ${this.servers.map(s => html`<octopus-server id="${s.name}" name="${s.name}" .headsets="${ s.headsets }" headsetsCount="${ s.headsetsCount }" .experience="${ s.experience }" .clients="${ s.clients}"></octopus-server>`)} `;
  }
}

window.customElements.define("octopus-app",OctopusApp);
