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
    classpath 'com.intershop.gradle.repoconfig:repoconfig-gradle-plugin:3.3.0'
  }
}

// add configuration necessary for pre-project objects (like Gradle settings or init scripts)
apply plugin: com.intershop.gradle.repoconfig.RepoConfigPlugin

repositoryConfiguration {

    ${singlerepo ? '' : '//'} releaseRepo = '$RepoBaseURL/$RepoReleaseGroupID'
    ${singlerepo ? '' : '//'} snapshotRepo = '$RepoBaseURL/$RepoSnapshotsID'

    ${singlerepo ? '//' : ''} ivyReleaseRepo = '$RepoBaseURL/$IvyRepoReleases'
    ${singlerepo ? '//' : ''} mvnReleaseRepo = '$RepoBaseURL/$MvnRepoReleases'
    ${singlerepo ? '//' : ''} ivySnapshotRepo = '$RepoBaseURL/$IvyRepoSnapshots'
    ${singlerepo ? '//' : ''} mvnSnapshotRepo = '$RepoBaseURL/$MvnRepoSnapshots'

    repoHostList = $RepositoryHosts
    corporateName = '$CorporateName'
}