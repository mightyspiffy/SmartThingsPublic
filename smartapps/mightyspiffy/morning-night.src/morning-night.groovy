/**
 *  Morning/Night
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
    name: "Morning/Night",
    namespace: "mightyspiffy",
    author: "Joshua Matthews",
    description: "Morning/Nighttime Routine",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Title") {
		input "lights", "capability.colorControl", title: "Pick RGBW Lights", required: true, multiple: true
        input name: "time1", type: "time", title: "Run Time"
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
	state.runNumer = 0
	state.hue = 0
	//schedule("* 0/1 * * * ?", timeEvent)
    runIn(1,timeEvent)
}

def timeEvent(evt) {
    def tempHue = 0
    	
    lights.each { light ->
        if ("off" == light.switch) {
            light.on()
        }
        light.setSaturation(100)
        light.setLevel(100)
        log.debug "Setting Hue to ${state.hue}"
        light.setHue(state.hue)
		state.hue = (state.hue + 10) % 100
    }
    runIn(2,timeEvent)
}