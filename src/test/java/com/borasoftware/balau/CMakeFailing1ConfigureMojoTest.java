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

/**
 * Fail to perform CMake configure step due to incorrect path supplied.
 */
public class CMakeFailing1ConfigureMojoTest extends AbstractCMakeMojoTest {
	public CMakeFailing1ConfigureMojoTest() {
		super("src/test/test-project2");
	}

	public void test() throws Exception {
		boolean threw = false;
		performClean();

		try {
			performConfigure();
		} catch (MojoExecutionException e) {
			threw = true;
		}

		assertTrue(threw);
	}
}
