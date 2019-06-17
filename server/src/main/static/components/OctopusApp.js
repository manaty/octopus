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
          <div style="text-align:center">
           <span>Last global synchronisation time</span>
           <span>${this.timeElapsed}</span>
          </div>
          <div style="text-align:center">
            <span>Manual trigers</span>
            <span style="display:flex;justify-content:center">
              ${ !this.startFlag ?
                html `<button @click="${this.setExperience}">Exp. start</button>
                      <button disabled>Exp. end</button>` :
                html `<button disabled>Exp. start</button>
                      <button @click="${this.setExperience}">Exp. end</button>`
              }
            </span>
          </div>
          <div style="text-align:center; display:block ">
            <span>Exports</span>
            <button @click="${ this.generateReport } ">Export all data</button>
          </div>
        </div>
        </div>
          ${this.servers.map(s => html`<octopus-server id="${s.name}" name="${s.name}" .headsets="${ s.headsets }" .mobileApps="${ s.mobileApps }" ></octopus-server>`)}
           `;
    }
    generateReport(){
      try{
        let xhttp = new XMLHttpRequest();
        let self = this
        let apiExperience = this.endpointsWebApi.generateReport 

        xhttp.open("GET", this.serverWebAPI+apiExperience );
        xhttp.send()
        xhttp.onload = function(response ) {
          if (xhttp.status != 200) { 
            alert(`Error ${xhttp.status}: ${xhttp.statusText}`);
            self.startFlag = false
          } else {
            console.log( response )
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
          case "clients":
            let headsetId =  Object.entries( eventData.syncResultsByHeadsetId )
            let headsetIdArray = []
            for( let [ headset, status ] of headsetId) {
                headsetIdArray.push( { name: headset, status: status })
            }
            if ( headsetIdArray.length > 0  ){
                headsets =  headsetIdArray
                self.headsets = headsets
            }
          break;
        }
        self.servers[index] = { name: type , headsets : headsets, mobileApps : mobileApps  } 
      }
      
      setInterval( function(){
        console.log( self.servers , self.slaves )
      },1000  )

    }
    addFakeServer(){
        this.servers.push({'name':'S'+this.servers.length});
    }
}

window.customElements.define("octopus-app",OctopusApp);
