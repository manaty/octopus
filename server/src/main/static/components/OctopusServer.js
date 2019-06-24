import { LitElement, html } from '../web_modules/lit-element.js';
import  "./OctopusUser.js";

class OctopusServer extends LitElement {
    static get properties() {
        return {
            name: {type: String, reflect:true },
            timeElapsed: { type: String },
            headsets: {type: Object},
            clients:  {type: Array },
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
        <div class="header">
          <div class="title">Server ${this.name}</div>
          <div class="status">
            <span>Server ${this.name}</span>
            <span>${ Object.keys( this.headsets ).length } headsets</span>
            <span>${ Object.keys( this.clients ).length } mobile apps</span>
            <span>Last sync: ${this.timeElapsed}</span>
          </div>
          ${ Object.keys(this.clients).map( u =>
                                html`<octopus-user name="${ this.clients[u].name }"
                                        globalImpedence="${ this.clients[u].status.headsets.globalImpedence }"
                                        .headsetInfo= "${ this.clients[u].status.headsets.info  }"
                                        synchSince="${this.clients[u].status.finished }"
                                        lastEmotion="${ (  this.clients[u].status.experience ? this.clients[u].status.experience.state : 'N/A' ) }"
                                        isMobileAppsConnected="${ this.clients[u].status.headsets.clientSessionCreated  } "
                                        isHeadsetConnected="${ this.clients[u].status.headsets.connected  } " ></octopus-user>` 
        ) }
        </div>`;
    }
}
window.customElements.define("octopus-server",OctopusServer);