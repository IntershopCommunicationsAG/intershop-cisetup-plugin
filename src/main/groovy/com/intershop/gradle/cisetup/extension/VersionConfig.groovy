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
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project

/**
 * Configuration extension for versions
 */
@CompileStatic
class VersionConfig {

    private final Project project
    
    public VersionConfig(Project project)
    {
        this.project = project
    }

    String intershopDeploymentBootstrapVersion

    void setIntershopDeploymentBootstrapVersion(String intershopDeploymentBootstrapVersion) {
        if (intershopDeploymentBootstrapVersion =~ /.*[+\(\)\[\]].*/) {
            throw new InvalidUserDataException("Version expressions like '$intershopDeploymentBootstrapVersion' are not allowed for the property 'intershopDeploymentBootstrapVersion'. Please specify a complete version number.")
        }

        this.intershopDeploymentBootstrapVersion = intershopDeploymentBootstrapVersion
    }

    String oracleCartridgeVersion

    void setOracleCartridgeVersion(String oracleCartridgeVersion) {
        if (oracleCartridgeVersion =~ /.*[+\(\)\[\]].*/) {
            throw new InvalidUserDataException("Version expressions like '$oracleCartridgeVersion' are not allowed for the property 'oracleCartridgeVersion'. Please specify a version number including build number.")
        }

        this.oracleCartridgeVersion = oracleCartridgeVersion
    }


    String oracleClientVersion
    
    void setOracleClientVersion(String oracleClientVersion) {
        if (oracleClientVersion =~ /.*[+\(\)\[\]].*/) {
            throw new InvalidUserDataException("Version expressions like '$oracleClientVersion' are not allowed for the property 'oracleClientVersion'. Please specify a version number including build number.")
        }
        
        this.oracleClientVersion = oracleClientVersion
    }

}