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

package com.borasoftware.balau;

import com.borasoftware.balau.builder.Make;
import com.borasoftware.balau.builder.Utilities;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.nio.file.Path;

/**
 * Execute Make on the compile targets specified in the plugin configuration.
 *
 * @author Nicholas Smethurst
 */
@Mojo(name = "compile", defaultPhase = LifecyclePhase.COMPILE)
public class CMakeCompileMojo extends AbstractCMakeMojo {
	public void execute() throws MojoExecutionException {
		checkParameters();

		final int j = Utilities.getConcurrency(concurrency);
		final Path binDirectory = Utilities.getCMakeBinaryDirectory(projectBuildDirectory, cmakeBinaryDirectory);

		Make.runMakeTargets(getLog(), j, binDirectory, compileTargets, makeOptions, environmentVariables, cmakePath);
	}
}
