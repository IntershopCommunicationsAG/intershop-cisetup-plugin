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
package com.intershop.gradle.cisetup.templates

import groovy.transform.CompileStatic

import java.util.jar.Attributes
import java.util.jar.Manifest

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * This is the abstract template task for all other
 * template create tasks.
 */
@CompileStatic
abstract class AbstractTemplate extends DefaultTask {

    /**
     * This it the distribution url for the gradle
     * wrapper of the corporate distribution or standard
     * distribution.
     */
	@Input
	String distributionURL

    /**
     * Output directory
     */
	@OutputDirectory
	File outputDir
	
	abstract void create(Properties props, File dir)

    /**
     * Task action
     */
    @TaskAction
	void create() {
		Properties props = new Properties()
		
		File dir = getOutputDir()
		if(! dir.exists()) {
			dir.mkdirs()
		}
		setTemplateInfo(props)
		
		props['DistributionURL'] = getDistributionURL()
				
		create(props, dir)
	}

    /**
     * Get string helper method
     *
     * @param list
     * @return
     */
	static String getString(String[] list) {	
		String returnValue = ''
		list.each { it ->
			returnValue += returnValue ? ',' : ''
			returnValue += it
		}
		if(returnValue.endsWith(',')) {
			returnValue = returnValue.substring(0, returnValue.length() - 1)
		}
		return returnValue
	}

    /**
     * set template info helper
     *
     * @param props
     */
	void setTemplateInfo(Properties props) {
		def now = new Date()
		URLClassLoader cl = (URLClassLoader) getClass().getClassLoader()
		try {
		  URL url = cl.findResource("META-INF/MANIFEST.MF")
		  Manifest manifest = new Manifest(url.openStream())
		  Attributes attr = manifest.getMainAttributes()

		  props.setProperty('TemplateVersionString', attr.getValue("Implementation-Version"))
		  props.setProperty('TemplateTimeString', attr.getValue("Build-Time")?:'')
		  props.setProperty('TemplateGenerationTime', now.format('YYYYMMddHHmmss'))
		  
		} catch (IOException ex) {
		  println ex
		}
	}
}
