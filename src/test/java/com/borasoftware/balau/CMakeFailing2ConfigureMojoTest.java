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

import org.apache.maven.plugin.MojoExecutionException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Fail to perform CMake configure step due to incorrect path to cmake.
 */
public class CMakeFailing2ConfigureMojoTest extends AbstractCMakeMojoTest {
	public CMakeFailing2ConfigureMojoTest() {
		super("src/test/test-project3");
	}

	public void test() throws Exception {
		boolean threw = false;
		performClean();

		final Path outputDirectory = Paths.get("src").resolve("test").resolve("test-project3").resolve("target").resolve("cmake");
		Files.createDirectories(outputDirectory);

		final Path outputFile = outputDirectory.resolve("failMessage.txt");

		if (Files.exists(outputFile)) {
			Files.delete(outputFile);
		}

		try {
			try {
				performConfigure();
			} catch (MojoExecutionException e) {
				threw = true;
			}

			assertTrue(threw);
			assertTrue(Files.exists(outputFile));

			final String actual = new String(Files.readAllBytes(outputFile));
			final String expected = "Incorrect binary test script called.\n";
			assertEquals(expected, actual);
		} finally {
			Files.delete(outputFile);
		}
	}
}
