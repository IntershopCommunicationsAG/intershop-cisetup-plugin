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
import org.gradle.api.tasks.OutputDirectory

class IntershopProject extends AbstractTemplate {
	
	@Input
	String projectName = 'corporateshop'

	@OutputDirectory
	File outputDir

	void createDeveloperBase(String path, Properties props) {
		ProjectTemplate.fromRoot(path) {
			'gradle_environment.bat' template: '/templates/developer_home/gradle_environment.bat.tpl',
									 templateProperties: props
			'gradle_environment.sh'  template: '/templates/developer_home/gradle_environment.sh.tpl',
									 templateProperties: props
		}
	}

    void createGradleBase(String path, Properties props) {
        ProjectTemplate.fromRoot(path) {
			'gradle' {
				'wrapper' {
					'gradle-wrapper.jar' resourcePath: '/resources/gradle-wrapper.jar'
					'gradle-wrapper.properties' template: '/resources/corporate/gradle-wrapper.properties.tpl',
							templateProperties: props
				}
			}
        }
    }
	
	@Override
	public void create(Properties props, File dir) {
		File gradleOutputDir = getOutputDir()
		if(! gradleOutputDir.exists()) {
			gradleOutputDir.mkdirs()
		}
		
		File projectDir = new File(gradleOutputDir, getProjectName())
		if(! projectDir.exists()) {
			projectDir.mkdirs()
		}

		props['ProjectName'] = getProjectName()

		createDeveloperBase(projectDir.getAbsolutePath(), props)
        createGradleBase(projectDir.getAbsolutePath(), props)
	}
	

}
