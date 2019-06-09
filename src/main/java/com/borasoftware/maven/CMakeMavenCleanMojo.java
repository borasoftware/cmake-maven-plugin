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

package com.borasoftware.maven;

import com.borasoftware.maven.cleaner.Cleaner;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Mojo(name = "clean", threadSafe = true, defaultPhase = LifecyclePhase.CLEAN)
public class CMakeMavenCleanMojo extends AbstractMojo {
	@Parameter(defaultValue = "${project.build.directory}", readonly = true, required = true)
	private File projectBuildDirectory;

	// The folder in which the CMake build will be made.
	// Defaults to "${project.build.directory}/cmake".
	@Parameter(defaultValue = "${cmake.project.build.directory}", readonly = true)
	private File cmakeProjectBuildDirectory;

	@Parameter(property = "maven.clean.verbose")
	private Boolean verbose;

	@Parameter(property = "maven.clean.followSymLinks", defaultValue = "false")
	private boolean followSymLinks;

	@Parameter(property = "maven.clean.skip", defaultValue = "false")
	private boolean skip;

	@Parameter(property = "maven.clean.failOnError", defaultValue = "true")
	private boolean failOnError;

	@Parameter(property = "maven.clean.retryOnError", defaultValue = "true")
	private boolean retryOnError;

	public void execute() throws MojoExecutionException {
		final Path buildDirectory = cmakeProjectBuildDirectory != null
			? cmakeProjectBuildDirectory.toPath()
			: projectBuildDirectory.toPath().resolve("cmake");

		getLog().info("CMakeMavenCleanMojo: buildDirectory = " + buildDirectory);

		if (skip) {
			getLog().info("Clean is skipped.");
			return;
		}

		final Cleaner cleaner = new Cleaner(getLog(), isVerbose());

		try {
			final File directoryItem = buildDirectory.toFile();

			if (directoryItem != null) {
				cleaner.delete(directoryItem, null, followSymLinks, failOnError, retryOnError);
			}
		} catch (IOException e) {
			throw new MojoExecutionException("Failed to clean project: " + e.getMessage(), e);
		}
	}

	private boolean isVerbose() {
		return (verbose != null) ? verbose : getLog().isDebugEnabled();
	}
}