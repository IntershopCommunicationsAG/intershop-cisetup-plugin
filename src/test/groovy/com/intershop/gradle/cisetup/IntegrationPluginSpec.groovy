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

import com.intershop.gradle.test.AbstractIntegrationSpec
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome

class IntegrationPluginSpec extends AbstractIntegrationSpec {
    public static final String buildFileContent = """
            plugins {
                id 'com.intershop.gradle.cisetup'
            }

            IntershopCISetup {
                /**
                 *  Configures directories that sources are generated into.
                 **/
                directories {

                    /**
                     * Base directory for all generated sources.
                     **/
                        //TODO: Provide an absolute path, e.g. '/home/developer/intershop-ci/source' on Linux.'C:/developer/intershop-ci' on Windows
                    setupDirectory = 'intershop-ci-setup'

                    /**
                     *  ... contains
                     *        - corporate plugin
                     *        - deployment example project
                     **/
                    devOpsDir = "\${setupDirectory}/devops"

                    /**
                     *  ... contains all projects
                     *        - oracle driver project
                     *        - empty project directories
                     **/
                    projectsDir = "\${setupDirectory}/projects"
                }

                /**
                 *  Repository Settings
                 *  For build and deployment a remote repository is necessary.
                 *  This configuration is used by a special corporate plugin and distribution.
                 *  The configuration parameters are also used in the configuration of the
                 *  wrapper in all projects.
                 **/
                repository {
                    /**
                     * Technical enterprise or corporate name.
                     * This is used for group/organization of generated artifacts in
                     * the artifact repository.
                     */
                    //TODO: Provide a reverse domain name, e.g. 'com.corporate'
                    corporateName = 'test-corporatename'

                    /**
                     * Base URL for all repositories on the repository server.
                     */
                    //TODO: Provide URL (e.g. http://nexus:8081/nexus/content/repositories)
                    repoBaseURL = 'http://nexus:8081/nexustest/repositories'

                    /**
                     *  A list of alternative host names. Can be used when having multiple
                     *  repository servers proxying each other.
                     */
                    repoHosts = ['nexus', 'nexus.corporate.com']

                    /**
                     * Repository path of releases.
                     */
                    repoReleasesPath = 'repositories/releases'

                    /**
                     * Repository path of snapshots.
                     */
                    repoSnapshotsPath = 'repositories/snapshots'

                    /**
                     * Group path of all repositories with component releases
                     */
                    groupReleasePath = 'groups/components'

                    /**
                     * Gradle version to base corporate distribution on.
                     * Must be compatible with Gradle tools version defined in versions block.
                     */
                    gradleBaseVersion = '2.11'

                    /**
                     * Initial version of the corporate distribution.
                     */
                    distributionVersion = '2.0.0'

                    /**
                     * If the distribution stored on an alternative path (separate web server)
                     * It is necessary to specify the whole URL. Otherwise the URL will be
                     * generated from the previous configuration.
                     *
                     * Defaults to
                     * \${repoBaseURL}/distributions/gradle-dist/\${corporateName without spaces}/\${distributionVersion}/\${corporateName without spaces}-\${distributionVersion}.zip
                     **/ 
                    //distributionURL=''
                }

                /**
                 *  Version settings
                 *  For build and deployment projects it is necessary to configure the information about the used Intershop versions.
                 *
                 *  It is also possible to use Ivy version ranges.
                 **/
                versions {
                    /**
                     * Version of the deployment bootstrap plugin
                     * Check always for updates.
                     */
                    intershopDeploymentBootstrapVersion = '2.11.6'

                    /**
                     * Oracle client version.
                     */
                    //TODO: Specify the version of used Oracle driver
                    oracleClientVersion = '12.1.0.2'

                    oracleCartridgeVersion = '12.1.0.2.0.0'
                }

                intershopProject {
                    /**
                     * Technical name of the project. This is also used for the name of the multi project,
                     * that contains all components (cartridges) and the assembly build.
                     **/
                    //TODO: Provide a name using only letters, numbers and underscores (no spaces or other special characters), e.g. 'corporateshop'
                    projectName = 'test-project'
                }

                /**
                 *  CI server configurationen settings
                 **/
                ciServer {
                    // Hostname of the ci server or ci server agent which runs the assembly integration test
                    hostName = 'ciserver'
                }
            }
        """.stripIndent()

    def "Show tasks for plugin"() {
        given:
        File settingsFile = file('settings.gradle')
        settingsFile << """
            rootProject.name= 'cisetup'
        """.stripIndent()

        buildFile << buildFileContent

        when:
        def result = getPreparedGradleRunner()
                .withArguments('tasks', '--stacktrace', '-i')
                .build()

        then:
        result.output.contains('createCorporateDistribution - Creates a structure of a corporate distribution package project.')
        result.output.contains('createOracleComponentSet - Creates a special component set for publishing Oracle JDBC drivers')
        result.output.contains('createDeploymentConfig - Creates a structure of a deployment configuration.')
        result.output.contains('createProject - Creates a structure of a project configuration.')
    }

    def "Run over all task"() {
        given:
        String[] taskList = [':createCorporateDistribution',':createOracleComponentSet',':createDeploymentConfig', ':createProject']

        File settingsFile = file('settings.gradle')
        settingsFile << """
            rootProject.name= 'cisetup'
        """.stripIndent()

        buildFile << buildFileContent

        when:
        def result = getPreparedGradleRunner()
                .withArguments('intershopCISetupAll', '--stacktrace', '-i')
                .build()

        List taskListExecuted = []
        result.getTasks().each {it ->
            taskListExecuted.add(it.path)
        }

        then:
        taskListExecuted.containsAll(taskList)

        new File(testProjectDir, 'intershop-ci-setup/devops/ci_server/host_configs/ciserver/environment.properties').exists()
        new File(testProjectDir, 'intershop-ci-setup/devops/ci_server/host_configs/ciserver/environment.properties').text.contains('environment = development')
        new File(testProjectDir, 'intershop-ci-setup/devops/deployments/test-project/gradle/wrapper/gradle-wrapper.properties').exists()
        new File(testProjectDir, 'intershop-ci-setup/devops/deployments/test-project/gradle/wrapper/gradle-wrapper.properties').text.contains('http://nexus:8081/nexustest/repositories/distributions/gradle-dist/test-corporatename/2.0.0/test-corporatename-2.0.0.zip')
        new File(testProjectDir, 'intershop-ci-setup/devops/deployments/test-project/settings.gradle').exists()
        new File(testProjectDir, 'intershop-ci-setup/devops/deployments/test-project/settings.gradle').text.contains('com.intershop.build.gradle:deployment-launcher:2.11.6')

        new File(testProjectDir, 'intershop-ci-setup/devops/gradle/corporate-distribution/src/init.d/intershop-init.gradle').exists()
        def contentIntershopInitGradle = new File(testProjectDir, 'intershop-ci-setup/devops/gradle/corporate-distribution/src/init.d/intershop-init.gradle').text
        contentIntershopInitGradle.contains('http://nexus:8081/nexustest/repositories/groups/components')
        contentIntershopInitGradle.contains('releaseRepo = \'http://nexus:8081/nexustest/repositories/groups/components\'')
        contentIntershopInitGradle.contains('snapshotRepo = \'http://nexus:8081/nexustest/repositories/repositories/snapshots\'')
        contentIntershopInitGradle.contains('repoHostList = [\'nexus\',\'nexus.corporate.com\']')
        contentIntershopInitGradle.contains('corporateName = \'test-corporatename\'')

        new File(testProjectDir, 'intershop-ci-setup/projects/oracleDriver/3rd_oracle/gradle').exists()
        new File(testProjectDir, 'intershop-ci-setup/projects/oracleDriver/3rd_oracle/build/oracleLibs/jars').exists()
        new File(testProjectDir, 'intershop-ci-setup/projects/test-project/gradle').exists()
    }

    def "Run deployment configuration task"() {
        given:
        String[] taskList = [':createDeploymentConfig']

        File settingsFile = file('settings.gradle')
        settingsFile << """
            rootProject.name= 'cisetup'
        """.stripIndent()

        buildFile << buildFileContent

        when:
        def result = getPreparedGradleRunner()
                .withArguments('createDeploymentConfig', '--stacktrace', '-i')
                .build()

        List taskListExecuted = []
        result.getTasks().each {it ->
            taskListExecuted.add(it.path)
        }

        then:
        taskListExecuted.containsAll(taskList)

        new File(testProjectDir, 'intershop-ci-setup/devops/ci_server/host_configs/ciserver/environment.properties').exists()
        new File(testProjectDir, 'intershop-ci-setup/devops/ci_server/host_configs/ciserver/environment.properties').text.contains('environment = development')
        new File(testProjectDir, 'intershop-ci-setup/devops/deployments/test-project/gradle/wrapper/gradle-wrapper.properties').exists()
        new File(testProjectDir, 'intershop-ci-setup/devops/deployments/test-project/gradle/wrapper/gradle-wrapper.properties').text.contains('http://nexus:8081/nexustest/repositories/distributions/gradle-dist/test-corporatename/2.0.0/test-corporatename-2.0.0.zip')
        new File(testProjectDir, 'intershop-ci-setup/devops/deployments/test-project/settings.gradle').exists()
        new File(testProjectDir, 'intershop-ci-setup/devops/deployments/test-project/settings.gradle').text.contains('com.intershop.build.gradle:deployment-launcher:2.11.6')

        ! new File(testProjectDir, 'intershop-ci-setup/devops/gradle/corporate-distribution/src/init.d/intershop-init.gradle').exists()

        ! new File(testProjectDir, 'intershop-ci-setup/projects/oracleDriver/p_oracle').exists()
        ! new File(testProjectDir, 'intershop-ci-setup/projects/test-project/gradle').exists()
    }


    def "Run project task"() {
        given:
        String[] taskList = [':createProject']

        File settingsFile = file('settings.gradle')
        settingsFile << """
            rootProject.name= 'cisetup'
        """.stripIndent()

        buildFile << buildFileContent

        when:
        def result = getPreparedGradleRunner()
                .withArguments('createProject', '--stacktrace', '-i')
                .build()

        List taskListExecuted = []
        result.getTasks().each {it ->
            taskListExecuted.add(it.path)
        }

        then:
        taskListExecuted.containsAll(taskList)

        ! new File(testProjectDir, 'intershop-ci-setup/devops/ci_server/host_configs/ciserver/environment.properties').exists()
        ! new File(testProjectDir, 'intershop-ci-setup/devops/deployments/test-project/gradle/wrapper/gradle-wrapper.properties').exists()
        ! new File(testProjectDir, 'intershop-ci-setup/devops/deployments/test-project/settings.gradle').exists()

        ! new File(testProjectDir, 'intershop-ci-setup/devops/gradle/corporate-distribution/src/init.d/intershop-init.gradle').exists()

        ! new File(testProjectDir, 'intershop-ci-setup/projects/oracleDriver/p_oracle').exists()
        new File(testProjectDir, 'intershop-ci-setup/projects/test-project/gradle').exists()
    }


    def "Run corporate distribution task"() {
        given:
        String[] taskList = [':createCorporateDistribution']

        File settingsFile = file('settings.gradle')
        settingsFile.createNewFile()

        settingsFile << """
            rootProject.name= 'cisetup'
        """.stripIndent()

        File repoRoot = new File(testProjectDir, 'testRepo')
        buildFile << buildFileContent.replace('http://nexus:8081/nexustest/repositories', "${repoRoot.toURI().toURL()}/nexustest")

        when:
        def result = getPreparedGradleRunner()
                .withArguments('createCorporateDistribution', '--stacktrace', '-i')
                .build()

        List taskListExecuted = []
        result.getTasks().each {it ->
            taskListExecuted.add(it.path)
        }

        then:
        taskListExecuted.containsAll(taskList)

        ! new File(testProjectDir, 'intershop-ci-setup/devops/ci_server').exists()
        ! new File(testProjectDir, 'intershop-ci-setup/devops/deployments').exists()

        new File(testProjectDir, 'intershop-ci-setup/devops/gradle/corporate-distribution/src/init.d/intershop-init.gradle').exists()
        new File(testProjectDir, 'intershop-ci-setup/devops/gradle/corporate-distribution/src/init.d/intershop-init.gradle').text.contains("${repoRoot.toURI().toURL()}/nexustest/groups/components")
        ! new File(testProjectDir, 'intershop-ci-setup/devops/gradle/corporate-plugins/corporate-configuration').exists()

        ! new File(testProjectDir, 'intershop-ci-setup/projects/oracleDriver').exists()
        ! new File(testProjectDir, 'intershop-ci-setup/projects/test-project').exists()

        when:
        File distriBuildDir = new File(testProjectDir, 'intershop-ci-setup/devops/gradle/corporate-distribution')

        File distriFBuildFile = new File(distriBuildDir, 'build.gradle')
        distriFBuildFile.setText(distriFBuildFile.getText().replace(
                "id 'com.intershop.gradle.artifactorypublish-configuration'",
                "// id 'com.intershop.gradle.artifactorypublish-configuration'").replace(
                "//id 'com.intershop.gradle.simplepublish-configuration'",
                "id 'com.intershop.gradle.simplepublish-configuration'").replace(
                "artifactory {","/** artifactory {").replace(
                "// end of artifactory configuration","**/"))

        def distributionsResult = GradleRunner.create()
                .withProjectDir(distriBuildDir)
                .withArguments('-PrunOnCI=true', "-DSNAPSHOTURL=${repoRoot.toURI().toURL()}/nexustest/snapshots",
                "-PreleaseURL=${repoRoot.toURI().toURL()}/nexustest/releases", 'publish', '--stacktrace', '-i')
                .withGradleVersion('2.11')
                .withPluginClasspath(pluginClasspath)
                .forwardOutput()
                .build()

        File distributionsRoot = new File(repoRoot,'nexustest/snapshots')
        File repoDir = new File(distributionsRoot, 'gradle-dist/test-corporatename/2.0.0-SNAPSHOT/')

        then:
        distributionsResult.output.contains(':publish')
        repoDir.exists()
    }

    def "Run oracle component task"() {
        given:
        String[] taskList = [':createOracleComponentSet']

        File settingsFile = file('settings.gradle')
        settingsFile.createNewFile()

        settingsFile << """
            rootProject.name= 'cisetup'
        """.stripIndent()

        File repoRoot = directory('testrepo')
        File releaseRoot = directory('nexustest/repositories/releases', repoRoot)
        releaseRoot.mkdirs()

        buildFile << buildFileContent.replace('http://nexus:8081/nexustest/repositories', "${repoRoot.toURI().toURL()}/nexustest")

        when:
        def result = getPreparedGradleRunner()
                .withArguments('createOracleComponentSet','createCorporateDistribution', '--stacktrace', '-i')
                .build()

        List taskListExecuted = []
        result.getTasks().each {it ->
            taskListExecuted.add(it.path)
        }

        File publishbat = new File(testProjectDir, 'intershop-ci-setup/projects/oracleDriver/3rd_oracle/publish.bat')

        then:
        taskListExecuted.containsAll(taskList)

        ! new File(testProjectDir, 'intershop-ci-setup/devops/ci_server').exists()
        ! new File(testProjectDir, 'intershop-ci-setup/devops/deployments').exists()

        new File(testProjectDir, 'intershop-ci-setup/devops/gradle/corporate-distribution').exists()

        new File(testProjectDir, 'intershop-ci-setup/projects/oracleDriver/3rd_oracle/gradle').exists()
        new File(testProjectDir, 'intershop-ci-setup/projects/oracleDriver/3rd_oracle/build/oracleLibs/jars').exists()
        ! new File(testProjectDir, 'intershop-ci-setup/projects/test-project').exists()

        publishbat.exists()
        ! publishbat.getText().contains('null')

        when:
        File distriBuildDir = new File(testProjectDir, 'intershop-ci-setup/devops/gradle/corporate-distribution')

        File distriFBuildFile = new File(distriBuildDir, 'build.gradle')
        distriFBuildFile.setText(distriFBuildFile.getText().replace(
                "id 'com.intershop.gradle.artifactorypublish-configuration'",
                "// id 'com.intershop.gradle.artifactorypublish-configuration'").replace(
                "//id 'com.intershop.gradle.simplepublish-configuration'",
                "id 'com.intershop.gradle.simplepublish-configuration'").replace(
                "artifactory {","/** artifactory {").replace(
                "// end of artifactory configuration","**/"))

        GradleRunner.create()
                .withProjectDir(distriBuildDir)
                .withArguments('-PstaticVersion=2.0.0', 'showVersion', '--stacktrace', '-i')
                .withGradleVersion('2.11')
                .withPluginClasspath(pluginClasspath)
                .forwardOutput()
                .build()

        GradleRunner.create()
                .withProjectDir(distriBuildDir)
                .withArguments('-PrunOnCI=true',
                "-PreleaseURL=${repoRoot.toURI().toURL()}/nexustest/distributions", 'publish', '--stacktrace', '-i')
                .withGradleVersion('2.11')
                .withPluginClasspath(pluginClasspath)
                .forwardOutput()
                .build()

        File oracleDir = new File(testProjectDir, 'intershop-ci-setup/projects/oracleDriver/3rd_oracle')

        File ojdbc = new File(testProjectDir, 'intershop-ci-setup/projects/oracleDriver/3rd_oracle/build/oracleLibs/jars/ojdbc7.jar')
        File ons = new File(testProjectDir, 'intershop-ci-setup/projects/oracleDriver/3rd_oracle/build/oracleLibs/jars/ons.jar')
        File ucp = new File(testProjectDir, 'intershop-ci-setup/projects/oracleDriver/3rd_oracle/build/oracleLibs/jars/ucp.jar')

        ojdbc.setText('ojdbc')
        ons.setText('ons')
        ucp.setText('ucp')

        def publishResult = GradleRunner.create()
                .withProjectDir(oracleDir)
                .withArguments('publish', "-PreleaseURL=${repoRoot.toURI().toURL()}/nexustest/releases", '--stacktrace', '-i')
                .withGradleVersion('2.11')
                .withPluginClasspath(pluginClasspath)
                .forwardOutput()
                .build()

        then:
        publishResult.task(':publish').outcome == TaskOutcome.SUCCESS
        (new File(repoRoot, 'nexustest/releases/com/oracle/jdbc/ojdbc7/12.1.0.2.0.0/ojdbc7-12.1.0.2.0.0.jar')).exists()
        (new File(repoRoot, 'nexustest/releases/com.intershop/3rd_oracle/12.1.0.2.0.0/ojdbc7-12.1.0.2.0.0.jar')).exists()

    }

}
