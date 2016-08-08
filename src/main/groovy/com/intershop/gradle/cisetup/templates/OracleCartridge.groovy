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
 * Template task of the oracle component set
 */
class OracleCartridge extends AbstractTemplate {

	@Input
	String oracleCartridgeVersion
	
	@Input
	String oracleClientVersion

	@Input
	String repositoryURL

	@Input
	String repoReleasesPath

	static void createBase(String path, Properties props) {
		ProjectTemplate.fromRoot(path) {
			'3rd_oracle' {
				'gradle' {
					'wrapper' {
						'gradle-wrapper.jar' resourcePath: '/resources/gradle-wrapper.jar'
						'gradle-wrapper.properties' template: '/resources/corporate/gradle-wrapper.properties.tpl',
								templateProperties: props
					}
				}
				'gradlew' resourcePath: '/resources/gradlew', permissions: 'rwxr-xr-x'
				'publish.sh' resourcePath: '/resources/oracle/publish.sh', permissions: 'rwxr-xr-x'

				'gradlew.bat' resourcePath: '/resources/gradlew.bat'
				'publish.bat' resourcePath: '/resources/oracle/publish.bat'

				'init.gradle' resourcePath: '/resources/oracle/init.gradle'
				'settings.gradle' resourcePath: '/resources/oracle/settings.gradle'

				'build.gradle' 		template: '/templates/oracle/build.gradle.tpl',
									templateProperties: props

				'build' {
					'oracleLibs' {
						'jars' {}
					}
				}
			}
		}
	}

	void create(Properties props, File dir) {
		props['OracleCartridgeVersion'] = getOracleCartridgeVersion()
		props['OracleClientVersion'] = getOracleClientVersion()
		props['RepoBaseURL'] = getRepositoryURL()
		props['RepoReleasesID'] = getRepoReleasesPath()

		createBase(dir.getAbsolutePath(), props)
	}
}
