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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Shared logic used in the mojos.
 *
 * @author Nicholas Smethurst
 */
public class Utilities {
	/**
	 * Given the optional CMake source directory parameter, determine the actual CMake source directory.
	 *
	 * @param projectBuildDirectory the Maven project build directory
	 * @param cmakeSourceDirectory the CMake source directory parameter (may be null)
	 * @return a path to the CMake source directory to use during the build
	 * @throws MojoExecutionException sanity check (should not throw)
	 */
	public static Path getCMakeSourceDirectory(File projectBuildDirectory, File cmakeSourceDirectory) throws MojoExecutionException {
		if (cmakeSourceDirectory != null) {
			return cmakeSourceDirectory.toPath();
		}

		if (projectBuildDirectory == null) {
			throw new MojoExecutionException("${project.build.directory} was not supplied to plugin.");
		}

		return projectBuildDirectory.toPath().resolve("cmake");
	}

	/**
	 * Given the optional CMake binary directory parameter, determine the actual CMake binary directory.
	 *
	 * @param projectBuildDirectory the Maven project build directory
	 * @param cmakeBinaryDirectory the CMake binary directory parameter (may be null)
	 * @return a path to the CMake binary directory to use during the build
	 * @throws MojoExecutionException sanity check (should not throw)
	 */
	public static Path getCMakeBinaryDirectory(File projectBuildDirectory, File cmakeBinaryDirectory) throws MojoExecutionException {
		if (cmakeBinaryDirectory != null) {
			return cmakeBinaryDirectory.toPath();
		}

		if (projectBuildDirectory == null) {
			throw new MojoExecutionException("${project.build.directory} was not supplied to plugin.");
		}

		return projectBuildDirectory.toPath().resolve("cmake");
	}

	/**
	 * Get the default concurrency if the specified concurrency is zero, otherwise return the specified concurrency.
	 *
	 * @param concurrency the specified concurrency or zero if the default concurrency should be used
	 * @return the default concurrency if the specified concurrency is zero, otherwise return the specified concurrency
	 */
	public static int getConcurrency(int concurrency) {
		if (concurrency > 1) {
			return concurrency;
		}

		return Runtime.getRuntime().availableProcessors();
	}

	/**
	 * Concatenate the supplied targets for logging purposes.
	 *
	 * @param targets the targets
	 * @param returnDefault when set to true, return the default string instead
	 * @return the targets concatenated
	 */
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

	/**
	 * Get the supplied CMake command line definitions as a string for logging purposes.
	 *
	 * @param defines the definitions
	 * @return the definitions concatenated
	 * @throws MojoExecutionException if a definition does not have a value supplied
	 */
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

	/**
	 * Create a new process with the specified command line arguments.
	 *
	 * @param command the command
	 * @param concurrency specific to the Make process, specifies the required concurrency
	 * @param buildDirectory the working directory in which the process will launch
	 * @param arguments the command line arguments
	 * @return a new process
	 * @throws IOException if an I/O error occurs
	 */
	static Process createProcess(String command,
	                             int concurrency,
	                             Path buildDirectory,
	                             List<String> arguments) throws IOException {
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

	/**
	 * Log the process' stdout to the Maven logger and wait for the process to complete.
	 *
	 * @param command the original command supplied to the process (for error logging purposes)
	 * @param log the Maven logger
	 * @param process the process
	 * @exception  IOException  If an I/O error occurs
	 * @throws InterruptedException if the current thread is
	 *         {@linkplain Thread#interrupt() interrupted} by another
	 *         thread while it is waiting, then the wait is ended and
	 *         an {@link InterruptedException} is thrown.
	 * @throws MojoExecutionException if the exit status of the process is non-zero
	 */
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
