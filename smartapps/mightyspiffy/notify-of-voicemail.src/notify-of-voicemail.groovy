/**
 *  Notify of Voicemail
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
    name: "Notify of Voicemail",
    namespace: "mightyspiffy",
    author: "Joshua Matthews",
    description: "This kicks off a notification when a person arrives home whether they have a voicemail or not.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Title") {
		input "voicemailSwitch","capability.switch", title: "Voicemail Switch", require: true, multiple: false
        input "presenceSensor","capability.presenceSensor", title: "Presence Sensors", require: true, multiple: true
        input "timeDelay", "number", title: "How many minutes to delay", required: true
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
	log.debug "Suscribing to $presenceSensor[0].displayName"
	subscribe(presenceSensor, "presence", presenceHandler)
}

def presenceHandler(evt) {
	log.debug "$evt.descriptionText delay notification by $timeDelay.value"
	if ("present" == evt.value && "on" == voicemailSwitch.currentSwitch) {
    	state.message = "Welcome home $evt.displayName, you have voicemail"
        log.debug "Message: $state.message"
        runIn(timeDelay*60,notifyUser)
    	voicemailSwitch.off()
    }
}

def notifyUser() {
	if (sendPush) {
    	sendPush(state.message)
    }
    if (location.contactBookEnabled && recipients) {
        log.debug "contact book enabled!"
        sendNotificationToContacts(state.message, recipients)
    } else {
        log.debug "contact book not enabled"
        if (phone) {
            log.debug "Sending SMS to $phone.value: $state.message"
            sendSms(phone, state.message)
        }
    }
}