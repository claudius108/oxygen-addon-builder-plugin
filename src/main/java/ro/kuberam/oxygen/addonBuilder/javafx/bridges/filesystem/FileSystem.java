package ro.kuberam.oxygen.addonBuilder.javafx.bridges.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSystem {

	public static String getDirectoryTree(Path directoryPath, String[] patterns) {
		FileVisitor fileVisitor = new FileVisitor(patterns);

		try {
			Files.walkFileTree(directoryPath, fileVisitor);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String directoryTree = fileVisitor.getList();

		return directoryTree;
	}

}
