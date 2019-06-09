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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Utilities {
	public static Path getCMakeSourceDirectory(File projectBuildDirectory, File cmakeSourceDirectory) throws MojoExecutionException {
		if (cmakeSourceDirectory != null) {
			return cmakeSourceDirectory.toPath();
		}

		if (projectBuildDirectory == null) {
			throw new MojoExecutionException("${project.build.directory} was not supplied to plugin.");
		}

		return projectBuildDirectory.toPath().resolve("cmake");
	}

	public static Path getCMakeBinaryDirectory(File projectBuildDirectory, File cmakeBinaryDirectory) throws MojoExecutionException {
		if (cmakeBinaryDirectory != null) {
			return cmakeBinaryDirectory.toPath();
		}

		if (projectBuildDirectory == null) {
			throw new MojoExecutionException("${project.build.directory} was not supplied to plugin.");
		}

		return projectBuildDirectory.toPath().resolve("cmake");
	}

	public static int getConcurrency(int concurrency) {
		if (concurrency > 1) {
			return concurrency;
		}

		return Runtime.getRuntime().availableProcessors();
	}

	public static String getTargetsAsString(List<String> targets, boolean returnDefault) {
		if (targets == null || targets.isEmpty()) {
			return returnDefault ? "<default>" : "";
		}

		final StringBuilder builder = new StringBuilder();
		String prefix = "";

		for (String target : targets) {
			builder.append(prefix).append(target);
			prefix = " ";
		}

		return builder.toString();
	}

	public static String getDefinesAsString(Map<String, String> defines) throws MojoExecutionException {
		if (defines == null || defines.isEmpty()) {
			return "";
		}

		final StringBuilder builder = new StringBuilder();
		String prefix = "-D";

		for (Map.Entry<String, String> define : defines.entrySet()) {
			final String key = define.getKey();
			final String value = define.getValue();

			if (value == null || value.isEmpty()) {
				throw new MojoExecutionException("Supplied CMake command line definition with key '" + key + "' has no value supplied with it.");
			}

			builder.append(prefix).append(key.trim()).append("=").append(value.trim());
			prefix = " -D";
		}

		return builder.toString();
	}

	static Process createProcess(String command, int concurrency, Path buildDirectory, List<String> arguments) throws IOException {
		final List<String> commandLine = new ArrayList<>();

		commandLine.add(command);

		if (concurrency > 1) {
			commandLine.add("-j");
			commandLine.add(Integer.toString(concurrency));
		}

		if (arguments != null) {
			for (String argument : arguments) {
				commandLine.add(argument.trim());
			}
		}

		final ProcessBuilder builder = new ProcessBuilder(commandLine);

		builder.directory(buildDirectory.toFile());
		builder.redirectErrorStream(true);

		return builder.start();
	}

	static void runProcess(String command, Log log, Process process) throws IOException, InterruptedException, MojoExecutionException {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;

		while ((line = reader.readLine()) != null) {
			log.info(line);
		}

		final int exitStatus = process.waitFor();

		if (exitStatus != 0) {
			throw new MojoExecutionException(command + " command failed with exit status of " + exitStatus);
		}
	}

	private Utilities() {}
}
