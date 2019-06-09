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

import com.borasoftware.maven.builder.CMake;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mojo(name = "configure", defaultPhase = LifecyclePhase.VALIDATE)
public class CMakeMavenConfigureMojo extends AbstractMojo {
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

	public void execute() throws MojoExecutionException {
		final Path buildDirectory = cmakeProjectBuildDirectory != null
			? cmakeProjectBuildDirectory.toPath()
			: projectBuildDirectory.toPath().resolve("cmake");

		getLog().info("CMakeMavenConfigureMojo: buildDirectory = " + buildDirectory);

		final List<String> parameters = processDefines();

		parameters.add(buildDirectory.toAbsolutePath().toString());

		CMake.runCMake(getLog(), buildDirectory, parameters);
	}

	private List<String> processDefines() {
		final List<String> list = new ArrayList<>();

		if (defines != null && !defines.isEmpty()) {
			for (Map.Entry<String, String> entry : defines.entrySet()) {
				final String key = entry.getKey();
				final String value = entry.getValue();
				list.add("-D" + key + (value != null && !value.isEmpty() ? ("=" + value) : ""));
			}
		}

		return list;
	}
}
