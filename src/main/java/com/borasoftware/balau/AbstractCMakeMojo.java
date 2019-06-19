/*
 * Copyright (C) 2019 Bora Software (contact@borasoftware.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.borasoftware.balau;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Abstract based class of all the CMake mojos.
 *
 * @author Nicholas Smethurst
 */
abstract class AbstractCMakeMojo extends AbstractMojo {
	@Parameter(defaultValue = "${project.build.directory}", readonly = true, required = true)
	File projectBuildDirectory;

	// The folder in which the source code is located.
	@Parameter(defaultValue = "${project.basedir}", readonly = true)
	File cmakeSourceDirectory;

	// The folder in which the CMake build will be made.
	@Parameter(defaultValue = "${project.build.directory}/cmake", readonly = true)
	File cmakeBinaryDirectory;

	@Parameter(defaultValue = "false")
	Boolean verbose;

	@Parameter(defaultValue = "false")
	boolean followSymLinks;

	@Parameter(defaultValue = "false")
	boolean skip;

	@Parameter(defaultValue = "false")
	boolean failOnError;

	@Parameter(defaultValue = "false")
	boolean retryOnError;

	// The concurrency value specified to make (default to the number of cores).
	@Parameter(defaultValue = "0", readonly = true)
	int concurrency;

	// Attach Make targets to the Maven compile phase.
	// If no targets are defined, Make will be run with its default target.
	@Parameter(readonly = true)
	List<String> compileTargets;

	// Attach Make targets to the Maven test-compile phase.
	// If no targets are defined, no Make targets will be run.
	@Parameter(readonly = true)
	List<String> testCompileTargets;

	// Additional command line definitions specified when running CMake.
	@Parameter
	Map<String, String> cmakeDefines;

	// Additional environment variables specified when running CMake and Make.
	@Parameter
	Map<String, String> environmentVariables;
}
