import { LitElement, html } from '../web_modules/lit-element.js';

class OctopusTimepicker extends LitElement {
    static get properties() {
        return {
            placeholder: {type: String, reflect:true },
            isConnected:{type: String, reflect:true },
        }
    }
    constructor(){
        super();
        this.placeholder = ""
        this.isConnected = ""
        this.id = ""
        this.defaults = { interval: 15, start: 0 }
        this.options =  null
        this.time =  { hour: 0, minute: 0 }
        this.active =  !1
        this.source =  null
        this.addEventListener("DOMContentLoaded", this.iniTime() )
        this.addEventListener("DOMContentLoaded", this.onClickEvent )
        this.addEventListener("mouseup", this.onClickEvent)
        this.addEventListener("keydown", this.onKeyEvents)
        this.setTime  = this.time.hour+':'+ ( this.time.minute == 0 ? '00' :  this.time.minute )
    }
    getSelectedTime(){
        if( !this.shadowRoot.getElementById( this.id+'-timepicker').value ){
            return '00:00'
        } else {
            return this.shadowRoot.getElementById( this.id+'-timepicker').value
        }
       
    }
    iniTime( e ){
        var self = this
        setTimeout ( function(e){
            self.load( {
                interval : 1
           })
        }, 1000)
        self.removeDiv()
    }
    load( e ) {
        var t = this;
        this.options = e,
        t.each(function(e) {
            e.onclick = function() {
                t.source = this,
                1 != t.active && t.showHour(this, 1, 24, function(e, i) {
                    t.time.hour = i,
                    t.source.value = t.time.hour + ":00",
                    t.showMinute(e, t.option("interval"), 60, t.time.hour, function(e) {
                        t.time.minute = e,
                        t.source.value = t.time.hour + ":" + t.time.minute
                    })
                })
            }
        })
    }
    option(e) {
        return null === this.options ? this.defaults[e] : void 0 === this.options[e] ? this.defaults[e] : this.options[e]
    }
    each(e) {
        for (var t = this.shadowRoot.querySelectorAll('[data-toggle="timepicker"]'), i = 0; i < t.length; i++)
            e(t[i])
    }
    removeDiv( e) { 
        var self = this
        window.addEventListener("mousedown", function (e) {
            if( self.id  ){
                let dropdown = self.shadowRoot.querySelector("div#"+self.id+"-timepicker" )
                if ( self.shadowRoot.querySelectorAll("div#"+self.id+"-timepicker") ){
                    if( e.path ){
                        if( !self.shadowRoot.contains( e.path[0] ) ){
                            self.shadowRoot.querySelectorAll("div#"+self.id+"-timepicker").forEach( node  =>  {
                                node.parentNode.removeChild( node );
                            });
                        } 
                    }
                    if(  e.composedPath() ){
                        if( !self.shadowRoot.contains( e.composedPath()[0] ) ){
                            self.shadowRoot.querySelectorAll("div#"+self.id+"-timepicker").forEach( node  =>  {
                                node.parentNode.removeChild( node );
                            });
                        }
                    }
                   
                }
            }
          });
    }
    remove() {
        this.shadowRoot.removeEventListener("mouseup", this.onClickEvent)
        this.shadowRoot.removeEventListener("keydown", this.onKeyEvents)
        this.active = !1
    }
    clear(e) {
        var all = e.querySelectorAll("li");
        for (var t = 0; t < all.length; t++)
            all[t].remove()
    }
    showHour(e, t, i, o) {
        let divtemplate = document.createElement('div');
        let ul = document.createElement('ul');
        ul.setAttribute("data-value", "null")
        divtemplate.setAttribute("class", "timepicker")
        divtemplate.setAttribute("id", this.id+"-timepicker")
        divtemplate.setAttribute("data-name", e.getAttribute("name")),
        divtemplate.appendChild(ul)
        var div = this.shadowRoot.appendChild(divtemplate)
        var li
        var self = this

        for (var l = self.option("start"); i > l; l += t){
            var litemplate = document.createElement('li');
            li = this.shadowRoot.appendChild(litemplate),
            li.setAttribute("href", "#"),
            li.setAttribute("data-value", this.pad(l)),
            li.textContent =  self.pad(l) + ":00"
            ul.appendChild(li)
            li.onmouseover = function() {
                var all = self.shadowRoot.querySelectorAll("div#"+self.id+"-timepicker ul li");
                for (var e = 0; e < all.length; e++)
                    all[e].classList.remove("hover");
                    self.classList.add("hover")
            }
            ,
            li.onmouseout = function() {
                this.classList.remove("hover")
            }
            ,
            li.onclick = function() {
                o(ul, this.getAttribute("data-value"))
            }
        }
    }
    showMinute(e, t, i, o, l) {
        self = this,
        this.clear(e);
        var li 
        let ultemplate = document.createElement('ul');

        for (var n = 0; i > n; n += t){
            var litemplate = document.createElement('li');
            var self = this

            li = this.shadowRoot.appendChild(litemplate)
            li.setAttribute("href", "#")
            li.setAttribute("data-value", self.pad(n))
            li.textContent = o + ":" + self.pad(n)
            ultemplate.appendChild(li)
            this.shadowRoot.querySelector("div#"+self.id+"-timepicker ul").appendChild( li )
            li.onmouseover = function() {
               var all = self.shadowRoot.querySelector("div#"+self.id+"-timepicker ul li");
                for (var e = 0; e < all.length; e++)
                    all[e].classList.remove("hover");
                this.classList.add("hover")
            }
            
            li.onmouseout = function() {
                this.classList.remove("hover")
            }
            
            li.onclick = function() {
                l(this.getAttribute("data-value")),
               // this.remove()
                self.shadowRoot.querySelector("div#"+self.id+"-timepicker").remove()
            }
        }
    }
    pad(e) {
        for (var t = String(e); t.length < 2; )
            t = "0" + t;
        return t
    }
    onClickEvent( event ) {
        var self = this
        void 0 === event.target.attributes["data-value"] && self.remove()
    }
    onKeyEvents( event ) {
        var self = this
        switch (event.keyCode) {
        case 27:
            self.remove();
            break;
        case 40:
            if (  item = this.shadowRoot.querySelector("div.timepicker ul li.hover"),
            list = this.shadowRoot.querySelectorAll("div#"+self.id+"-timepicker ul li"),
            index = Array.prototype.indexOf.call(list, item),
            -1 == index) {
                list[0].classList.add("hover");
                break
            }
            index < list.length - 1 && (item.classList.remove("hover"),
            list[index + 1].classList.add("hover"));
            break;
        case 38:
            var item = this.shadowRoot.querySelector("div#"+self.id+"-timepicker ul li.hover")
            var list = this.shadowRoot.querySelectorAll("div#"+self.id+"-timepicker ul li")
            var index = Array.prototype.indexOf.call(list, item)
            index > 0 && (item.classList.remove("hover"),
            list[index - 1].classList.add("hover"));
            break;
        case 13:
            var item = this.shadowRoot.querySelector("div#"+self.id+"-timepickerul li.hover")
            if( item ){
                item.click()
            } else {
               this.shadowRoot.querySelector("div#"+self.id+"-timepicker ul li").remove() 
            }
        }
    }
    render(){
        return html`
            <link rel="stylesheet" href="./css/timepicker.css">
            <input type="text" id="${ this.id }-timepicker" placeholder="${ this.placeholder }" @change="${ (event) => this.setTime = event.target.value}"  name="timepicker" data-toggle="timepicker" style="width:100%" class="${this.isConnected}">
        `;
    }
}
window.customElements.define("octopus-timepicker",OctopusTimepicker);

