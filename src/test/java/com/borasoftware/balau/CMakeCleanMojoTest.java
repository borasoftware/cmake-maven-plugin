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

/**
 * Clean CMake build files and folders.
 */
public class CMakeCleanMojoTest extends AbstractCMakeMojoTest {
	public CMakeCleanMojoTest() {
		super("src/test/test-project1");
	}

	public void test() throws Exception {
		assertTrue(testFiles.testTargetFile.exists());
		performClean();
		assertFalse(testFiles.testTargetFile.exists());
	}
}
