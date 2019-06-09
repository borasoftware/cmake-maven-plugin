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

package com.borasoftware.maven.builder;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class CMake {
	public static void runCMake(Log log, Path buildDirectory, List<String> parameters) throws MojoExecutionException {
		try {
			final Process process = Utilities.createProcess("cmake", 1, buildDirectory, parameters);
			Utilities.runProcess("cmake", log, process);
		} catch (InterruptedException e) {
			throw new MojoExecutionException("CMake command was interrupted.", e);
		} catch (IOException e) {
			throw new MojoExecutionException("CMake command threw an error.", e);
		}
	}

	private CMake() {}
}
