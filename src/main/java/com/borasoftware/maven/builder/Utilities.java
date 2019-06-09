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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

class Utilities {
	static Process createProcess(String command, int concurrency, Path buildDirectory, List<String> arguments) throws IOException {
		final int j = getConcurrency(concurrency);
		final List<String> commandLine = new ArrayList<>();

		commandLine.add(command);

		if (concurrency > 1) {
			commandLine.add("-j");
			commandLine.add(Integer.toString(j));
		}

		if (arguments != null) {
			commandLine.addAll(arguments);
		}

		final ProcessBuilder builder = new ProcessBuilder(commandLine);

		builder.directory(buildDirectory.toFile());
		builder.redirectErrorStream(true);

		return builder.start();
	}

	static void runProcess(String command, Log log, Process process) throws IOException, InterruptedException, MojoExecutionException {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		final String sep = System.getProperty("line.separator");
		while ((line = reader.readLine()) != null) {
			log.info(line + sep);
		}

		final int exitStatus = process.waitFor();

		if (exitStatus != 0) {
			throw new MojoExecutionException(command + " command failed with exit status of " + exitStatus);
		}
	}

	private static int getConcurrency(int concurrency) {
		if (concurrency != 0) {
			return concurrency;
		}

		// todo
		return 1;
	}

	private Utilities() {}
}
