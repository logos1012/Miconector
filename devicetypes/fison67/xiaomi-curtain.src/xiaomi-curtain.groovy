/**
 *  Xiaomi Curtain (v.0.0.2)
 *
 * MIT License
 *
 * Copyright (c) 2018 fison67@nate.com
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

import groovy.json.JsonSlurper

metadata {
	definition (name: "Xiaomi Curtain", namespace: "fison67", author: "fison67") {
        capability "Actuator"		
        capability "Door Control"
        capability "Switch Level"
        capability "Switch"
        capability "Refresh"
         
        attribute "lastCheckin", "Date"
	}


	simulator {
	}

	preferences {
	}

}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

def setInfo(String app_url, String id) {
	log.debug "${app_url}, ${id}"
	state.app_url = app_url
    state.id = id
}

def setStatus(params){
    log.debug "${params.key} : ${params.data}"
    
 	switch(params.key){
    case "curtainLevel":
    	sendEvent(name:"level", value: params.data )
        sendEvent(name:"switch", value: (params.data == "0" ? "off" : "on" ))
		sendEvent(name:"door", value: (params.data == "0" ? "closed" : "open" ))
    	break;
    }
    
    updateLastTime()
}

def setLevel(level){
	log.debug "setLevel >> ${state.id}, val=${level}"
    def body = [
        "id": state.id,
        "cmd": "curtainLevel",
        "data": level
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def on(){
	open()
}

def off(){
	close()
}

def close(){
	log.debug "Off >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "curtainLevel",
        "data": 0
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def open(){
	log.debug "Off >> ${state.id}"
	def body = [
        "id": state.id,
        "cmd": "curtainLevel",
        "data": 100
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
    
}

def refresh(){
	log.debug "Refresh"
    def options = [
     	"method": "GET",
        "path": "/devices/get/${state.id}",
        "headers": [
        	"HOST": state.app_url,
            "Content-Type": "application/json"
        ]
    ]
    sendCommand(options, callback)
}

def callback(hubitat.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
	
        sendEvent(name:"level", value: jsonObj.state.curtainLevel)
       
        updateLastTime()
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
}

def updateLastTime(){
	def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now, displayed:false)
}

def updated() {
}

def sendCommand(options, _callback){
	def myhubAction = new hubitat.device.HubAction(options, null, [callback: _callback])
    sendHubCommand(myhubAction)
}

def makeCommand(body){
	def options = [
     	"method": "POST",
        "path": "/control",
        "headers": [
        	"HOST": state.app_url,
            "Content-Type": "application/json"
        ],
        "body":body
    ]
    return options
}
