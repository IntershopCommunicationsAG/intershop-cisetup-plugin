// Using this settings.gradle file assumes that
// there is an init script providing repositories

buildscript {
	gradle.injectRepositories(repositories, configurations)
	dependencies {
		classpath 'com.intershop.build.gradle:deployment-launcher:$IntershopCDBootstrapVersion'
	}
}

// tell Gradle to run the deployment and configure parameters
apply plugin: com.intershop.deploy.bootstrap.DeploymentBootstrapPlugin

deploymentBootstrap {
	gradle.injectRepositories(repositoryHandler, configurationContainer)
	
	assembly ("com.intershop.responsive:inspired:1.0.0") {
		
		/*
		 * Define the type of your deployment. According to hostType and environment the contained cartridges
		 * and local component vary.
		 * The host types are defined in your assembly (e.g. 'webserver', 'appserver', 'share', 'all')
		 * The environments are defined in your assembly (e.g. 'development', 'test', 'production')
		 *
		 * hostType = 'webserver' | 'appserver' | 'share' | 'all' | 'javadoc'
		 * environment = 'development' | 'test' | 'production'
		 *
		 * hostType {
		 *     hostType 'all'
		 *     environment 'production'
		 * }
		 */
		hostType {
			hostType 'all'
			environment 'production'
		}
	}
	
	// Configure the deployment
	config {
		/*
		 * Adjust the default settings, if necessary. The default configuration is recommended.
		 * IS_SHARE may not a subfolder of IS_HOME
		 * The settingsDir may not in IS_HOME
		 */
		target {
			/*
			 * Configure the platform we are on.
			 *
			 * platform = 'linux.rhel.x86_64' | 'linux.sles.x86_64' | 'win.x86_64'
			 */
			platform = 'linux.sles.x86_64'
			
			// Id of the instance has to be a number for now
			instanceId = '2'
			
			// IS_SHARE should be located in the 'share' subdirectory of this script's directory
			shareDirectory = new File('/opt/intershop/eserver1/share')
			
			
			// IS_HOME should be located in the 'local' subdirectory of this script's directory
			localDirectory = new File('/opt/intershop/eserver1/local')
		}
		
		assemblyDeployment {
			// Name of user and group that should own the deployed files
			// (needed on windows and linux)
			// The deployment can only be run by the specified user
			user = 'developer'
			userGroup = 'developer'
			
			/*
			 * Configure undeployment.
			 * purgeUnknownFiles = true | false
			 *
			 * true: Clean all
			 * false: Keep files produced outside the deployment (e.g. log files) in file system.
			 */
			purgeUnknownFiles = true
			
			modificationPriorities = ['default', 'intershop', 'development']
		}
		
		/*
		 * Declare some appserver instances.
		 *
		 * Add a new instance by adding a new block:
		 * appserver[n] {
		 *     tomcatShutdownPort = <PORT1>
		 *     tomcatHttpPort = <PORT2>
		 *     tomcatHttpsPort = <PORT3>
		 *     appserverPort = <PORT4>
		 * }
		 * Please use a sequence starting with zero as suffix:
		 * appserver0, appserver1, appserver2, ...
		 */
		appserver {
			nodemanagerJmxPort = 10105
			hostname = 'localhost'
			instances {
				appserver0 {
					tomcatShutdownPort = 10101
					tomcatHttpPort = 10102
					tomcatHttpsPort = 10103
					appserverPort = 10104
				}
			}
		}
		
		// Configure the location of your license.
		// The license file must be available
		license {
			licenseFile = new File('/opt/intershop/license/license.xml')
		}
		
		multicast {
			all {
				networkInterface = '127.0.0.1'
			}
			
			appserver {
				address = '239.192.10.10'
				port = 50001
			}
			
			tcm {
				address = '239.192.10.11'
				port = 50002
			}
			
			cache {
				address = '239.192.10.12'
				port = 50003
			}
			
			orm {
				address = '239.192.10.13'
				port = 50004
			}
		}
		
		webadapter {
			sharedMemoryKey = '0x2001'
			port = 9080
			securePort = 9443
			hostname = 'localhost'
			configurationServices = [ 'localhost:10104' ]
		}
		
		// Database connection parameters
		database {
			host = 'localhost'
			port = 1521
			sid = 'XE'
			tnsAlias = 'isdb1.world'
			user = 'intershop'
			password = 'intershop'
			oracleClientDir = new File('/u01/app/oracle/product/11.2.0/xe')
		}
		
		deployment {
		}
	}
}
