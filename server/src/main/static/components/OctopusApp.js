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
          clients: {type: Object },
        };
    }

    constructor(){
        super();
        this.lastSyncDate= new Date();
        this.timeElapsed= "";
        this.servers=[];
        this.mobileApps=[];
        this.startFlag = 0
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
      this.connectWebSocket()
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
            <span>${this.mobileApps.length} mobile apps</span>
          </div>
          <div style="text-align:center">
           <span>Last global synchronisation time</span>
           <span>${this.timeElapsed}</span>
          </div>
          <div style="text-align:center">
            <span>Manual trigers</span>
            <span style="display:flex;justify-content:center">
               <button @click="${this.setExperience}">Exp. start</button>
               <button @click="${this.setExperience}">Exp. end</button>
            </span>
            <span style="display:flex;justify-content:center">
                <button>Exp. start</button>
                <button disabled>Exp. end</button>
            </span>
          </div>
          <div style="text-align:center">
            <span>Exports</span>
            <button >Export all data</button>
          </div>
          <button @click="${this.addFakeServer}">add server</button>
        </div>
        </div>
          ${this.servers.map(s => html`<octopus-server id="${s.name}" name="${s.name}"></octopus-server>`)}
           `;
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
            self.startFlag = true
          }
        };
        xhttp.onerror = function( message ) {
          alert( message );
        };

      } catch( e ){
        console.log( e )
      }
    }


    connectWebSocket(){
      let websocket = new WebSocket( this.serverWebSocket+this.endpointsWebApi.list );
      let self = this

      websocket.onmessage = function (event) {
        let eventData = JSON.parse( event.data ) 
        switch( eventData.type ){
          case 'slaves':
            self.slaves = eventData.slaves 
          break;
          case "clientstates":
            self.clientStates = eventData.statesByHeadsetId 
          break;
          case "clients":
            self.clients =  eventData.syncResultsByHeadsetId 
          break;
          case "headsets":
            self.headsets = eventData.statusByHeadsetId 
          break;
        }
      
      }
      
      setInterval( function(){
        console.log( self.headsets, self.clientStates, self.clients, self.slaves  )
      },1000  )

    }
    addFakeServer(){
        this.servers.push({'name':'S'+this.servers.length});
    }
}

window.customElements.define("octopus-app",OctopusApp);
