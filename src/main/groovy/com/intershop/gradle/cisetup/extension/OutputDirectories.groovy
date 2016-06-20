/*
 * Copyright 2015 Intershop Communications AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.intershop.gradle.cisetup.extension

import groovy.transform.CompileStatic

/**
 * Configuration extension for output directories
 */
@CompileStatic
class OutputDirectories {

	final static String defaultSetupDirectory = 'ishCISetup'
	
	String setupDirectory
	
	String devOpsDir
	
	String projectsDir
	
	String getSetupDirectory() {
		if(setupDirectory) {
			return setupDirectory
		} else {
			return defaultSetupDirectory
		}
	} 
	
	String getDevOpsDir() {
		if(devOpsDir) {
			return devOpsDir
		} else {
			return "${getSetupDirectory()}/devops"
		}
	}

	String getProjectsDir() {
		if(projectsDir) {
			return projectsDir
		} else {
			return "${getSetupDirectory()}/projects"
		}
	}
}
