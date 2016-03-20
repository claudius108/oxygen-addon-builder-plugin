package ro.kuberam.oxygen.addonBuilder.javafx.bridges.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class FileSystem {

	public static String getDirectoryTree(String directoryPath, String[] patterns) {
		FileVisitor fileVisitor = new FileVisitor(patterns);
		
		try {
			Files.walkFileTree(Paths.get(directoryPath), fileVisitor);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String directoryTree = fileVisitor.getList();

		return directoryTree;
	}

}
