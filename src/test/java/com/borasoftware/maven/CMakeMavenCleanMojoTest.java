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

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

public class CMakeMavenCleanMojoTest extends AbstractMojoTestCase {
	private File testTargetFile;

	protected void setUp() throws Exception {
		super.setUp();

		final File pom = getTestFile("src/test/resources/test-project/pom.xml");
		final File target = new File(pom.getParentFile(), "target");
		final File cmakeTarget = new File(target, "cmake");

		testTargetFile = new File(cmakeTarget, "testTargetFile");

		assertTrue(cmakeTarget.mkdirs());
		assertTrue(testTargetFile.createNewFile());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void test() throws Exception {
		assertTrue(testTargetFile.exists());

		final File pom = getTestFile("src/test/resources/test-project/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		final CMakeMavenCleanMojo mojo = (CMakeMavenCleanMojo) lookupMojo("clean", pom);
		assertNotNull(mojo);
		mojo.execute();

		assertFalse(testTargetFile.exists());
	}
}
