package ro.kuberam.maven.plugins.expath.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import ro.kuberam.maven.plugins.expath.Utils;

public class ProcessOutputDirectoryTest {

	@Test
	public void testNullOutputDirectory() throws Exception {
		String outputDirectory = null;
		String processedOutputDirectory = Utils.processOutputDirectory(outputDirectory);
		assertTrue(processedOutputDirectory.equals("content/"));
	}

	@Test
	public void testEmptyOutputDirectory() throws Exception {
		String outputDirectory = "";
		String processedOutputDirectory = Utils.processOutputDirectory(outputDirectory);
		assertTrue(processedOutputDirectory.equals("content/"));
	}

	@Test
	public void testForwardSlashOutputDirectory() throws Exception {
		String outputDirectory = "/";
		String processedOutputDirectory = Utils.processOutputDirectory(outputDirectory);
		assertTrue(processedOutputDirectory.equals("content/"));
	}

	@Test
	public void testSimpleOutputDirectory1() throws Exception {
		String outputDirectory = "a";
		String processedOutputDirectory = Utils.processOutputDirectory(outputDirectory);
		assertTrue(processedOutputDirectory.equals("content/a/"));
	}

	@Test
	public void testSimpleOutputDirectory2() throws Exception {
		String outputDirectory = "/a";
		String processedOutputDirectory = Utils.processOutputDirectory(outputDirectory);
		assertTrue(processedOutputDirectory.equals("content/a/"));
	}

	@Test
	public void testSimpleOutputDirectory3() throws Exception {
		String outputDirectory = "/a/";
		String processedOutputDirectory = Utils.processOutputDirectory(outputDirectory);
		assertTrue(processedOutputDirectory.equals("content/a/"));
	}

	@Test
	public void testComplexOutputDirectory1() throws Exception {
		String outputDirectory = "a/b";
		String processedOutputDirectory = Utils.processOutputDirectory(outputDirectory);
		assertTrue(processedOutputDirectory.equals("content/a/b/"));
	}
	
	@Test
	public void testComplexOutputDirectory2() throws Exception {
		String outputDirectory = "/a/b";
		String processedOutputDirectory = Utils.processOutputDirectory(outputDirectory);
		assertTrue(processedOutputDirectory.equals("content/a/b/"));
	}
	
	@Test
	public void testComplexOutputDirectory3() throws Exception {
		String outputDirectory = "/a/b/";
		String processedOutputDirectory = Utils.processOutputDirectory(outputDirectory);
		assertTrue(processedOutputDirectory.equals("content/a/b/"));
	}	

}
