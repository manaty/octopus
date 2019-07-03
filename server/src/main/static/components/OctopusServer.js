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
        <div class="leftMenu">
        <div class="card ">
            <div class="header server">
                <div class="title">Server ${this.name}</div>
            </div>
            <div class="body">
                <p>Server ${this.name}</p>
                <p>${ this.headsetsCount } headsets</p>
                <p>${ Object.keys( this.clients ).length } mobile apps</p>
                <p>Last sync: ${this.timeElapsed}</p>
            </div>
        </div>
        ${ Object.keys(this.headsets).map( u =>
             ( this.clients[ this.headsets[u].name ]  ? 
                html`<octopus-user name="${ this.headsets[u].status.code }"
                        globalImpedence="${ this.headsets[u].status.globalImpedence }"
                        .headsetInfo= "${ this.headsets[u].status.info  }"
                        synchSince="${ this.clients[ this.headsets[u].name] ? this.clients[ this.headsets[u].name ].status.finished : '' }"
                        lastEmotion="${ this.clients[ this.headsets[u].name ] ? ( this.clients[ this.headsets[u].name ].experience ?  this.clients[ this.headsets[u].name ].experience.state : 'N/A' ) : '' }"
                        isMobileAppsConnected="${ this.headsets[u].status.clientConnectionCreated }"
                        isHeadsetConnected="${ this.headsets[u].status.connected  } " ></octopus-user>` 
                : '' )
        ) } </div> </div>`
       ;
    }
}
window.customElements.define("octopus-server",OctopusServer);