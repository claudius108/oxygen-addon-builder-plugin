package ro.kuberam.oxygen.addonBuilder.javafx.bridges.filesystem;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import ro.kuberam.oxygen.addonBuilder.javafx.JavaFXDialog;
import ro.kuberam.oxygen.addonBuilder.javafx.bridges.BaseBridge;

public class FileSystemBridge extends BaseBridge {

	/**
	 * 
	 */
	private static final long serialVersionUID = -630908644080282667L;

	public FileSystemBridge() {
	}

	public FileSystemBridge(JavaFXDialog dialogWindow) {
		super(dialogWindow);
	}

	public String list(Path directoryPath, String filter) {
		logger.debug("directoryPath in list() = " + directoryPath);
		logger.debug("filter = " + filter);

		String fileTree = FileSystem.getDirectoryTree(directoryPath, filter.split(","));
		logger.debug("fileTree = " + fileTree);

		return fileTree;
	}

	public boolean create(String newPathAsString, boolean isDirectory) {
		Boolean result = true;

		logger.debug("currentPathAsString = " + newPathAsString);
		logger.debug("isDirectory = " + isDirectory);

		Path newPath = Paths.get(newPathAsString);

		try {
			if (isDirectory) {
				Files.createDirectories(newPath);
			} else {
				Files.createFile(newPath);
			}
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		}

		return result;
	}

	public void rename(String currentPathAsString, String newName) {
		logger.debug("currentPathAsString = " + currentPathAsString);
		logger.debug("newName = " + newName);
		Path currentPath = Paths.get(currentPathAsString);
		logger.debug("currentPath = " + currentPath);

		try {
			Files.move(currentPath.toAbsolutePath(), currentPath.resolveSibling(newName).toAbsolutePath(),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean move(String sourcePathAsString, String targetPathAsString) {
		Boolean result = true;

		logger.debug("sourcePathAsString = " + sourcePathAsString);
		logger.debug("targetPathAsString = " + targetPathAsString);
		Path sourcePath = Paths.get(sourcePathAsString);
		Path sourceItem = sourcePath.getFileName();
		logger.debug("sourceItem = " + sourceItem);
		Path targetPath = Paths.get(targetPathAsString);

		try {
			Files.move(sourcePath, targetPath.resolve(sourceItem));
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		}

		return result;
	}

	public boolean copy(String sourcePathAsString, String targetPathAsString) {
		Boolean result = true;
		logger.debug("sourcePathAsString = " + sourcePathAsString);
		logger.debug("targetPathAsString = " + targetPathAsString);
		Path sourcePath = Paths.get(sourcePathAsString);
		Path sourceItem = sourcePath.getFileName();
		logger.debug("sourceItem = " + sourceItem);
		Path targetPath = Paths.get(targetPathAsString);

		try {
			Files.copy(sourcePath, targetPath.resolve(sourceItem), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		}

		return result;
	}

	public void delete(String currentPathAsString) {
		logger.debug("currentPathAsString = " + currentPathAsString);

		try {
			Files.walkFileTree(Paths.get(currentPathAsString), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}

			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void edit(String currentPathAsString) {
		logger.debug("currentPathAsString = " + currentPathAsString);

		try {
			pluginWorkspaceAccess.open(Paths.get(currentPathAsString).toUri().toURL());
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
	}

	public void search(String currentPathAsString) {
		// Path rootPath = Paths.get("data");
		// String fileToFind = File.separator + "README.txt";
		//
		// try {
		// Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
		//
		// @Override
		// public FileVisitResult visitFile(Path file, BasicFileAttributes
		// attrs) throws IOException {
		// String fileString = file.toAbsolutePath().toString();
		// //System.out.println("pathString = " + fileString);
		//
		// if(fileString.endsWith(fileToFind)){
		// System.out.println("file found at path: " + file.toAbsolutePath());
		// return FileVisitResult.TERMINATE;
		// }
		// return FileVisitResult.CONTINUE;
		// }
		// });
		// } catch(IOException e){
		// e.printStackTrace();
		// }
	}
}
