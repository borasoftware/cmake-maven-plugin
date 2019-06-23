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

import com.borasoftware.balau.builder.CMake;
import com.borasoftware.balau.builder.Utilities;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Execute CMake to configure the build.
 *
 * @author Nicholas Smethurst
 */
@Mojo(name = "configure", defaultPhase = LifecyclePhase.VALIDATE)
public class CMakeConfigureMojo extends AbstractCMakeMojo {
	public void execute() throws MojoExecutionException {
		checkParameters();

		final Log log = getLog();
		final Path srcDirectory = Utilities.getCMakeSourceDirectory(projectBuildDirectory, cmakeSourceDirectory);
		final Path binDirectory = Utilities.getCMakeBinaryDirectory(projectBuildDirectory, cmakeBinaryDirectory);

		log.debug("cmakeSourceDirectory = " + srcDirectory);
		log.debug("cmakeBinaryDirectory = " + binDirectory);
		log.debug("cmakeDefines         = " + Utilities.getDefinesAsString(cmakeDefines));

		try {
			Files.createDirectories(binDirectory);
		} catch (IOException e) {
			throw new MojoExecutionException("Cannot create CMake build directory: " + binDirectory);
		}

		final List<String> parameters = processDefines();

		// Set the generator if necessary.
		final String osName = System.getProperty("os.name");

		if (generator != null && !generator.isEmpty()) {
			parameters.add("-G\"" + generator.trim() + "\"");
		} else if (osName != null && osName.toLowerCase().trim().startsWith("windows")) {
			parameters.add("-G\"NMake Makefiles\"");
		}

		parameters.addAll(processDefines());
		parameters.add(srcDirectory.toAbsolutePath().toString());

		CMake.runCMake(log, binDirectory, parameters, environmentVariables, cmakePath);
	}

	private List<String> processDefines() {
		final List<String> list = new ArrayList<>();

		if (cmakeDefines != null && !cmakeDefines.isEmpty()) {
			for (Map.Entry<String, String> entry : cmakeDefines.entrySet()) {
				final String key = entry.getKey();
				final String value = entry.getValue();
				list.add("-D" + key + "=" + value);
			}
		}

		return list;
	}
}
