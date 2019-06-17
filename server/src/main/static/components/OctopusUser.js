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
            endpointsWebApi: {type: Object },

        };
    }

    constructor(){
        super();
        console.log("OctopusUser constructor called");
        this.lastSyncDate= new Date();
        this.timeElapsed= "";
        this.lastEmotion = [];
        this.impedance=98;
        this.ip="127.0.0.1";
        this.serverWebAPI="http://localhost:9998/rest",
        this.endpointsWebApi = {
            list: '/ws/admin',
            start: '/admin/experience/start',
            stop: '/admin/experience/stop',
            trigger : '/admin/trigger',
            generateReport: '/report/generate',
            gerReport: '/report/get'
         }
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
        </style>
        <div class="header">
        <div class="title">User ${this.name}</div>
        <span style="display:flex;justify-content:center;align-items:center;">
            <span style="background-image: url(../img/headset.svg);background-repeat:no-repeat;min-width:24px;min-height:24px;margin:5px"></span>
            <span style="${this.ip?'':'border:1px solid red;'}background-image: url(../img/mobile.svg);background-repeat:no-repeat;min-width:12px;min-height:20px;margin:5px"></span>
        </span>
        <div class="status">
            <span>Impedance:  ${this.impedance}</span>
            <span>Last emotion: ${this.lastEmotion.state }</span>
            <span>Last sync: ${this.timeElapsed}</span>
            <button @click="${ this.generateReport }"> generate report </button>
        </div>
        
        </div>
        `;
    }
    generateReport(){
        try{
            let xhttp = new XMLHttpRequest();
            let self = this
            let apiExperience = this.endpointsWebApi
            
            xhttp.onreadystatechange = function ( res ) {
                if (this.readyState === 4) {
                    if (this.status === 200) {
                        let res =  JSON.parse( this.response  );
                        let reports = Object.entries( res )
            
                        for( let [ key, report ] of reports ) {
                            console.log( key, report )
                            window.open( self.serverWebAPI+'/report/get/'+report )
                          }

                    } else if (this.response == null && this.status === 0) {
                        document.body.className = 'error offline';
                        console.log("The computer appears to be offline.");
                    } else {
                        document.body.className = 'error';
                    }
                }
            };
            xhttp.open("GET", 'rest'+this.endpointsWebApi.generateReport+'?headset_id='+this.name+'&from=10:30&to=23:00' );
            xhttp.send()
            xhttp.onload = function(response ) {
            if (xhttp.status != 200) { 
                alert(`Error ${xhttp.status}: ${xhttp.statusText}`);
                self.startFlag = false
            } else {
                console.log( 'this',response )
            }
            };
            xhttp.onerror = function( message ) {
            alert( message );
            };
    
        } catch( e ){
            console.log( e )
        }
        }
    

}

window.customElements.define("octopus-user",OctopusUser);