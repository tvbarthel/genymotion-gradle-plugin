/*
 * Copyright (C) 2015 Genymobile
 *
 * This file is part of GenymotionGradlePlugin.
 *
 * GenymotionGradlePlugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version
 *
 * GenymotionGradlePlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GenymotionGradlePlugin.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.genymotion.tasks

import com.genymotion.tools.GMToolException
import com.genymotion.tools.Log
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class GenymotionFinishTask extends DefaultTask {

    String flavor = null

    @TaskAction
    def exec() {

        Log.info("Finishing devices")
        project.genymotion.getDevices(flavor).each() {
            processDeviceEnd(it)
        }
    }

    def processDeviceEnd(device) {
        Log.info("Finishing ${device.name}")
        if (device.start) {

            try {
                if (device.isRunning()) {
                    device.logcatDump()
                    device.pushAfter()
                    device.pullAfter()
                    device.stopWhenFinish()
                }
                device.deleteWhenFinish()

            } catch (Exception e) {
                e.printStackTrace()
                Log.info("Stopping all launched devices and deleting when needed")

                project.genymotion.getDevices(flavor).each() {
                    device.stopWhenFinish()
                    device.deleteWhenFinish()
                }
                //then, we thow a new exception to end task, if needed
                if (project.genymotion.config.abortOnError) {
                    throw new GMToolException("GMTool command failed. " + e.getMessage())
                }
            }
        }
    }
}
