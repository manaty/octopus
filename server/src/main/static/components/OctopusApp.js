import { LitElement, html } from 'https://unpkg.com/@polymer/lit-element@latest/lit-element.js?module';
import  "./OctopusServer.js";

class OctopusApp extends LitElement {
    static get properties() {
        return {
          lastSyncDate: { type: Date },
          timeElapsed: { type: String },
          servers: { type: Object},
          headsets: {type: Object },
          mobileApps: {type: Object },
          serverWebAPI: { type : String },
          serverWebSocket: { type : String },
          endpointsWebApi: {type: Object },
          slaves:{type: Object },
          clientStates : {type: Object },
          hours: { type: Array }
        };
    }

    constructor(){
        super();
        this.lastSyncDate= new Date();
        this.timeElapsed= "";
        this.servers=[];
        this.mobileApps=[];
        this.startFlag = 1
        this.serverWebAPI="http://localhost:9998/rest",
        this.serverWebSocket = "ws://localhost:9998",
        this.endpointsWebApi = {
           list: '/ws/admin',
           start: '/admin/experience/start',
           stop: '/admin/experience/stop',
           trigger : '/admin/trigger',
           generateReport: '/report/generate',
           gerReport: '/report/get'
        }
        this.slaves = []
        this.clientStates = []
        this.clients = []
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

    render(){
        return html`
        <link rel="stylesheet" href="./css/style.css">
        <div class="leftMenu">
          <div class="title">Octopus'Sync</div>
          <div class="status">
            <span>Live status of connected devices</span>
            <span>${this.servers.length} servers</span>
            <span>${ Object.keys( this.headsets ) .length} headsets</span>
            <span>${ Object.keys( this.mobileApps ) .length } mobile apps</span>
          </div>
          <div class="center">
           <span>Last global synchronisation time</span>
           <span>${this.timeElapsed}</span>
          </div>
          <div class="center">
            <span>Manual trigers</span>
            <div class="block" >
              <button class="btn-mtrigger" @click="${ this.setManualTrigger }" data-args="chef1">Chef Arch 1</button>
              <button class="btn-mtrigger" @click="${ this.setManualTrigger }" data-args="chef2">Chef Arch 2</button>
              <button class="btn-mtrigger" @click="${ this.setManualTrigger }" data-args="chef3">Chef Arch 3</button>
              <button class="btn-mtrigger" @click="${ this.setManualTrigger }" data-args="chef4">Chef Arch 4</button>
            </div>
          </div>
          <div class="center">
            <span>Experience</span>
            <span style="display:flex;justify-content:center">
              ${ !this.startFlag ?
                html `<button @click="${this.setExperience}" >Exp. start</button>
                      <button disabled>Exp. end</button>` :
                html `<button disabled>Exp. start</button>
                      <button @click="${this.setExperience}">Exp. end</button>`
              }
            </span>
          </div>
          <div class="block center" ">
            <div>Exports</div>
            <div class="block"> 
              <div class="w50  pull-left"> From </div>
              <div class="w50  pull-left"> 
                <select> ${ this.hours.map( u => html `<option value="${u.value}"> ${ u.valueText }</option>`) } </select>
              </div>
              <div class="w50 pull-left"> To </div>
              <div class="w50  pull-left"> 
                <select> ${ this.hours.map( u => html `<option value="${u.value}" > ${ u.valueText }</option>`) } </select>
              </div>
            </div>
            <button @click="${ this.generateReport } ">Export all data</button>
          </div>
        </div>
        </div>
          ${this.servers.map(s => html`<octopus-server id="${s.name}" name="${s.name}" .headsets="${ s.headsets }" .mobileApps="${ s.mobileApps }" ></octopus-server>`)}
           `;
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
      for( let i = 0; i <= 24; i++) {
        let hour = i
        let mins , temh , ampm  , hourText
        for( let x = 0; x <=  59; x ++ ){
              if( x < 10){
                  x = '0'+x
              }
              mins = x
              temh = i
              if( i > 12  ){
                temh = i - 12
                ampm = 'PM'
              } else {
                temh = i
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
          xhttp.open("GET", 'rest'+this.endpointsWebApi.generateReport+'?headset_id='+headsetID+'&from=00:00&to=23:00' );
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
      let mobileApps = []
      let self = this

      websocket.onmessage = function (event) {
        let eventData = JSON.parse( event.data ) 
        switch( eventData.type ){
          case 'slaves':
            self.slaves = eventData.slaves 
            if( self.slaves.length > 0   ){
              self.slaves.forEach( function( item, slaveIndex ){
                self.connectWebSocket( 'slave', item , slaveIndex + 1  )
              })
            }
          break;
          case "clientstates":
            let mobileAppsStates =  Object.entries( eventData.statesByHeadsetId )
            let mobileAppsArray = []
            for( let [ mobileApp, status ] of mobileAppsStates ) {
              mobileAppsArray.push( { name: mobileApp, status: status[0] })
            }
            if ( mobileAppsArray.length > 0  ){
                mobileApps = mobileAppsArray
                self.mobileApps = mobileApps
            }
          break;
          case "headsets":
            let headsetId =  Object.entries( eventData.statusByHeadsetId )
            let headsetIdArray = []
            for( let [ headset, status ] of headsetId) {
              status.info =  {
                "battery" : 4,
                "signal" : 2,
                "af3" : 0.0,
                "f7" : 2.0,
                "f3" : 1.0,
                "fc5" : 0.0,
                "t7" : 4.0,
                "p7" : 0.0,
                "o1" : 4.0,
                "o2" : 4.0,
                "p8" : 2.0,
                "t8" : 4.0,
                "fc6" : 4.0,
                "f4" : 0.0,
                "f8" : 4.0,
                "af4" : 4.0
              }
                headsetIdArray.push( { name: headset, status: status })
                if ( status.clientSessionCreated ){
                }
            }
            if ( headsetIdArray.length > 0  ){
                headsets =  headsetIdArray
                self.headsets = headsets
            }
          break;
        }
        self.servers[index] = { name: type , headsets : headsets, mobileApps : mobileApps  } 
      }
    }
    addFakeServer(){
        this.servers.push({'name':'S'+this.servers.length});
    }
}

window.customElements.define("octopus-app",OctopusApp);
