/**
 * Configuration
 **/
// Base URL of the repository without a specific path
final REPO_URL = '$RepoBaseURL'
// Specific path for all repositories
// Repository for distributions
final REPO_DISTRIBUTION_PATH = '$RepoDistributionsID'

/**
 * Script
 **/
apply plugin: 'base'
apply plugin: 'ivy-publish'

String repoUser = project.hasProperty('repoUserName') ? project.getProperty('repoUserName') : System.getProperty('REPO_USER_NAME') ?: System.getenv('REPO_USER_NAME')
String repoUserPasswd = project.hasProperty('repoUserPasswd') ? project.getProperty('repoUserPasswd') : System.getProperty('REPO_USER_PASSWD') ?: System.getenv('REPO_USER_PASSWD')

task downloadGradle(type: DownloadGradle) {
	gradleVersion '$GradleVersion'
	destinationDir file('gradle-downloads')
}

task customGradleDistribution(type: Zip, dependsOn: downloadGradle) {
	from zipTree(downloadGradle.destinationFile)
	archiveName "corporate_gradle_\${downloadGradle.gradleVersion}-\$version-bin.zip"
	into "\${downloadGradle.distributionNameBase}/init.d", {
		from "src/init.d"
	}
}

publishing {
	publications {
		ivy(IvyPublication) {
			artifact(customGradleDistribution) {
				organisation 'gradle-dist'
				module "corporate_gradle_\${downloadGradle.gradleVersion}"
				revision "\${version}"
				type "bin"
				conf "runtime"
			}
			configurations {
				runtime {}
			}
		}
	}
	repositories {
		ivy {
			name = 'Ivy Distribution Repository'
			if(repoUser && repoUserPasswd) {
                credentials {
                    username repoUser
                    password repoUserPasswd
                }
            } else {
                println 'No username and password for repository specified. Please check system properties, if errors occur.'
            }
			url  = "\$REPO_URL/\$REPO_DISTRIBUTION_PATH/"
			layout('pattern') {
				ivy '[organisation]/[module]/[revision]/ivy.xml'
				artifact '[organisation]/[module]/[revision]/[artifact]-[revision](-[type]).[ext]'
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
