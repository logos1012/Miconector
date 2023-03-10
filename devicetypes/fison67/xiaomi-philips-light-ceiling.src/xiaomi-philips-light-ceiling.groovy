/**
 *  Xiaomi Philips Light Ceiling(v.0.0.3)
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
	definition (name: "Xiaomi Philips Light Ceiling", namespace: "fison67", author: "fison67") {
        capability "Switch"						//"on", "off"
        capability "Light"
        capability "Refresh"
		capability "ColorTemperature"
        capability "Switch Level"

        attribute "lastOn", "string"
        attribute "lastOff", "string"
        
        attribute "lastCheckin", "Date"
         
        command "setAutoColorOn"
        command "setAutoColorOff"
        command "setSmartNightLightOn"
        command "setSmartNightLightOff"
        
        command "setScene1"
        command "setScene2"
        command "setScene3"
        command "setScene4"
        
        command "setTimeRemaining"
        command "stop"
	}

	preferences {
		input name:	"smooth", type:"enum", title:"Select", options:["On", "Off"], description:"", defaultValue: "On"
        input name: "duration", title:"Duration" , type: "number", required: false, defaultValue: 500, description:""
	}

	simulator {
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
    log.debug "Status >> " + params.data
    def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
 	switch(params.key){
    case "power":
        if(params.data == "true"){
            sendEvent(name:"switch", value: "on")
            sendEvent(name: "lastOn", value: now)
        } else {
            sendEvent(name:"switch", value: "off")
            sendEvent(name: "lastOff", value: now)
        }
    	break;
    case "colorTemperature":
    	sendEvent(name:"colorTemperature", value: params.data as int)
    	break
    case "brightness":
    	sendEvent(name:"level", value: params.data )
    	break;
    case "smartNightLight":
    	sendEvent(name:"smartNightLight", value: params.data == "true" ? "on" : "off")
    	break;
    case "autoColorTemperature":
    	sendEvent(name:"autoColor", value: params.data == "true" ? "on" : "off")
    	break;
    }
    
    sendEvent(name: "lastCheckin", value: now)
}
def refresh(){
	log.debug "Refresh"
    def options = [
     	"method": "GET",
        "path": "/devices/get/${state.id}",
        "headers": [
        	"HOST": parent._getServerURL(),
            "Content-Type": "application/json"
        ]
    ]
    sendCommand(options, callback)
}

def setLevel(brightness){
	if(brightness < 0){
		brightness = 0	
	}else if(brightness > 100){
		brightness = 100	
	}
	log.debug "setBrightness >> ${state.id}, val=${brightness}"
    def body = [
        "id": state.id,
        "cmd": "brightness",
        "data": brightness
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setColorTemperature(colortemperature){
    def body = [
        "id": state.id,
        "cmd": "color",
        "data": colortemperature + "K",
        "subData": getDuration()
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
    
    setPowerByStatus(true)	
}

def on(){
	log.debug "Off >> ${state.id}"
    def body = [
        "id": state.id,
        "cmd": "power",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def off(){
	log.debug "Off >> ${state.id}"
	def body = [
        "id": state.id,
        "cmd": "power",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setAutoColorOn(){
	log.debug "setAutoColorOn >> ${state.id}"
    
    def body = [
        "id": state.id,
        "cmd": "autoColor",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setAutoColorOff(){
	log.debug "setAutoColorOff >> ${state.id}"
    
    def body = [
        "id": state.id,
        "cmd": "autoColor",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setSmartNightLightOn(){
	log.debug "setSmartNightLightOn >> ${state.id}"
    
    def body = [
        "id": state.id,
        "cmd": "smartNightLight",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setSmartNightLightOff(){
	log.debug "setSmartNightLightOff >> ${state.id}"
    
    def body = [
        "id": state.id,
        "cmd": "smartNightLight",
        "data": "off"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setScene1(){
	log.debug "setScene1 >> ${state.id}"
    
    def body = [
        "id": state.id,
        "cmd": "scene",
        "data": 1
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setScene2(){
	log.debug "setScene2 >> ${state.id}"
    
    def body = [
        "id": state.id,
        "cmd": "scene",
        "data": 2
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setScene3(){
	log.debug "setScene3 >> ${state.id}"
    
    def body = [
        "id": state.id,
        "cmd": "scene",
        "data": 3
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def setScene4(){
	log.debug "setScene4 >> ${state.id}"
    
    def body = [
        "id": state.id,
        "cmd": "scene",
        "data": 4
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}


def updated() {}

def callback(hubitat.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
       
        sendEvent("colorTemperature", jsonObj.properties.colorTemperature[0] as int)
        sendEvent(name:"level", value: jsonObj.properties.brightness)
        sendEvent(name:"switch", value: jsonObj.properties.power == true ? "on" : "off")
	    
        def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
        sendEvent(name: "lastCheckin", value: now)
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
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
        	"HOST": parent._getServerURL(),
            "Content-Type": "application/json"
        ],
        "body":body
    ]
    return options
}


def msToTime(duration) {
    def seconds = (duration%60).intValue()
    def minutes = ((duration/60).intValue() % 60).intValue()
    def hours = ( (duration/(60*60)).intValue() %24).intValue()

    hours = (hours < 10) ? "0" + hours : hours
    minutes = (minutes < 10) ? "0" + minutes : minutes
    seconds = (seconds < 10) ? "0" + seconds : seconds

    return hours + ":" + minutes + ":" + seconds
}

def stop() { 
	unschedule()
	state.timerCount = 0
	updateTimer()
}

def timer(){
	if(state.timerCount > 0){
    	state.timerCount = state.timerCount - 30;
        if(state.timerCount <= 0){
        	if(device.currentValue("switch") == "on"){
        		off()
            }
        }else{
        	runIn(30, timer)
        }
        updateTimer()
    }
//	log.debug "Left Time >> ${state.timerCount}"
}

def updateTimer(){
    def timeStr = msToTime(state.timerCount)
    sendEvent(name:"leftTime", value: "${timeStr}")
    sendEvent(name:"timeRemaining", value: Math.round(state.timerCount/60))
}

def processTimer(second){
	if(state.timerCount == null){
    	state.timerCount = second;
    	runIn(30, timer)
    }else if(state.timerCount == 0){
		state.timerCount = second;
    	runIn(30, timer)
    }else{
    	state.timerCount = second
    }
//    log.debug "Left Time >> ${state.timerCount} seconds"
    updateTimer()
}

def setTimeRemaining(time) { 
	if(time > 0){
        log.debug "Set a Timer ${time}Mins"
        processTimer(time * 60)
        setPowerByStatus(true)
    }
}

def getDuration(){
	def smoothOn = settings.smooth == "" ? "On" : settings.smooth
    def duration = 500
    if(smoothOn == "On"){
        if(settings.duration != null){
            duration = settings.duration
        }
    }
    return duration
}

def setPowerByStatus(turnOn){
	if(device.currentValue("switch") == (turnOn ? "off" : "on")){
        if(turnOn){
        	on()
        }else{
        	off()
        }
    }
}
