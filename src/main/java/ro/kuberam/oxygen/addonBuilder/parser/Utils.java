package ro.kuberam.oxygen.addonBuilder.parser;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Map;

public class Utils {

	public static ArrayList<String> generateDatalistImportStatements(Map<String, String> datalists) {
		ArrayList<String> datalistImportStatements = new ArrayList<>();
		datalistImportStatements.add("@charset \"utf-8\";");

		for (Map.Entry<String, String> datalist : datalists.entrySet()) {
			datalistImportStatements.add("@import \"datalists/" + datalist.getKey() + ".less\";");
		}

		return datalistImportStatements;
	}

	public static void deleteDirectoryContent(Path directory) {
		try {
			Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
