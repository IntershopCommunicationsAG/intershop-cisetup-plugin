/**
 * Provide Oracle libraries to your own local or remote 
 * Maven or Ivy repository
 *  - from your own local directory
 *  - from the official Oracle repository (OTN account is necessary)
 *    The credentials are passed to the script via system environment settings:
 *      OTN account name:     OTN_USER
 *      Password:             OTN_PASSWORD
 *    Activate your OTN account for Oracle Maven Repository:
 *      https://www.oracle.com/webapps/maven/register/license.html
 *  - The remote repository with all necessary parameters must be specified by 
 *    system environment settings:
 *       Release repository: RELEASEREPO
 *       Repository user with permissions to publish an artifact:
 *                           REPO_USER_NAME
 *       Repository user password:
 *                           REPO_USER_PASSWD
 *    
 **/

// plugin configuration
plugins {
	id 'ivy-publish'
    id 'maven-publish'

	id 'com.intershop.gradle.buildinfo' version '1.1.0'
    id 'de.undercouch.download' version '3.0.0'
}

/*
 * basic script configuration
 */
// Oracle artifact url
def oracleRepo = 'https://www.oracle.com/content/secure/maven/content'
// JDBC version
def oracleVersion = '${OracleClientVersion}'
// Version for Ivy publishing
def ivyPublishVersion = '${OracleCartridgeVersion}'

/*
 * script provided by Intershop
 */
group = 'com.intershop'
version = ivyPublishVersion

def authorization = "\${System.getenv('OTN_USER')}:\${System.getenv('OTN_PASSWD')}".getBytes().encodeBase64().toString()
def libsDestination = '\${project.buildDir}/oracleLibs/jars'

String repoUser = project.hasProperty('repoUserName') ? project.getProperty('repoUserName') : System.getProperty('REPO_USER_NAME') ?: System.getenv('REPO_USER_NAME')
String repoUserPasswd = project.hasProperty('repoUserPasswd') ? project.getProperty('repoUserPasswd') : System.getProperty('REPO_USER_PASSWD') ?: System.getenv('REPO_USER_PASSWD')
String releaseRepo = project.hasProperty('releaseURL') ? project.getProperty('releaseURL') : System.getProperty('RELEASEURL') ?: System.getenv('RELEASEURL')

if (!releaseRepo) releaseRepo = '${RepoBaseURL}/${RepoReleasesID}'

afterEvaluate {
    project.tasks.matching { it.name.startsWith('generate') }.all { it.dependsOn project.tasks.ojdbc7Download, project.tasks.onsDownload, project.tasks.ucpDownload }
}

task ojdbc7Download(type: de.undercouch.gradle.tasks.download.Download) {
    src "\${oracleRepo}/com/oracle/jdbc/ojdbc7/\${oracleVersion}/ojdbc7-\${oracleVersion}.jar"
    dest file("\${project.buildDir}/oracleLibs/jars/ojdbc7.jar")
    header "Authorization", "Basic " + authorization
}
ojdbc7Download.onlyIf {
    ! file("\${project.buildDir}/oracleLibs/jars/ojdbc7.jar").exists()
}

task onsDownload(type: de.undercouch.gradle.tasks.download.Download) {
    src "\${oracleRepo}/com/oracle/jdbc/ons/\${oracleVersion}/ons-\${oracleVersion}.jar"
    dest file("\${project.buildDir}/oracleLibs/jars/ons.jar")
    header "Authorization", "Basic " + authorization
}
onsDownload.onlyIf {
    ! file("\${project.buildDir}/oracleLibs/jars/ons.jar").exists()
}

task ucpDownload(type: de.undercouch.gradle.tasks.download.Download) {
    src "\${oracleRepo}/com/oracle/jdbc/ucp/\${oracleVersion}/ucp-\${oracleVersion}.jar"
    dest file("\${project.buildDir}/oracleLibs/jars/ucp.jar")
    header "Authorization", "Basic " + authorization
}
ucpDownload.onlyIf {
    ! file("\${project.buildDir}/oracleLibs/jars/ucp.jar").exists()
}

/*
 * Prevent the expansion of the version number with the 'LOCAL' extension
 * This project property is evaluated by the repoconfig-gradle-plugin
 * See https://github.com/IntershopCommunicationsAG/repoconfig-gradle-plugin
 */
 project.ext.useSCMVersionConfig  = true

publishing {
    publications {
        // Ivy publishing for legacy builds
        ivy(IvyPublication) {
            configurations {
                'default' { }
                runtime { extend 'default' }
            }
        	artifact(ojdbc7Download.dest) {
        		name 'ojdbc7'
        		type 'jar'
        		extension 'jar'        	
            }
            artifact(onsDownload.dest) {
                name 'ons'
                type 'jar'
                extension 'jar'         
            }
            artifact(ucpDownload.dest) {
                name 'ucp'
                type 'jar'
                extension 'jar'         
            }
            
        }

        // Maven publishing for simplified Oracle JDBC client
        // this is prepared for future releases.
        mavenOJDBC(MavenPublication) {
            artifact(ojdbc7Download.dest) {
                groupId 'com.oracle.jdbc'
                artifactId 'ojdbc7'
            }            
        }
        mavenONS(MavenPublication) {
            artifact(onsDownload.dest) {
                groupId 'com.oracle.jdbc'
                artifactId 'ons'     
            }            
        }
        mavenUCP(MavenPublication) {
            artifact(ucpDownload.dest) {
                groupId 'com.oracle.jdbc'
                artifactId 'ucp' 

            }            
        }      
    }

    if(releaseRepo) {
        repositories {
            ivy {
                url releaseRepo
                
                if (repoUserName && repoUserPasswd) {
                    credentials {
                        username repoUserName
                        password repoUserPasswd
                    }
                }
            }
        }
    }
}