plugins {
    // publishing / upload configuration
    id 'maven-publish'

    // ide plugin
    id 'idea'

    id 'com.intershop.gradle.scmversion' version '2.3.5'

    /**
     * Intershop release configuration
     * requires
     *  - Gradle Artifactory plugin
     *  - Atlassian Jira
     *  - Buildinfo plugin
     *  and additional environment variables.
     * Furthermore an applied SCMVersion plugin is mandatory.
     * See https://github.com/IntershopCommunicationsAG/gradle-release-plugins.
     **/
    // id 'com.intershop.gradle.artifactorypublish-configuration' version '3.5.2'

    /**
     * Simple Release configuration
     * requires only a Maven compatible repository and
     * additional environment variables.
     * See also https://github.com/IntershopCommunicationsAG/gradle-release-plugins.
     **/
     id 'com.intershop.gradle.simplepublish-configuration' version '3.5.2'
}

scm {
	version {
		type = 'threeDigits'
		increment = 'MAJOR'
		patternDigits = 1

		initialVersion = '$CustomDistributionVersion'
	}
}

group = 'gradle-dist'
version = scm.version.version


/**
 * Only necessary for artifactory publish configuration
 **/
// artifactory {
//    publish {
//        // for mvn publications
//        repository {
//            maven = true
//        }
//        // list of publication names
//        defaults {
//            publications('mvn')
//        }
//    }
// }


task downloadGradle(type: DownloadGradle) {
	gradleVersion '$GradleVersion'
	destinationDir file('gradle-downloads')
}

task customGradleDistribution(type: Zip, dependsOn: downloadGradle) {
	from zipTree(downloadGradle.destinationFile)
	archiveName "${CorporateNameNormalized}.zip"
	destinationDir new File(project.buildDir, 'zipfile')
	into "\${downloadGradle.distributionNameBase}/init.d", {
		from "src/init.d"
	}
}

publishing {
    publications {
        mvn(MavenPublication) {
            artifactId '$CorporateNameNormalized'
            groupId 'gradle-dist'

            artifact(customGradleDistribution)

            configurations {
                runtime {}
            }
        }
    }
}

class DownloadGradle extends DefaultTask {
	@Input String gradleVersion
	@Input File destinationDir
	@Input String gradleDownloadBase = "http://services.gradle.org/distributions"

	@TaskAction doDownloadGradle() {
		destinationFile.withOutputStream { it << new URL(downloadUrl).newInputStream() }
	}

	String getDownloadUrl() {
		"\$gradleDownloadBase/\$downloadFileName"
	}

	String getDistributionNameBase() {
		"gradle-\$gradleVersion"
	}

	String getDownloadFileName() {
		"\$distributionNameBase-bin.zip"
	}

	@OutputFile File getDestinationFile() {
		new File(destinationDir, downloadFileName)
	}
}
