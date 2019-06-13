import { LitElement, html } from 'https://unpkg.com/@polymer/lit-element@latest/lit-element.js?module';


class OctopusUser extends LitElement {
    static get properties() {
        return {
            id: { type: String ,reflect:true},
            name: {type: String, reflect:true},
            lastSyncDate: { type: Date },
            timeElapsed: { type: String },
            impedance: { type: Number },
            ip: { type: String},
            lastEmotion: { type: String}
        };
    }

    constructor(){
        super();
        console.log("OctopusUser constructor called");
        this.lastSyncDate= new Date();
        this.timeElapsed= "";
        this.impedance=98;
        this.ip="127.0.0.1";
        this.lastEmotion="neutre";
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
    <style>
      :host {
          display: block;
          font-family:sans-serif;
          display:flex;
      }
      .header{
          display:flex;
          flex-direction: column;
          justify-content:flex-start;
          min-width:200px;
          border:1px solid black;
      }
    
      .header > div {
        background-color: white;
        border: 1px solid #d3d3d3;
        display:flex;
        flex-direction: column;
    }
    
    .header > div > span {
        margin:5px;
    }
    
    
      .header > .title {
            background-image: url(../img/user.svg);
            background-color: #d3d3d3;
            margin: 2px;
            padding: 2px;
            background-position: left;
            background-size: 1em;
            background-repeat: no-repeat;
            padding-left: 1.2em;
       }
    }
    
    </style>
    <div class="header">
      <div class="title">User ${this.name}</div>
      <span style="display:flex;justify-content:center;align-items:center;">
        <span style="background-image: url(../img/headset.svg);background-repeat:no-repeat;min-width:24px;min-height:24px;margin:5px"></span>
        <span style="${this.ip?'':'border:1px solid red;'}background-image: url(../img/mobile.svg);background-repeat:no-repeat;min-width:12px;min-height:20px;margin:5px"></span>
      </span>
      <div class="status">
        <span>Impedance:  ${this.impedance}</span>
        <span>Last emotion: ${this.lastEmotion}</span>
        <span>Last sync: ${this.timeElapsed}</span>
      </div>
    
    </div>
    `;
}

}

window.customElements.define("octopus-user",OctopusUser);