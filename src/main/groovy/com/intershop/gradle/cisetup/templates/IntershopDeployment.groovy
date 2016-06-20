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
package com.intershop.gradle.cisetup.templates

import com.intershop.gradle.cisetup.util.ProjectTemplate
import org.gradle.api.tasks.Input

/**
 * Template task of a Intershop project deployment
 */
class IntershopDeployment extends AbstractTemplate {

	@Input
	String assemblyGroup = 'com.corporate.assembly'
	
	@Input
	String assemblyName = 'assembly-name'
	
	@Input
	String assemblyVersion = '1.0.0.0'
	
	@Input
	String toolsVersion

	@Input
	String bootstrapVersion
	
	@Input
	String projectName = 'corporateshop'

	@Input
	String hostName
	
	static void createBase(String path, Properties props, String name) {
		ProjectTemplate.fromRoot(path) {
			"${name}" {
				'gradle' {
					'wrapper' {
						'gradle-wrapper.jar' 		resourcePath: '/resources/gradle-wrapper.jar'
						'gradle-wrapper.properties' template: '/resources/corporate/gradle-wrapper.properties.tpl', 
													templateProperties: props 
					}
				}
				'gradlew' 		resourcePath: '/resources/gradlew', permissions: 'rwxr-xr-x'
				'gradlew.bat' 	resourcePath: '/resources/gradlew.bat'
				
				'settings.gradle' template: '/templates/deployment/settings.gradle.tpl',
								  templateProperties: props
			}
		}
	}


	static void createCIServerExample(String path, String hostName) {
		ProjectTemplate.fromRoot(path) {
			'ci_server' {
				'host_configs' {
					"${hostName}" {
						'environment.properties' resourcePath: '/templates/ciserver/environment.properties'
					}
				}
			}
		}
	}
	
	void create(Properties props, File dir) {
		
		props['AssemblyGroup'] = getAssemblyGroup()
		props['AssemblyName'] = getAssemblyName()
		props['AssemblyVersion'] = getAssemblyVersion()
		props['IntershopCDToolsVersion'] = getToolsVersion()
		props['IntershopCDBootstrapVersion'] = getBootstrapVersion()
		
		File deployments = new File(dir, 'deployments')
		createBase(deployments.getAbsolutePath(), props, getProjectName())
		createCIServerExample(dir.getAbsolutePath(), getHostName())
	}
}
