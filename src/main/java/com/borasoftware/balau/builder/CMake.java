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

package com.borasoftware.balau.builder;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Runs a CMake process with the supplied parameters.
 *
 * @author Nicholas Smethurst
 */
public class CMake {
	/**
	 * Run the CMake process.
	 *
	 * @param log the Maven plugin logger
	 * @param buildDirectory the directory in which CMake will be executed
	 * @param parameters the command line parameters to be specified to CMake
	 * @param environmentVariables a map containing extra environment variables (may be null or empty)
	 * @param cmakePath optional path to CMake binary (if null or empty, the path is searched)
	 * @throws MojoExecutionException if an error occurs
	 */
	public static void runCMake(Log log,
	                            Path buildDirectory,
	                            List<String> parameters,
	                            Map<String, String> environmentVariables,
	                            String cmakePath) throws MojoExecutionException {
		try {
			final String cmake = cmakePath != null && !cmakePath.isEmpty() ? cmakePath : "cmake";
			final Process process = Utilities.createProcess(log, cmake, buildDirectory, parameters, environmentVariables);
			Utilities.runProcess("cmake", log, process);
		} catch (InterruptedException e) {
			throw new MojoExecutionException("CMake command was interrupted.", e);
		} catch (IOException e) {
			throw new MojoExecutionException("CMake command threw an error.", e);
		}
	}

	private CMake() {}
}
