import { LitElement, html } from 'https://unpkg.com/@polymer/lit-element@latest/lit-element.js?module';


class OctopusUser extends LitElement {
    static get properties() {
        return {
            id: { type: String ,reflect:true},
            name: {type: String, reflect:true},
            lastSyncDate: { type: Date },
            isMobileAppsConnected  : { type : String, reflect :true },
            isHeadsetConnected  : { type : String, reflect :true },
            timeElapsed: { type: String },
            impedance: { type: Number },
            ip: { type: String},
            mobileApps: { type : Object },
            endpointsWebApi: {type: Object },
            lastEmotion  : { type : String , reflect : true},
            synchSince  : { type : Date , reflect : true},
            headsetInfo  : {type: Object , reflect  : true }, 


        };
    }

    constructor(){
        super();
        console.log("OctopusUser constructor called");
        this.lastSyncDate = new Date();
        this.timeElapsed= "";
        this.synchSince =  ""
        this.lastEmotion =  ""
        this.headsetInfo = {}
        this.impedance=98;
        this.ip="127.0.0.1";
        this.serverWebAPI="http://localhost:9998/rest",
        this.isHeadsetConnected = '';
        this.isMobileAppsConnected = '';
        this.showInfoClass = 'display:none'
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
          const d=new Date( parseInt(this.synchSince ) );
          this.timeElapsed=(d.getHours()+':'+d.getMinutes()+':'+d.getSeconds()+".").replace(/(^|:)(\d)(?=:|\.)/g, '$10$2');
        },1000);
      }
    showInfo(){
        this.showInfo = 'display: block'
    }
    render(){
        return html`
        <link rel="stylesheet" href="./css/style.css">
        </style>
        <span class="header">
        <div class="title">User ${this.name}</div>
            <span style="display:flex;justify-content:center;align-items:center; padding:5px">
                <img src="../img/headset.svg" width="25" height="25" style="margin:5px" class="${ ( this.isHeadsetConnected ) }" >
                <img src="../img/mobile.svg" width="25" height="25" style="margin:5px" class="${ ( this.isMobileAppsConnected ) }">
            </span>
            <div class="status" >
                <span>Last emotion: <strong> ${this.lastEmotion.replace(/_/g, " ") } </strong></span>
                <span>Last sync: ${this.timeElapsed}</span>
                <span>Impedance: ${this.impedance}</span>
                ${ ( Object.keys(this.headsetInfo).length > 0  ? 
                    html`<a href="#" @click=${ this.showInfo }> show </a>
                    <div style="padding:10px; background:#e2e2e2; margin:15px ; ${ this.showInfoClass }">
                        ${ Object.keys(this.headsetInfo).map( (value, index ) =>  html` <div style="padding:2px"> ${ value} : ${ index }</div>  ` )}
                    </div>`    
                    : '' )  }
                
                <button @click="${ this.generateReport }"> Generate report </button>
            </div>
        </div>
        `;
    }
    showInfo(e){
        e.preventDefault();
        this.showInfoClass = 'display: block'
    }
    generateReport(){;
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
            xhttp.open("GET", 'rest'+this.endpointsWebApi.generateReport+'?headset_id='+this.name+'&from=00:00&to=23:00' );
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