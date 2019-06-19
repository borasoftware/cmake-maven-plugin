package com.borasoftware.balau.builder;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class UtilitiesTest {
	@Test
	public void envVarRegexMatchingPlaceholderExecutesCorrectly() throws MojoExecutionException {
		final Map<String, String> processEnvironment = new HashMap<String, String>() {{
			put("A", "abc");
			put("PATH", "def:ghi");
			put("B", "jkl");
		}};


		final Map<String, String> suppliedEnvironmentVariables = new HashMap<String, String>() {{
			put("PATH", "mno:pqr:%PATH%");
		}};

		Utilities.updateEnvironment(processEnvironment, suppliedEnvironmentVariables);

		final String path = processEnvironment.get("PATH");

		assertEquals("mno:pqr:def:ghi", path);
	}

	@Test
	public void envVarRegexMatchingTwoPlaceholdersExecutesCorrectly() throws MojoExecutionException {
		final Map<String, String> processEnvironment = new HashMap<String, String>() {{
			put("A", "abc");
			put("PATH", "def:ghi");
			put("B", "jkl");
		}};


		final Map<String, String> suppliedEnvironmentVariables = new HashMap<String, String>() {{
			put("PATH", "mno:pqr:%PATH%:%B%");
		}};

		Utilities.updateEnvironment(processEnvironment, suppliedEnvironmentVariables);

		final String path = processEnvironment.get("PATH");

		assertEquals("mno:pqr:def:ghi:jkl", path);
	}

	@Test
	public void envVarRegexNonMatchingPlaceholderExecutesCorrectly() throws MojoExecutionException {
		final Map<String, String> processEnvironment = new HashMap<String, String>() {{
			put("A", "abc");
			put("AAPATHAA", "def:ghi");
			put("B", "jkl");
		}};


		final Map<String, String> suppliedEnvironmentVariables = new HashMap<String, String>() {{
			put("PATH", "jkl:mno:%PATH%");
		}};

		Utilities.updateEnvironment(processEnvironment, suppliedEnvironmentVariables);

		final String path = processEnvironment.get("PATH");

		assertEquals("jkl:mno:", path);
	}
}
