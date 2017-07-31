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
import com.intershop.gradle.cisetup.templates.CorporateDistribution
import com.intershop.gradle.cisetup.templates.IntershopDeployment
import com.intershop.gradle.cisetup.templates.IntershopProject
import com.intershop.gradle.cisetup.templates.OracleCartridge
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class CISetupPlugin implements Plugin<Project> {
	
	// extension name
	public static final String EXTENSION_NAME = 'IntershopCISetup'
	public static final String CI_SETUP_TASK_GROUP = 'Intershop CI Source'
	public static final String OVERALL_TASK = 'intershopCISetupAll'

    private CISetupExtension extension

    /**
     * Apply plugin to project
     *
     * @param project
     */
	void apply(Project project) {

		project.logger.info("Applying ${EXTENSION_NAME} plugin to project: ${project.name}")
		this.extension = project.extensions.create(EXTENSION_NAME, CISetupExtension, project)

		Task all = project.getTasks().create(OVERALL_TASK).configure {
			group = CI_SETUP_TASK_GROUP
			description = "Create all necessary source artefacts"
		}

		all.dependsOn(addCorporateDistributionTask(project, extension))
        all.dependsOn(addOracleComponentSetTask(project, extension))
		all.dependsOn(addProjectTask(project, extension))
        all.dependsOn(addDeploymentTask(project, extension))
	}

    /**
     * Add corporate distribution tasks
     *
     * @param project
     * @param extension
     * @return
     */
	private static Task addCorporateDistributionTask(Project project, CISetupExtension extension) {
		CorporateDistribution task = project.getTasks().create('createCorporateDistribution', CorporateDistribution.class )
        task.group = CI_SETUP_TASK_GROUP
		task.description = "Creates a structure of a corporate distribution package project."

        // output
		task.conventionMapping.outputDir = { project.file(extension.directories.getDevOpsDir()) }

        // repo configuration
		task.conventionMapping.repositoryURL = { extension.repository.getRepoBaseURL() }
		task.conventionMapping.groupReleasesPath = { extension.repository.getGroupReleasePath() }
		task.conventionMapping.repoSnapshotsPath = { extension.repository.getRepoSnapshotsPath() }

        // repo hosts
        task.conventionMapping.repoHosts = { extension.repository.getRepoHosts() }

        // gradle base version
		task.conventionMapping.gradleBaseVersion = { extension.repository.getGradleBaseVersion() }
        // initial version
        task.conventionMapping.distributionVersion = { extension.repository.getDistributionVersion() }

        // only used for task configuration
		task.conventionMapping.distributionURL = { extension.repository.getDistributionURL() }

		// corporate name
		task.conventionMapping.corporateName = { extension.repository.getCorporateName() }
		
		return task
	}

    /**
     * Add template for oracle component set
     *
     * @param project
     * @param extension
     * @return
     */
	private static Task addOracleComponentSetTask(Project project, CISetupExtension extension) {
		OracleCartridge task = project.getTasks().create('createOracleComponentSet', OracleCartridge.class)
        task.group = CI_SETUP_TASK_GROUP
		task.description = "Creates a special component set for publishing Oracle JDBC drivers"

        // output
		task.conventionMapping.outputDir = { project.file("${extension.directories.getProjectsDir()}/oracleDriver") }

        // template configuration
		task.conventionMapping.oracleClientVersion = { extension.versions.getOracleClientVersion() }
		task.conventionMapping.oracleCartridgeVersion = { extension.versions.getOracleCartridgeVersion() }

        // used for build and publishing
		task.conventionMapping.distributionURL = { extension.repository.getDistributionURL() }

        return task
	}

    /**
     * Add project template task
     *
     * @param project
     * @param extension
     * @return
     */
	private static Task addProjectTask(Project project, CISetupExtension extension) {
		IntershopProject task  = project.getTasks().create('createProject', IntershopProject.class )
        task.group = CI_SETUP_TASK_GROUP
		task.description = "Creates a structure of a project configuration."

        // output
		task.conventionMapping.outputDir = { project.file(extension.directories.getProjectsDir()) }

        // template configuration
        task.conventionMapping.projectName = { extension.intershopProject.getProjectName() }

        // used for build and publishing
		task.conventionMapping.distributionURL = { extension.repository.getDistributionURL() }

        return task
	}

    /**
     * Add deployment task for the project
     *
     * @param project
     * @param extension
     * @return
     */
	private static Task addDeploymentTask(Project project, CISetupExtension extension) {
		IntershopDeployment task = project.getTasks().create('createDeploymentConfig', IntershopDeployment.class )
        task.group = CI_SETUP_TASK_GROUP
		task.description = "Creates a structure of a deployment configuration."

        // output
		task.conventionMapping.outputDir = { project.file(extension.directories.getDevOpsDir()) }

        // template configuration
		task.conventionMapping.projectName = { extension.intershopProject.getProjectName() }
		task.conventionMapping.bootstrapVersion = { extension.versions.getIntershopDeploymentBootstrapVersion() }

        // used for build and publishing
		task.conventionMapping.distributionURL = { extension.repository.getDistributionURL() }

        task.conventionMapping.hostName = { extension.ciServer.getHostName() }

		return task
	}
}
