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
  repositories {
    jcenter()
  }
  dependencies {
    //Repository configuration - provided by Intershop
    classpath 'com.intershop.gradle.repoconfig:repoconfig-gradle-plugin:1.1.1'
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