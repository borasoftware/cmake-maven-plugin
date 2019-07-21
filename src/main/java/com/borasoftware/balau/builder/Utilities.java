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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Shared logic used in the mojos.
 *
 * @author Nicholas Smethurst
 */
public class Utilities {
	//
	// In order to avoid clashes with Maven properties, environment
	// variable placeholders must be specified with a %VAR% syntax.
	//
	private static final Pattern ENV_VAR_PLACEHOLDER_REGEX = Pattern.compile("%[a-zA-Z0-9_-]+%");

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
	 * Concatenate the supplied string list for logging purposes.
	 *
	 * @param targets the targets
	 * @param delimiter the delimiter to separate the strings with
	 * @param defaultValue a default value to return if the list is null or empty
	 * @return the strings concatenated
	 */
	public static String concatenateStringList(List<String> targets, String delimiter, String defaultValue) {
		if (targets == null || targets.isEmpty()) {
			return defaultValue != null ? defaultValue : "";
		}

		final StringBuilder builder = new StringBuilder();
		String prefix = "";

		for (String target : targets) {
			builder.append(prefix).append(target);
			prefix = delimiter;
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
				// Skip.
				continue;
			}

			builder.append(prefix).append(key.trim()).append("=").append(value.trim());
			prefix = " -D";
		}

		return builder.toString();
	}

	/**
	 * Create a new process with the specified command line arguments.
	 *
	 * @param log the Maven plugin logger
	 * @param command the command
	 * @param buildDirectory the working directory in which the process will launch
	 * @param arguments the command line arguments
	 * @param environmentVariables a map containing extra environment variables (may be null or empty)
	 * @return a new process
	 * @throws IOException if an I/O error occurs
	 */
	static Process createProcess(Log log,
	                             String command,
	                             Path buildDirectory,
	                             List<String> arguments,
	                             Map<String, String> environmentVariables) throws IOException, MojoExecutionException {
		final List<String> commandLine = new ArrayList<>();

		commandLine.add(command);

		if (arguments != null) {
			for (String argument : arguments) {
				commandLine.add(argument.trim());
			}
		}

		log.info("command line: " + concatenateStringList(commandLine, " ", ""));

		final ProcessBuilder builder = new ProcessBuilder(commandLine);

		builder.directory(buildDirectory.toFile());
		builder.redirectErrorStream(true);

		updateEnvironment(builder.environment(), environmentVariables);

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

	//
	// Update the environment of the supplied process builder if additional environment variables have been specified.
	// Any existing referenced environment variables within the supplied environment variables will be expanded.
	//
	static void updateEnvironment(Map<String, String> processEnvironment,
	                              Map<String, String> suppliedEnvironmentVariables) throws MojoExecutionException {
		if (suppliedEnvironmentVariables == null || suppliedEnvironmentVariables.isEmpty()) {
			// No environment variables supplied.
			return;
		}

		try {
			final Map<String, String> originalEnvironment = new HashMap<>(processEnvironment);

			for (Map.Entry<String, String> entry : suppliedEnvironmentVariables.entrySet()) {
				final String name = entry.getKey();
				final String value = entry.getValue();
				final Matcher matcher = ENV_VAR_PLACEHOLDER_REGEX.matcher(value);
				int start, end;

				if (matcher.find()) {
					final StringBuilder newValue = new StringBuilder();

					start = matcher.start();
					end = matcher.end();
					newValue.append(value, 0, start);

					// Extract environment variable name (remove leading and trailing % characters).
					String originalEnvironmentName = value.substring(start + 1, end - 1);
					String originalEnvironmentValue = originalEnvironment.get(originalEnvironmentName);

					// Add the value if such an environment variable exists in the process builder's environment.
					if (originalEnvironmentValue != null) {
						newValue.append(originalEnvironmentValue);
					}

					while (matcher.find()) {
						start = matcher.start();
						newValue.append(value, end, start);
						end = matcher.end();

						// Extract environment variable name (remove leading and trailing % characters).
						originalEnvironmentName = value.substring(start + 1, end - 1);
						originalEnvironmentValue = originalEnvironment.get(originalEnvironmentName);

						// Add the value if such an environment variable exists in the process builder's environment.
						if (originalEnvironmentValue != null) {
							newValue.append(originalEnvironmentValue);
						}
					}

					processEnvironment.put(name, newValue.toString());
				} else {
					// No placeholders to expand.. set the environment variable as is.
					processEnvironment.put(name, value);
				}
			}
		} catch (UnsupportedOperationException | IllegalArgumentException e) {
			throw new MojoExecutionException("Failed to update environment due to a restriction of the operating system: ", e);
		}
	}

	private Utilities() {}
}
