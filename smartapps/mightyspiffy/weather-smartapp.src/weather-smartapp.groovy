/**
 *  Weather SmartApp
 *
 *  Copyright 2016 Joshua Matthews
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Weather SmartApp",
    namespace: "mightyspiffy",
    author: "Joshua Matthews",
    description: "Test of Weather SmartApp",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Title") {
		input "outsideTemp","capability.temperatureMeasurement",title:"Weather Source",required:true
        input "days", "enum", title:"Run Days", required: true, multiple:true, options: weekdays() + weekend()
		input "runAt","time",title:"Run Time",required:true
        input "sendPush", "bool", required: false, title: "Send push Notifications?"
        input ("recipients", "contact", title: "Send notification to...") {
            input "phone", "phone", title: "Warn with text message (optional)",
                description: "Phone Number", required: false
        }
	}
}

def weekdays() {
	return ["Monday","Tuesday","Wednesday","Thursday","Friday"]
}

def weekend() {
	return ["Saturday","Sunday"]
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	schedule(runAt,weatherHandler)
}

def weatherHandler() {
	def today = new Date().format("EEEE", timeZone(runAt))
    def hour = new Date().format("HH", timeZone(runAt))
    log.debug "Running: today<${today}>, days<${days}> : hour<${hour}>"
	if (!days || days.contains(today)) {
        def feature = getWeatherFeature("forecast")
        def outString
        if (hour.toInteger() < 10) {
            outString = "Today's forecast is " + feature?.forecast.txt_forecast.forecastday[0]?.fcttext + 
                " Tonight's forecast is " + feature?.forecast.txt_forecast.forecastday[1]?.fcttext +
                " The current temperature is " + outsideTemp?.currentTemperature + " degrees Fahrenheit."
            log.debug "Morning Message: ${outString}"
        } else {
        	outString = "Tomorrow's forecast is " + feature?.forecast.txt_forecast.forecastday[2]?.fcttext + 
                " Tomorrow night's forecast is " + feature?.forecast.txt_forecast.forecastday[3]?.fcttext +
                " The current temperature is " + outsideTemp?.currentTemperature + " degrees Fahrenheit."
            log.debug "Afternoon Message: ${outString}"
        }
        outString = outString.replace("F."," degrees Fahrenheit.")
        
        outString = outString.replaceAll(" N "," out of the north ")
        
        outString = outString.replaceAll(" NNE "," out of the north north east ")
        outString = outString.replaceAll(" NE "," out of the north east ")        
        outString = outString.replaceAll(" ENE "," out of the east north east ")        
        
        outString = outString.replaceAll(" E "," out of the east ")
        
        outString = outString.replaceAll(" ESE "," out of the east south east ")        
        outString = outString.replaceAll(" SE "," out of the south east ")
        outString = outString.replaceAll(" SSE "," out of the south south east ")
        
        outString = outString.replaceAll(" S "," out of the south ")

        outString = outString.replaceAll(" SSW "," out of the south south west ")
        outString = outString.replaceAll(" SW "," out of the south west ")
        outString = outString.replaceAll(" WSW "," out of the west south west ")


		outString = outString.replaceAll(" W "," out of the west ")
        
        outString = outString.replaceAll(" WNW "," out of the west north west ")
        outString = outString.replaceAll(" NW "," out of the north west ")
        outString = outString.replaceAll(" NNW "," out of the north north west ")
        
        
        if (sendPush) {
            sendPush(outString)
        }
        if (location.contactBookEnabled && recipients) {
            log.debug "contact book enabled!"
            sendNotificationToContacts(outString, recipients)
        } else {
            log.debug "contact book not enabled"
            if (phone) {
                log.debug "Sending SMS to $phone.value: $outString"
                sendSms(phone, outString)
            }
        }
        log.debug outString
    } else {
    	log.debug "Not running today"
    }
}