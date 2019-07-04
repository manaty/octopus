import { LitElement, html } from '../web_modules/lit-element.js';
import "./OctopusTimepicker.js"
import "./OctopusServer.js";

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
          headsetsCount: { type: String }
        };
    }
    constructor(){
        super();
        this.lastSyncDate= new Date();
        this.timeElapsed= "";
        this.servers=[];
        this.experience=[ ];
        this.startFlag = 0
        this.serverWebAPI=   "http://localhost:9998/rest" 
        this.serverWebSocket = "ws://localhost:9998"
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
        this.headsetsCount = 0;
        this.mobileCount = 0
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
        xhttp.send( params )
        xhttp.onreadystatechange = function ( res ) {
          if (this.readyState === 4) {
              if (this.status === 200) {
                console.log( 'Sent Manual Trigger')
              } else if (this.response == null && this.status === 0) {
                  console.log("The computer appears to be offline.");
              } else {
                 console.log("The computer appears to be offline.");
              }
          }
      }
    }
    generateReport( event ){
      let from =  this.shadowRoot.getElementById( "app-from")
      let fromTime = from.getSelectedTime()
      let to =  this.shadowRoot.getElementById( "app-to") 
      let toTime = to.getSelectedTime()

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
                  if ( Object.keys(res).length > 1  ){                  
                    let reports = Object.entries( res )
                    for( let [ key, report ] of reports ) {
                        self.generateReportHeadset( key , fromTime, toTime )
                      }
                  } else {
                      alert( 'No EEG data has been recorded for this date range so no export can be created.' )
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
            alert( 'No date range selected, please select one' );
            self.startFlag = false
          }
        };
        xhttp.onerror = function( message ) {
          console.log( message );
        };
      } catch( e ){
        console.log( e )
      }
    }
    generateReportHeadset( headsetID, fromTime, toTime ){
      try{
          let xhttp = new XMLHttpRequest();
          let self = this

          xhttp.onreadystatechange = function ( res ) {
              if (this.readyState === 4) {
                  if (this.status === 200) {
                      let res =  JSON.parse( this.response  );
                      let reports = Object.entries( res )

                      for( let [ key, report ] of reports ) {
                          setTimeout( function() { 
                            window.open( self.serverWebAPI+'/report/get/'+report )
                          },2000)
                        }
                  } else if (this.response == null && this.status === 0) {
                      document.body.className = 'error offline';
                      console.log("The computer appears to be offline.");
                  } else {
                      document.body.className = 'error';
                  }
              }
          };
          let endpointsWebApi = ""
          if( fromTime == "0:00" &&  toTime == "0:00" ) {
              endpointsWebApi = 'rest'+this.endpointsWebApi.generateReport+'?headset_id='+headsetID
          } else {
              endpointsWebApi = 'rest'+this.endpointsWebApi.generateReport+'?headset_id='+headsetID+'&from='+fromTime+':00&to='+toTime+":59"
          }

          xhttp.open("GET", endpointsWebApi  );
          xhttp.send()
          xhttp.onload = function(response ) {
            if (xhttp.status != 200) { 
              let res =  JSON.parse( this.response  );
              let reports = Object.entries( res )
              for( let [ key, report ] of reports ) {
		              setTimeout( function() { 
                    window.open( self.serverWebAPI+'/report/get/'+report )
		              },1000)
                }
            }
          };
          xhttp.onerror = function( message ) {
            alert( 'No date range selected, please select one' );
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
                let globalImpedenceTotal = 0

                Object.entries(status.info).map( (value, index ) =>  ( value[0] != 'battery' && value[0] != 'signal' ? globalImpedenceTotal += value[1] : globalImpedenceTotal = globalImpedenceTotal ) )
                status.globalImpedence =  Math.round( ( globalImpedenceTotal / 56 ) * 100 ) 
                headsetIdArray.push( { name: headset, status: status })
            }
            if ( headsetIdArray.length > 0  ){
                headsets =  headsetIdArray
                if ( self.headsets.length < 1 ){
                  self.headsets = headsetIdArray
                }
            }
          break;
        }

        let headsetsCountTemp = 0
        let mobileAppCountTemp = 0
        Object.values( headsets ).map( ( index, value ) =>  {
          if( index.status.connected ){
            headsetsCountTemp += 1 
            self.headsets[value].status.hasConnected = true
            headsets[value].status.hasConnected = true
          } else {
            if (  self.headsets[value].status.hasConnected  ){
              self.headsets[value].status.hasConnected = true
              headsets[value].status.hasConnected = true
            }
          }

          if( index.status.clientConnectionCreated ){
            mobileAppCountTemp += 1
            self.headsets[value].status.hasConnected = true
            headsets[value].status.hasConnected = true
          } else {
            if (  self.headsets[value].status.hasConnected  ){
              self.headsets[value].status.hasConnected = true
              headsets[value].status.hasConnected = true
            }
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
        self.headsetsCount = headsetsCountTemp
        self.mobileAppCount = mobileAppCountTemp
        self.servers[index] = { name: type , headsets : headsets, headsetsCount: headsetsCountTemp, mobileAppCount: mobileAppCountTemp ,  experience : experience, clients: self.clients } 
       // console.log( 'servers', self.servers)
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
            <p>${ this.mobileAppCount } mobile apps</p>
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
          <div class="body center ">
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
            <vaadin-time-picker>
                <octopus-timepicker placeholder="From" id="${ 'app-from' }" >  </octopus-timepicker>
                <octopus-timepicker placeholder="To"  id="${ 'app-to' }" >  </octopus-timepicker>
              <button @click="${ this.generateReport } ">Export all data</button>
            </div>
          </div>
        </div>
      </div>
      ${this.servers.map(s => html`<octopus-server id="${s.name}" name="${s.name}" .headsets="${ s.headsets }" mobileappCount="${s.mobileAppCount}" headsetsCount="${s.headsetsCount}" .experience="${ s.experience }" .clients="${ s.clients}"></octopus-server>`)} `;
  }
}

window.customElements.define("octopus-app",OctopusApp);
