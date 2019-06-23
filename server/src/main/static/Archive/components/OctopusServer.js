import { LitElement, html } from 'https://unpkg.com/@polymer/lit-element@latest/lit-element.js?module';
import  "./OctopusUser.js";

class OctopusServer extends LitElement {
    static get properties() {
        return {
            name: {type: String, reflect:true },
            timeElapsed: { type: String },
            headsets: {type: Object},
            mobileApps: {type: Object  },
        };
    }

    constructor(){
        super();
        console.log("OctopusServer constructor called");
        this.timeElapsed= "";
        this.headsets = []
        this.mobileApps = []
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
            <span>${ Object.keys( this.mobileApps ).length } mobile apps</span>
            <span>Last sync: ${this.timeElapsed}</span>
          </div>
        ${ this.headsets.map(u =>  ( u.status.app ? html`<octopus-user name="${ u.name }" globalImpedence="${ u.status.globalImpedence }" .headsetInfo= "${ u.status.info }"   synchSince="${u.status.app.since}" lastEmotion="${ u.status.app.state}" isMobileAppsConnected="${ u.status.clientSessionCreated } " isHeadsetConnected="${ u.status.connected } " ></octopus-user>` : html``) )}
        </div>`;
    }
}

window.customElements.define("octopus-server",OctopusServer);