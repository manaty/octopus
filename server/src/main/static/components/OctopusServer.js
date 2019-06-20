import { LitElement, html } from 'https://unpkg.com/@polymer/lit-element@latest/lit-element.js?module';
import  "./OctopusUser.js";

class OctopusServer extends LitElement {
    static get properties() {
        return {
            name: {type: String, reflect:true },
            lastSyncDate: { type: Date },
            timeElapsed: { type: String },
            users: { type: Array},
            headsets: {type: Object},
            mobileApps: {type: Object  },
            headsetInfo: {type: Object  },
            lastEmotion  : { type : String },
            synchSince  : { type : String }
        };
    }

    constructor(){
        super();
        console.log("OctopusServer constructor called");
        this.lastSyncDate= new Date();
        this.timeElapsed= "";
        this.headsets = []
        this.headsetInfo = {}
        this.mobileApps = []
        this.users=[];
        this.apps=[];
        this.init();
        this.isHeadsetConnected = "";
        this.isMobileAppsConnected =  ""
        this.headsetsInfo = []
        this.lastEmotion =  ""
        this.synchSince = ""
        this.globalImpedence = ""
    }

    init(){
      setInterval(()=>{

        const d=new Date();
        this.timeElapsed=(d.getHours()+':'+d.getMinutes()+':'+d.getSeconds()+".").replace(/(^|:)(\d)(?=:|\.)/g, '$10$2');
        this.getHeadsetInfo()
      },1000);
    }
    getHeadsetInfo(){
        let headsets =  this.headsets
        for( let index = 0; index < headsets.length ; index++ ){
            if ( headsets[index].status.clientSessionCreated ){
                this.isMobileAppsConnected =  'isActive'
            }
            if ( headsets[index].status.connected ){
                this.isHeadsetConnected =  'isActive'
            }
            let mobileApps =  this.mobileApps

            for( let mindex = 0; mindex < mobileApps.length ; mindex++ ){
                if ( mobileApps[mindex].name == headsets[index].name ){
                    this.lastEmotion =  mobileApps[mindex].status.state
                }
                this.synchSince =  mobileApps[mindex].status.since
            }
            this.headsetInfo = headsets[index].status.info
            this.globalImpedence = headsets[index].status.globalImpedence
        }
    }
    
    render(){
        return html`
        <link rel="stylesheet" href="./css/style.css">
        <div class="header">
          <div class="title">Server ${this.name}</div>
          <div class="status">
            <span>Server ${this.name}</span>
            <span>${ Object.keys( this.headsets ).length } headsets</span>
            <span>${ Object.keys( this.mobileApps ).length } mobile apps</span>
            <span>Last sync: ${this.timeElapsed}</span>
          </div>
           ${ this.mobileApps.map(u => html`<octopus-user name="${ u.name }" globalImpedence="${ this.globalImpedence }" .headsetInfo= "${ this.headsetInfo }"   synchSince="${this.synchSince}" lastEmotion="${this.lastEmotion}" isMobileAppsConnected="${ this.isMobileAppsConnected } " isHeadsetConnected="${ this.isHeadsetConnected } " ></octopus-user>`)}
        </div>`;
    }
}

window.customElements.define("octopus-server",OctopusServer);