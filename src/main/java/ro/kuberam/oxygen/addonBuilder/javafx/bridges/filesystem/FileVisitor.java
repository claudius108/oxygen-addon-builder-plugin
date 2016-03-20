package ro.kuberam.oxygen.addonBuilder.javafx.bridges.filesystem;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import org.apache.log4j.Logger;


public class FileVisitor extends SimpleFileVisitor<Path> {

	/**
	 * Logger for logging.
	 */
	private static final Logger logger = Logger.getLogger(FileVisitor.class.getName());

	private ArrayList<PathMatcher> excludes = new ArrayList<PathMatcher>();
	private ArrayList<String> excludePatterns = new ArrayList<String>();
	private StringBuilder result = new StringBuilder();
	private String delim = "";
	private Path currentDirPath;

	FileVisitor(String[] patterns) {
		logger.debug("patterns = " + patterns);
		for (String pattern : patterns) {
			if (!pattern.equals("")) {
				pattern = pattern.trim();
				logger.debug("pattern = " + pattern);
				excludes.add(FileSystems.getDefault().getPathMatcher("glob:" + pattern));
				excludePatterns.add(pattern);
			}
		}
		logger.debug("excludes = " + excludes);
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dirPath, BasicFileAttributes attrs) throws IOException {

		logger.debug("dirPath = " + dirPath);

		for (int i = 0, il = excludes.size(); i < il; i++) {
			PathMatcher matcher = excludes.get(i);
			logger.debug("matcher '" + excludePatterns.get(i) + "' matches dir = "
					+ matcher.matches(dirPath));
			if (matcher.matches(dirPath)) {
				return FileVisitResult.SKIP_SUBTREE;
			}
		}

		result.append(delim).append("{\"title\": \"" + dirPath.getFileName() + "\",")
				.append(" \"key\": \"" + dirPath.getFileName() + "\",").append(" \"folder\": true,")
				.append(" \"children\": [");
		delim = "";

		currentDirPath = dirPath;

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path fullFilePath, BasicFileAttributes attrs) throws IOException {
		Path filePath = fullFilePath.getFileName();
		logger.debug("filePath = " + currentDirPath.resolve(filePath));
		logger.debug("file = " + fullFilePath);
		String fileName = filePath.toString();
		logger.debug("fileName = " + fileName);

		for (int i = 0, il = excludes.size(); i < il; i++) {
			PathMatcher matcher = excludes.get(i);
			logger.debug("matcher '" + excludePatterns.get(i) + "' matches file = "
					+ matcher.matches(filePath));
			if (matcher.matches(filePath)) {
				return FileVisitResult.CONTINUE;
			}
		}

		result.append(delim).append("{\"title\": \"" + fileName + "\",")
				.append(" \"key\": \"" + fileName + "\"}");
		delim = ",";

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		return super.visitFileFailed(file, exc);
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		result.append("]}");
		delim = ",";

		return FileVisitResult.CONTINUE;
	}

	public String getList() {
		return "[" + result.toString() + "]";
	}
}
