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
import org.gradle.api.Project
import org.gradle.util.ConfigureUtil

/**
 * Plugin extension
 */
@CompileStatic
class CISetupExtension {
	
    private final Project project
    
    public CISetupExtension(Project project) {
        this.project = project
    }

    /**
     * Repository configuration
     */
	RepositoryConfig repository = new RepositoryConfig()
	
	void repository(Closure configure) {
		ConfigureUtil.configure(configure, repository)
	}

    /**
     * Output directories configuration
     */
	OutputDirectories directories = new OutputDirectories()
	
	void directories(Closure configure) {
		ConfigureUtil.configure(configure, directories)
	}

    /**
     * Version configuration
     */
	VersionConfig versions = new VersionConfig(project)
	
	void versions(Closure configure) {
		ConfigureUtil.configure(configure, versions)
	}

    /**
     * Intershop project
     */
	IntershopProject intershopProject = new IntershopProject(project)

	void intershopProject(Closure configure) {
		ConfigureUtil.configure(configure, intershopProject)
	}

    /**
     * Ci server project configuration
     */
	CIServerConfig ciServer = new CIServerConfig()
	
	void ciServer(Closure configure) {
		ConfigureUtil.configure(configure, ciServer)
	}
}
