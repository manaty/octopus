import { LitElement, html } from '../web_modules/lit-element.js';
import  "./OctopusUser.js";

class OctopusServer extends LitElement {
    static get properties() {
        return {
            name: {type: String, reflect:true },
            timeElapsed: { type: String },
            headsets: {type: Object},
            clients:  {type: Array },
            headsetsCount: {type:  Number },
            mobileappCount: { type: Number },
            hasConnectedCount: {type:  Number },
        };
    }
    constructor(){
        super();
        console.log("OctopusServer constructor called");
        this.timeElapsed= "";
        this.headsets = []
        this.clients = []
        this.init();
    }
    init(){
      setInterval(()=>{  
        const d=new Date();
        this.timeElapsed=(d.getHours()+':'+d.getMinutes()+':'+d.getSeconds()+".").replace(/(^|:)(\d)(?=:|\.)/g, '$10$2');
      },1000);
    }
    render(){
        return html`
        <link rel="stylesheet" href="./css/style.css">
        <div class="serverMenu">
        <div class="card ">
            <div class="header server">
                <div class="title">Server ${this.name}</div>
            </div>
            <div class="body">
                <p>${ this.hasConnectedCount } users</p>
                <p class="${ this.hasConnectedCount != this.headsetsCount ? 'bold-red' : '' }">${ this.headsetsCount } headsets</p>
                <p class="${ this.hasConnectedCount != this.mobileappCount ? 'bold-red' : '' }">${ this.mobileappCount } mobile apps</p>
            </div>
        </div>
        ${ Object.keys(this.headsets).map( u =>
            (  this.headsets[u].status.hasConnected   ? 
                html`
                <octopus-user name="${ this.headsets[u].status.code }"
                        headsetName="${ this.headsets[u].name }"
                        globalImpedence="${ this.headsets[u].status.globalImpedence }"
                        isSessionConnected ="${ (this.headsets[u].status.connected == true &&  this.headsets[u].status.clientConnectionCreated == true ? true : false ) }"
                        .headsetInfo= "${ this.headsets[u].status.info  }"
                        synchSince="${ this.clients[ this.headsets[u].name] ? this.clients[ this.headsets[u].name ].status.finished : '' }"
                        lastEmotion="${ this.headsets[u].status.app ? ( this.headsets[u].status.app.state ?  this.headsets[u].status.app.state : 'N/A' ) : '' }"
                        isMobileAppsConnected="${ this.headsets[u].status.clientConnectionCreated }"
                        isHeadsetConnected="${ this.headsets[u].status.connected  } " style="float:left" ></octopus-user>` 
             : '' )
            )}
         </div> </div>`
       ;
    }
}
window.customElements.define("octopus-server",OctopusServer);