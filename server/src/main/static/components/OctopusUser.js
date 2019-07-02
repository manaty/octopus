import { LitElement, html } from '../web_modules/lit-element.js';


class OctopusUser extends LitElement {
    static get properties() {
        return {
            id: { type: String ,reflect:true},
            name: {type: String, reflect:true},
            lastSyncDate: { type: Date },
            isMobileAppsConnected  : { type : String, reflect :true },
            isHeadsetConnected  : { type : String, reflect :true },
            timeElapsed: { type: String },
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
    showInfo(e){
        e.preventDefault()
        if (  this.showInfoClass == 'display: inline-block' ){
            this.showInfoClass = 'display: none'
        } else {
            this.showInfoClass = 'display: inline-block'
        }
    }
    getHours(){
        let hours = []
        for( let i = 0; i <= 23; i++) {
          let hour = i
          let mins , temh , ampm  , hourText
          for( let x = 0; x <=  59; x ++ ){
                if( x < 10){
                    x = '0'+x
                }
                mins = x
               
                if( i > 11  ){
                  temh = ( i < 10 ? '0'+i : ( i > 12 ? i -12 : i )  )
                  ampm = 'PM'
                } else {
                  temh =  ( i < 10 ? '0'+i : ( i > 12 ? i -12 : i )  )
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
            <div class="card">
                <div class="header user">
                <div class="title">User ${this.name}</div>
                </div>
                <div class="body">
                        <div style="display:flex;justify-content:center;align-items:center; padding:5px">
                            <svg viewBox="0 0 512 512" xmlns="http://www.w3.org/2000/svg" width="25" height="25">
                                <path fill="${ ( this.isHeadsetConnected.trim() == 'true' ? '#00ff00' : '#ff0000' )  }"  d="M192 288h-48c-35.35 0-64 28.7-64 64.12v63.76c0 35.41 28.65 64.12 64 64.12h48c17.67 0 32-14.36 32-32.06V320.06c0-17.71-14.33-32.06-32-32.06zm-16 143.91h-32c-8.82 0-16-7.19-16-16.03v-63.76c0-8.84 7.18-16.03 16-16.03h32v95.82zM256 32C112.91 32 4.57 151.13 0 288v112c0 8.84 7.16 16 16 16h16c8.84 0 16-7.16 16-16V288c0-114.67 93.33-207.8 208-207.82 114.67.02 208 93.15 208 207.82v112c0 8.84 7.16 16 16 16h16c8.84 0 16-7.16 16-16V288C507.43 151.13 399.09 32 256 32zm112 256h-48c-17.67 0-32 14.35-32 32.06v127.88c0 17.7 14.33 32.06 32 32.06h48c35.35 0 64-28.71 64-64.12v-63.76c0-35.41-28.65-64.12-64-64.12zm16 127.88c0 8.84-7.18 16.03-16 16.03h-32v-95.82h32c8.82 0 16 7.19 16 16.03v63.76z" class=""></path>
                            </svg>
                            <svg viewBox="0 0 320 512" xmlns="http://www.w3.org/2000/svg" width="25" height="25">
                                <path fill="${ ( this.isMobileAppsConnected.trim() == 'true' ? '#00ff00' : '#ff0000'  )  }" fill="currentColor" d="M272 0H48C21.5 0 0 21.5 0 48v416c0 26.5 21.5 48 48 48h224c26.5 0 48-21.5 48-48V48c0-26.5-21.5-48-48-48zM160 480c-17.7 0-32-14.3-32-32s14.3-32 32-32 32 14.3 32 32-14.3 32-32 32zm112-108c0 6.6-5.4 12-12 12H60c-6.6 0-12-5.4-12-12V60c0-6.6 5.4-12 12-12h200c6.6 0 12 5.4 12 12v312z" class=""></>
                            </svg>
                        </div>
                        <p>Last emotion: <strong> ${this.lastEmotion.replace(/_/g, " ") } </strong></p>
                        <p>Last sync: ${this.timeElapsed}</p>
                        <p>Impedance:${ ( this.globalImpedence ? html ` <span class="bold-red"> ${ this.globalImpedence } %</span>` : this.globalImpedence   ) }  </p>
                        <div class="relative">
                        ${ ( this.globalImpedence > 0 ? 
                            ( Object.keys(this.headsetInfo).length > 0 
                               ? html`<a href="#" @click=${ this.showInfo } class="info-icon"> 
                                    <img src="../img/info.svg" width="15" height="15">  </a>
                                    <div class="impedence-container" style=" ${ this.showInfoClass }">
                                        ${ Object.entries(this.headsetInfo).map( (value, index ) => 
                                            ( value[0] != 'battery' && value[0] != 'signal' ? 
                                                html` <div class="w50 pull-left center"> ${ value[0] }  <div class="pull-right"> : </div></div><div class="w50 pull-left center">  
                                                    ${ ( value[1] < 4 
                                                        ? html` <span class="bold-red">${ value[1]}</span>`
                                                        : value[1] ) } </div> </div>  ` 
                                            : html`` )
                                        )}
                                    </div>`
                                : '' )
                            : '' )  }
                        </div>
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
                        <div class="block center">
                            <button @click="${ this.generateReport } " data-args="${ this.name }">Generate reports</button>
                        </div>
                </div>
            </div>
            `;
        }
}

window.customElements.define("octopus-user",OctopusUser);