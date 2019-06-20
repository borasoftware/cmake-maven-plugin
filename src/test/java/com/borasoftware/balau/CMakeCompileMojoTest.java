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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Perform CMake clean, configure and compile steps.
 */
public class CMakeCompileMojoTest extends AbstractCMakeMojoTest {
	public CMakeCompileMojoTest() {
		super("src/test/test-project1");
	}

	public void test() throws Exception {
		performClean();
		performConfigure();

		final File pom = getTestFile(testProjectLocation + "/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		final CMakeCompileMojo mojo = (CMakeCompileMojo) lookupMojo("compile", pom);
		assertNotNull(mojo);
		mojo.projectBuildDirectory = getTestFile(testProjectLocation + "/target");
		mojo.execute();

		final Path appExecutable = testFiles.cmakeTargetDirectory.toPath().resolve("bin").resolve("App");
		assertTrue(Files.exists(appExecutable));

		// TODO library for other platforms.
		final Path libFile = testFiles.cmakeTargetDirectory.toPath().resolve("lib").resolve("libLib.a");
		assertTrue(Files.exists(libFile));
	}
}
