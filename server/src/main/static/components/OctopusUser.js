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
            globalImpedence  : {type: String , reflect  : true }, 
            


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
         this.hours = this.getHours()

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
   
    showInfo(e){
        e.preventDefault()
        if (  this.showInfoClass == 'display: block' ){
            this.showInfoClass = 'display: none'
        } else {
            this.showInfoClass = 'display: block'
        }
    }
    getHours(){
        let hours = []
        for( let i = 0; i <= 24; i++) {
          let hour = i
          let mins , temh , ampm  , hourText
          for( let x = 0; x <=  59; x ++ ){
                if( x < 10){
                    x = '0'+x
                }
                mins = x
                temh = i
                if( i > 12  ){
                  temh = i - 12
                  ampm = 'PM'
                } else {
                  temh = i
                  ampm = 'AM'
                }
                hour = i+':'+mins
                hourText = temh+':'+mins+' '+ampm
                hours.push({ value : hour, valueText : hourText })
          }
        }
        return hours 
     }
    generateReport(e){
        let params =  e.target.getAttribute('data-args')
        let from=  this.shadowRoot.getElementById( params+"-from") 
        let fromTime = from.options[from.selectedIndex].value 

        let to =  this.shadowRoot.getElementById( params+"-to") 
        let toTime = to.options[to.selectedIndex].value
         
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
            xhttp.open("GET", 'rest'+this.endpointsWebApi.generateReport+'?headset_id='+this.name+'&from='+fromTime+'&to='+toTime );
            xhttp.send()
            xhttp.onload = function(response ) {
                if (xhttp.status != 200) { 
                    alert(`Error ${xhttp.status}: ${xhttp.statusText}`);
                    self.startFlag = false
                }
            };
            xhttp.onerror = function( message ) {
            alert( message );
            };
    
        } catch( e ){
            console.log( e )
            }
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
                    <div>Last emotion: <strong> ${this.lastEmotion.replace(/_/g, " ") } </strong></div>
                    <div>Last sync: ${this.timeElapsed}</div>
                    <div class="relative">Impedance: ${ ( this.globalImpedence ? html ` <span class="bold-red"> ${ this.globalImpedence } %</span>` : this.globalImpedence   ) }  </span>
                    ${ ( Object.keys(this.headsetInfo).length > 0  ? 
                        html`<a href="#" @click=${ this.showInfo } class="info-icon"> <img src="../img/info.svg" width="15" height="15">  </a>
                        <div style="padding:10px; background:#e2e2e2; margin:15px ; ${ this.showInfoClass }">
                            ${ Object.entries(this.headsetInfo).map( (value, index ) => 
                                ( value[0] != 'battery' && value[0] != 'signal' ? html` <div style="padding:2px"> ${ value[0] } : ${ ( value[1] < 4 ? html` <span class="bold-red">${ value[1]}</span>`:  value[1] ) }</div>  ` : html`` )
                                 )}
                        </div>`    
                        : '' )  }
                    <div class="block "> 
                        <div class="w50  pull-left">
                            <label name="${ this.name}-from"> From <label> 
                            <select for="${ this.name}-from" id="${ this.name}-from"> ${ this.hours.map( u => html `<option value="${u.value}"> ${ u.valueText }</option>`) } </select>
                        </div>
                        <div class="w50 pull-left"> 
                            <label name="${ this.name}-to"> To </label> 
                            <select id="${ this.name}-to" for="${ this.name}-to"> ${ this.hours.map( u => html `<option value="${u.value}" > ${ u.valueText }</option>`) } </select>
                        </div>
                    </div>
                    <button @click="${ this.generateReport } " data-args="${ this.name }">Generate reports</button>
                </div>
            </div>
            `;
        }
}

window.customElements.define("octopus-user",OctopusUser);