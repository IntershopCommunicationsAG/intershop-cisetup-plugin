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

import com.intershop.gradle.cisetup.extension.RepositoryConfig
import spock.lang.Specification;

class RepositoryConfigSpec extends Specification
{
    def "repoHosts defaults to host of repoBaseURL"() {
        RepositoryConfig repositoryConfig = new RepositoryConfig()
        
        when:
        repositoryConfig.repoBaseURL = 'http://nexushost:8081/nexus'
        
        then:
        repositoryConfig.repoHosts == ['nexushost']        
        
        when:
        repositoryConfig.repoBaseURL = 'file:///C|/repositories'
        
        then:
        println repositoryConfig.repoHosts
        repositoryConfig.repoHosts == []
    }
}
