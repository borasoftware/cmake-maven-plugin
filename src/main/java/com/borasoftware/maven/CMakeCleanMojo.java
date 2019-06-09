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

import com.borasoftware.maven.builder.Utilities;
import com.borasoftware.maven.cleaner.Cleaner;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Mojo(name = "clean", threadSafe = true, defaultPhase = LifecyclePhase.CLEAN)
public class CMakeCleanMojo extends AbstractCMakeMojo {
	public void execute() throws MojoExecutionException {
		final Log log = getLog();
		final Path buildDirectory = Utilities.getCMakeBinaryDirectory(projectBuildDirectory, cmakeBinaryDirectory);

		log.debug("cmakeBinaryDirectory   = " + buildDirectory);
		log.debug("verbose               = " + verbose);
		log.debug("followSymLinks        = " + followSymLinks);
		log.debug("skip                  = " + skip);
		log.debug("failOnError           = " + failOnError);
		log.debug("retryOnError          = " + retryOnError);

		if (skip) {
			log.info("Clean is skipped.");
			return;
		}

		final Cleaner cleaner = new Cleaner(log, isVerbose());

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
