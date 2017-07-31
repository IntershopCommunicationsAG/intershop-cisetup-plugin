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

import org.gradle.api.tasks.Input
import com.intershop.gradle.cisetup.util.ProjectTemplate

/**
 * Template task of corporate distribution
 */
class CorporateDistribution extends AbstractTemplate {

	@Input
	String corporateName

	@Input
	String repositoryURL

    @Input
	String groupReleasesPath

	@Input
	String repoSnapshotsPath

	@Input
	String distributionName

	@Input
	String[] repoHosts

	@Input
    String gradleBaseVersion = '2.11'

    @Input
    String distributionVersion = '2.0.0'
	
	static void createBase(String path, Properties props) {
		ProjectTemplate.fromRoot(path) {
			'corporate-distribution' {
				'src' {
					'init.d' {
						'intershop-init.gradle' template: '/templates/corporate-distribution/src/intershop-init.gradle.tpl',
												templateProperties: props
					}
				}
				'gradle' {
					'wrapper' {
						'gradle-wrapper.jar' resourcePath: '/resources/gradle-wrapper.jar'
						'gradle-wrapper.properties' template: '/resources/gradle/gradle-wrapper.properties.tpl', 
													templateProperties: props 
					}
				}
				'gradlew' resourcePath: '/resources/gradlew', permissions: 'rwxr-xr-x'
				'gradlew.bat' resourcePath: '/resources/gradlew.bat'
				
				'build.gradle' 		template: '/templates/corporate-distribution/build.gradle.tpl',
									templateProperties: props
			}
		}
	}
	
	void create(Properties props, File dir) {
		
		props['RepoBaseURL'] = getRepositoryURL()
		props['RepoReleaseGroupID'] = getGroupReleasesPath()
		props['RepoSnapshotsID'] = getRepoSnapshotsPath()
		props['CustomDistributionVersion'] = getDistributionVersion()
		props['GradleVersion'] = getGradleBaseVersion()
		props['CorporateName'] = getCorporateName()
		props['CorporateNameNormalized'] = getDistributionName()
		props['RepositoryHosts'] = "[${ getRepoHosts().collect{ "'$it'" }.join(",")}]"
		File gradle = new File(dir, 'gradle')
		println gradle
		CorporateDistribution.createBase(gradle.getAbsolutePath(), props)
	}
}
