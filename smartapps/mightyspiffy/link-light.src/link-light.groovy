/**
 *  Link Light
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
    name: "Link Light",
    namespace: "mightyspiffy",
    author: "Joshua Matthews",
    description: "Links lamps to switches",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Settings") {
		input "masterSwitchLevel", "capability.switchLevel", title: "Master Switch", required: true, multiple: false
		input "slaveSwitches", "capability.switchLevel", title: "Slave Switches", required: true, multiple: true
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
	subscribe(masterSwitchLevel,"level",masterSwitchEvent)
}

def masterSwitchEvent(evt) {
	log.debug "Master Switch Event Occured: $evt.value"
	slaveSwitches.each { switchLevel -> 
    	log.debug "Setting the Value for $switchLevel.name"
    	switchLevel.setLevel(evt.value)
    }
}