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
 * Configuration extension for versions
 */
@CompileStatic
class RepositoryConfig {

    String corporateName = 'CorporateName'

	String repoBaseURL
	
    String[] repoHosts
	
    String[] getRepoHosts() {
        if (repoHosts) {
            return repoHosts
        } else {
            String host = repoBaseURL.toURL().host
            if (host.empty) { 
                // This is the case for file:/// URIs
                return [] as String[]
            } else  {
                return [host] as String[]
            }
        }
    }
	
	String repoReleasesPath = 'repositories/releases'
	String repoSnapshotsPath = 'repositories/snapshots'

	String groupReleasePath = 'groups/components'

    String[] ivyRepoPaths = []
	String[] mavenRepoPaths = []

    String[] ivySnapshotsRepoPaths = []
    String[] mavenSnapshotsRepoPaths = []
	
	String distributionVersion = '2.0.0'
	String gradleBaseVersion = '2.11'
	
	String distributionURL
	
	String getDistributionURL() {
		if(distributionURL) {
			return distributionURL
		} else {
            String normalizedName = getCorporateName().replaceAll('\\s','').toLowerCase()
			return "${getRepoBaseURL()}/distributions/gradle-dist/${normalizedName}/${getDistributionVersion()}/${normalizedName}-${getDistributionVersion()}.zip"
		}
	}
	
	String[] getIvyRepoPaths() {
		def repos = [] as ArrayList<String>
		
		repos.addAll(ivyRepoPaths)
		repos.add(getGroupReleasePath())
		repos.unique { a, b -> a <=> b } // remove duplicates
		
		return repos.toArray()
	}
	
	String[] getMavenRepoPaths() {
		def repos = [] as ArrayList<String>
		
		repos.addAll(mavenRepoPaths)
        repos.unique { a, b -> a <=> b } // remove duplicates
		
		return repos.toArray()
	}

    String[] getIvySnapshotsRepoPaths() {
        def repos = [] as ArrayList<String>

        repos.addAll(ivySnapshotsRepoPaths)
        repos.add(getRepoSnapshotsPath())
        repos.unique { a, b -> a <=> b } // remove duplicates

        return repos.toArray()
    }

    String[] getMavenSnapshotsRepoPaths() {
        def repos = [] as ArrayList<String>

        repos.addAll(mavenSnapshotsRepoPaths)
        repos.add(getRepoSnapshotsPath())
        repos.unique { a, b -> a <=> b } // remove duplicates

        return repos.toArray()
    }
}
