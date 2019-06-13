import { LitElement, html } from 'https://unpkg.com/@polymer/lit-element@latest/lit-element.js?module';
import  "./OctopusUser.js";

class OctopusServer extends LitElement {
    static get properties() {
        return {
            name: {type: String, reflect:true},
            lastSyncDate: { type: Date },
            timeElapsed: { type: String },
            users: { type: Array},
            headsets: {type: Array },
            mobileApps: {type: Array }
        };
    }

    constructor(){
        super();
        console.log("OctopusServer constructor called");
        this.lastSyncDate= new Date();
        this.timeElapsed= "";
        this.users=[];
        this.headsets=[];
        this.mobileApps=[];
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
        <style>
          :host {
              display: block;
              font-family:sans-serif;
              display:flex;
          }
          .header{
              display:flex;
              flex-direction: column;
              min-width:200px;
              border:1px solid black;
          }
        
          .header > div {
            background-color: white;
            border: 1px solid #d3d3d3;
            margin: 2px;
            padding: 2px;
            display:flex;
            flex-direction: column;
        }
        
        .header > div > span {
            margin:5px;
        }
          .header > .title {
                background-image: url(../img/server.svg);
                background-color: #d3d3d3;
                margin: 2px;
                padding: 2px;
                background-position: left;
                background-size: 1em;
                background-repeat: no-repeat;
                padding-left: 1.2em;
           }
           octopus-user {
                margin:2px;
           }
        </style>
        <div class="header">
          <div class="title">Server ${this.name}</div>
          <div class="status">
            <span>Server ${this.name}</span>
            <span>${this.headsets.length} headsets</span>
            <span>${this.mobileApps.length} mobile apps</span>
            <span>Last sync: ${this.timeElapsed}</span>
          </div>
          ${this.users.map(u => html`<octopus-user  id="${u.id}" name="${u.name}"></octopus-user>`)}
          <button @click="${this.addFakeUser}">add user</button>
        </div>`;
    }

    addFakeUser(){
        this.users.push({'id':this.users.length,'name':'U'+this.users.length});
    }
}

window.customElements.define("octopus-server",OctopusServer);