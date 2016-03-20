package ro.kuberam.oxygen.addonBuilder.javafx.bridges.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

public class FileSystemBridgeTest {

	@Test
	public void testGetFrameworksNames() {
		String fileTree = FileSystem.getDirectoryTree((new File(getClass().getProtectionDomain()
				.getCodeSource().getLocation().getFile())).getAbsolutePath(), new String[] {
				"**/frameworks/**/**", "*.*" });

		System.out.println(fileTree);

	}

	@Test
	public void testGetFileList() {
		String fileTree = FileSystem.getDirectoryTree((new File(getClass().getProtectionDomain()
				.getCodeSource().getLocation().getFile())).getAbsolutePath(), new String[] { "*.xml",
				"**/utils" });

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
