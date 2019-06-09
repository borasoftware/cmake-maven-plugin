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

import com.borasoftware.maven.builder.Make;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Mojo(name = "test-compile", defaultPhase = LifecyclePhase.TEST_COMPILE)
public class CMakeMavenTestCompileMojo extends AbstractMojo {
	@Parameter
	private String outputDirectory;

	@Parameter
	private File projectBuildDirectory;

	// The folder in which the CMake build will be made.
	// Defaults to "${project.build.directory}/cmake".
	@Parameter
	private File cmakeProjectBuildDirectory;

	// Additional command line definitions specified when running CMake.
	// TODO platform specific differences.
	@Parameter
	private Map<String, String> defines;

	// The concurrency value specified to make (default to the number of cores).
	@Parameter
	private int concurrency;

	// Attach Make targets to the Maven test-compile phase.
	// If no targets are defined, no Make target will be run.
	@Parameter
	private List<String> testCompile;

	public void execute() throws MojoExecutionException {
		final Path buildDirectory = cmakeProjectBuildDirectory != null
			? cmakeProjectBuildDirectory.toPath()
			: projectBuildDirectory.toPath().resolve("cmake");

		getLog().info("CMakeMavenTestCompileMojo: buildDirectory = " + buildDirectory);

		if (testCompile == null || testCompile.isEmpty()) {
			// No default target for test compilation.
			return;
		}

		Make.runMakeTargets(getLog(), concurrency, buildDirectory, testCompile);
	}
}
