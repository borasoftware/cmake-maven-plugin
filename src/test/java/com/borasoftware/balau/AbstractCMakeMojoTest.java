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

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;
import java.io.IOException;

abstract class AbstractCMakeMojoTest extends AbstractMojoTestCase {
	class TestFiles {
		final File pom;
		final File targetDirectory;
		final File cmakeTargetDirectory;
		final File testTargetFile;
		final File testTargetDirectory;

		TestFiles(File pom, File targetDirectory, File cmakeTargetDirectory, File testTargetFile, File testTargetDirectory) {
			this.pom = pom;
			this.targetDirectory = targetDirectory;
			this.cmakeTargetDirectory = cmakeTargetDirectory;
			this.testTargetFile = testTargetFile;
			this.testTargetDirectory = testTargetDirectory;
		}
	}

	final String testProjectLocation;
	TestFiles testFiles;

	AbstractCMakeMojoTest(String testProjectLocation) {
		this.testProjectLocation = testProjectLocation;
	}

	protected void setUp() throws Exception {
		super.setUp();
		testFiles = locateTestFiles();
		removeTestFiles(testFiles);
		createTestFiles(testFiles);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		removeTestFiles(testFiles);
	}

	void performClean() throws Exception {
		final File pom = getTestFile(testProjectLocation + "/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		final CMakeCleanMojo mojo = (CMakeCleanMojo) lookupMojo("clean", pom);
		assertNotNull(mojo);
		mojo.projectBuildDirectory = getTestFile(testProjectLocation + "/target");
		mojo.execute();
	}

	void performConfigure() throws Exception {
		final File pom = getTestFile(testProjectLocation + "/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		final CMakeConfigureMojo mojo = (CMakeConfigureMojo) lookupMojo("configure", pom);
		assertNotNull(mojo);
		mojo.projectBuildDirectory = getTestFile(testProjectLocation + "/target");
		mojo.execute();
	}

	private TestFiles locateTestFiles() {
		final File pom = AbstractMojoTestCase.getTestFile(testProjectLocation + "/pom.xml");
		final File targetDirectory = new File(pom.getParentFile(), "target");
		final File cmakeTargetDirectory = new File(targetDirectory, "cmake");

		return new TestFiles(
			  pom
			, targetDirectory
			, cmakeTargetDirectory
			, new File(cmakeTargetDirectory, "testTargetFile")
			, new File(cmakeTargetDirectory, "testTargetDirectory")
		);
	}

	private void createTestFiles(TestFiles testFiles) throws IOException {
		testFiles.cmakeTargetDirectory.mkdirs();
		assertTrue(testFiles.testTargetFile.createNewFile());
		assertTrue(testFiles.testTargetDirectory.mkdir());

	}

	private void removeTestFiles(TestFiles testFiles) {
		testFiles.testTargetDirectory.delete();
		testFiles.testTargetFile.delete();
		testFiles.cmakeTargetDirectory.delete();
		testFiles.targetDirectory.delete();
	}
}
