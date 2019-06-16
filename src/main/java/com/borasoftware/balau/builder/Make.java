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
	 */
	public static void runMakeTargets(Log log, int concurrency, Path buildDirectory, List<String> targets) throws MojoExecutionException {
		try {
			final Process process = Utilities.createProcess("make", concurrency, buildDirectory, targets);
			Utilities.runProcess("make", log, process);
		} catch (InterruptedException e) {
			throw new MojoExecutionException("Make command was interrupted.", e);
		} catch (IOException e) {
			throw new MojoExecutionException("Make command threw an error.", e);
		}
	}

	private Make() {}
}
