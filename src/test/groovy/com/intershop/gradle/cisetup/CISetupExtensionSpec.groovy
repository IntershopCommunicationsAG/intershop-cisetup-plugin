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

package com.intershop.gradle.cisetup

import com.intershop.gradle.cisetup.extension.CISetupExtension
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class CISetupExtensionSpec extends Specification {

    def "Corporate name is used as default for groups"() {
        Project project = ProjectBuilder.builder().build()
        
        project.apply plugin: CISetupPlugin
        
        CISetupExtension extension = project.IntershopCISetup
        
        when:        
        extension.repository.corporateName = 'xyz.acme'
        
        then:
        extension.repository.getCorporateName() == 'xyz.acme'
    }
    
    def "Project name is used as default for artifact names"() {
        Project project = ProjectBuilder.builder().build()
        
        project.apply plugin: CISetupPlugin
        
        CISetupExtension extension = project.IntershopCISetup
        
        when:
        extension.intershopProject.projectName = 'acme_shop'
        
        then:
        extension.intershopProject.projectName == 'acme_shop'
    }
}
