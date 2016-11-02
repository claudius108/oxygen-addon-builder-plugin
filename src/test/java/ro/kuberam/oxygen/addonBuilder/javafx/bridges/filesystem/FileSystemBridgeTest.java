package ro.kuberam.oxygen.addonBuilder.javafx.bridges.filesystem;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class FileSystemBridgeTest {

	@Test
	public void testGetFrameworkNames() throws URISyntaxException {
		String fileTree = FileSystem.getDirectoryTree(
				Paths.get(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()),
				new String[] { "**/frameworks/**/**", "*.*" });

		System.out.println(fileTree);

	}

	@Test
	public void testGetFileList() throws URISyntaxException {
		String fileTree = FileSystem.getDirectoryTree(
				Paths.get(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()),
				new String[] { "*.xml", "**/utils" });

		System.out.println(fileTree);

	}

	@Test
	public void testRename() {
		Path currentPath = null;
		try {
			currentPath = Files.createTempFile("XQueryExecutable", ".xqc");
		} catch (IOException e) {
			e.printStackTrace();
		}

		(new FileSystemBridge()).rename(currentPath.toString(), "new-name.xqc");
	}

}
