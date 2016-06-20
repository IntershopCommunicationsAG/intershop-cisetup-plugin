/** 
 * Generated 
 * Version   : ${TemplateVersionString}
 * TimeStamp : ${TemplateTimeString}
 *
 * Generation: ${TemplateGenerationTime}
 **/

//See Gradle Documentation 'Initialization Scripts'
//definition of external dependencies
initscript {

  // only used for secured repositories
  String repoUser = System.getProperty('repoUser') ?: System.getenv('REPO_USER')
  String repoUserPasswd = System.getProperty('repoUserPasswd') ?: System.getenv('REPO_USER_PASSWD')

  repositories {
    // Default repository of ivy releases
    ivy {
      name = 'ishreleases'
      url "$RepoBaseURL/$RepoReleaseGroupID"
      layout('pattern') {
        ivy '[organisation]/[module]/[revision]/[type]s/ivy-[revision].xml'
        artifact '[organisation]/[module]/[revision]/[ext]s/[artifact]-[type]-[revision].[ext]'
      }
      
      // only used for secured repositories
      if(repoUser && repoUserPasswd) {
        credentials {
          username repoUser
          password repoUserPasswd
        }
      }
    }
    //TODO: Check if this is still necessary as it's the same as above
    // Default repository for maven releases
    maven {
      url "$RepoBaseURL/$RepoReleaseGroupID"
      
      // only used for secured repositories
      if(repoUser && repoUserPasswd) {
        credentials {
          username repoUser
          password repoUserPasswd
        }
      }
    }
    jcenter()
  }
  dependencies {
    //Repository configuration - provided by Intershop
    classpath 'com.intershop.gradle.repoconfig:repoconfig-gradle-plugin:1.1.1'
    //versioning plugin - provided by Intershop
    classpath 'com.intershop.build.gradle:versioning:$IntershopCDToolsVersion'
  }
}

// add configuration necessary for pre-project objects (like Gradle settings or init scripts)
apply plugin: com.intershop.gradle.repoconfig.RepoConfigPlugin

repositoryConfiguration {
    releaseRepo = '$RepoBaseURL/$RepoReleaseGroupID'
    snapshotRepo = '$RepoBaseURL/$RepoSnapshotsID'
    repoHostList = $RepositoryHosts
    corporateName = '$CorporateName'
}

allprojects {
  // Versioning plugin
  project.plugins.apply(com.intershop.build.gradle.plugins.VersioningPlugin)
}