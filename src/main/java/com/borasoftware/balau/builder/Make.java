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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Runs a Make process on the specified targets and in the specified build director.
 *
 * @author Nicholas Smethurst
 */
public class Make {
	/**
	 * Run the Make process.
	 *
	 * @param log the Maven plugin logger
	 * @param concurrency the number of make threads to run
	 * @param buildDirectory the directory in which Make will be executed
	 * @param targets the Make targets to build
	 * @param makeOptions extra command line options to pass to Make
	 * @param environmentVariables a map containing extra environment variables (may be null or empty)
	 * @param cmakePath optional path to CMake binary (if null or empty, the path is searched)
	 * @throws MojoExecutionException if an error occurs
	 */
	public static void runMakeTargets(Log log,
	                                  int concurrency,
	                                  Path buildDirectory,
	                                  List<String> targets,
	                                  List<String> makeOptions,
	                                  Map<String, String> environmentVariables,
	                                  String cmakePath) throws MojoExecutionException {
		try {
			final String cmake = cmakePath != null && !cmakePath.isEmpty() ? cmakePath : "cmake";
			final List<String> argumentsBase = new ArrayList<>();

			argumentsBase.add("--build");
			argumentsBase.add(".");

			if (concurrency > 1) {
				argumentsBase.add("--parallel");
				argumentsBase.add(Integer.toString(concurrency));
			}

			if (targets == null || targets.isEmpty()) {
				// Default target.
				runCommand(log, cmake, buildDirectory, argumentsBase, makeOptions, environmentVariables);
			} else {
				// Multiple targets.
				for (String target : targets) {
					final List<String> arguments = new ArrayList<>(argumentsBase);

					arguments.add("--target");
					arguments.add(target);

					runCommand(log, cmake, buildDirectory, arguments, makeOptions, environmentVariables);
				}
			}
		} catch (InterruptedException e) {
			throw new MojoExecutionException("Make command was interrupted.", e);
		} catch (IOException e) {
			throw new MojoExecutionException("Make command threw an error.", e);
		}
	}

	private static void runCommand(Log log,
	                               String cmake,
	                               Path buildDirectory,
	                               List<String> arguments,
	                               List<String> makeOptions,
	                               Map<String, String> environmentVariables) throws IOException, MojoExecutionException, InterruptedException {
		if (makeOptions != null && !makeOptions.isEmpty()) {
			arguments.add("--");
			arguments.addAll(makeOptions);
		}

		final Process process = Utilities.createProcess(
			log, cmake, buildDirectory, arguments, environmentVariables
		);

		Utilities.runProcess("make", log, process);
	}

	private Make() {}
}
