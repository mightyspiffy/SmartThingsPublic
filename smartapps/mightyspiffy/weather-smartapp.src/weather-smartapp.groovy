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
		input "outsideTemp","capability.temperatureMeasurement",title:"Weather Source1",required:true
		input "runAt","time",title:"Run Time",required:true
        input "sendPush", "bool", required: false, title: "Send push Notifications?"
        input ("recipients", "contact", title: "Send notification to...") {
            input "phone", "phone", title: "Warn with text message (optional)",
                description: "Phone Number", required: false
        }
	}
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
	def feature = getWeatherFeature("forecast")
    def outString = "Today's forecast is " + feature?.forecast.txt_forecast.forecastday[0].fcttext + 
    	" Tonight's forecast is " + feature?.forecast.txt_forecast.forecastday[1].fcttext +
        " The current temperature is " + outsideTemp.currentTemperature + "."
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
}