import { LitElement, html } from 'https://unpkg.com/@polymer/lit-element@latest/lit-element.js?module';
import  "./OctopusUser.js";

class OctopusServer extends LitElement {
    static get properties() {
        return {
            name: {type: String, reflect:true},
            lastSyncDate: { type: Date },
            timeElapsed: { type: String },
            users: { type: Array},
            headsets: {type: Object},
            mobileApps: {type: Object  }
        };
    }

    constructor(){
        super();
        console.log("OctopusServer constructor called");
        this.lastSyncDate= new Date();
        this.timeElapsed= "";
        this.users=[];
        this.apps=[];
        this.init();
    }

    init(){
      setInterval(()=>{
        const d=new Date();
        this.timeElapsed=(d.getHours()+':'+d.getMinutes()+':'+d.getSeconds()+".").replace(/(^|:)(\d)(?=:|\.)/g, '$10$2');
      },100);
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
          ${ this.mobileApps.map(u => html`<octopus-user  id="${u.id}" name="${u.name}" .lastEmotion="${u.status }"></octopus-user>`)}
        </div>`;
    }
}

window.customElements.define("octopus-server",OctopusServer);