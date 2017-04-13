package perez.garcia.andres.utils;

import java.io.File;
import java.io.FilenameFilter;

import perez.garcia.andres.exceptions.CorpusSearchAutoException;

public class FileSystemUtils {

	public static void createDirectoryIfNotExist(File directory) throws CorpusSearchAutoException {
		if (directory.isFile()) {
			directory.delete();
		}

		if (!directory.isDirectory()) {
			if (!directory.mkdirs())
				throw new CorpusSearchAutoException("Unable to create " + directory.getName() + " folder (do you have permissions?).");
		}
	}

	public static void forceCreateDirectory(File directory) throws CorpusSearchAutoException {
		if (directory.exists()) {
			if (directory.isFile()) {
				directory.delete();
			} else {
				deleteDirectory(directory);
			}
		}
		if (!directory.mkdirs())
			throw new CorpusSearchAutoException("Unable to create " + directory.getName() + " folder (do you have permissions?).");
	}

	public static void deleteDirectory(File file) {
		File[] contents = file.listFiles();
		if (contents != null) {
			for (File f : contents) {
				deleteDirectory(f);
			}
		}
		file.delete();
	}
	
	public static class ExtensionFilter implements FilenameFilter {
		
		private final String extension;
		
		public ExtensionFilter(String extension) {
			this.extension = extension;
		}

		@Override
		public boolean accept(File dir, String name) {
			if (name.lastIndexOf('.') > 0) {
				int lastIndex = name.lastIndexOf('.');
				String ext = name.substring(lastIndex);
				return ext.equals("." + extension);
			} else {
				return false;
			}
		}
	}
}
