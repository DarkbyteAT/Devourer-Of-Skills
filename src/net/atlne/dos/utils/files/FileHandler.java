package net.atlne.dos.utils.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileHandler extends File {
	
	/**Finds all of the files within all of the sub-folders of a given folder. <br>
	 * Recursively applies itself to each sub-folder found and adds the result to the main list.*/
	public static ArrayList<File> getAllFilesInFolder(File folder) {
		/**Stores the files in the folder.*/
		ArrayList<File> files = new ArrayList<File>();
		
		/**Checks if the file is a folder before beginning.*/
		if(folder.isDirectory()) {
			/**Iterates over each file in the current directory.*/
			for(File file : folder.listFiles()) {
				/**Checks if the file is a directory.*/
				if(file.isDirectory()) {
					/**Adds the files within to the list recursively.*/
					files.addAll(getAllFilesInFolder(file));
				} else {
					files.add(file);
				}
			}
		} else throw new IllegalArgumentException("\"" + folder.getName() + "\" is not a folder!");
		
		/**Returns the completed list.*/
		return files;
	}
	
	/**Finds all of the files within all of the sub-folders of a given folder, with the given extension. <br>
	 * Recursively applies itself to each sub-folder found and adds the result to the main list.*/
	public static ArrayList<File> getAllFilesInFolderWithExtension(File folder, String extension) {
		/**Stores the files in the folder.*/
		ArrayList<File> files = new ArrayList<File>();
		
		/**Checks if the file is a folder before beginning.*/
		if(folder.isDirectory()) {
			/**Iterates over each file in the current directory.*/
			for(File file : folder.listFiles()) {
				/**Checks if the file is a directory.*/
				if(file.isDirectory()) {
					/**Adds the files within to the list recursively.*/
					files.addAll(getAllFilesInFolderWithExtension(file, extension));
				} else if(file.getName().endsWith("." + extension)) {
					files.add(file);
				}
			}
		} else throw new IllegalArgumentException("\"" + folder.getName() + "\" is not a folder!");
		
		/**Returns the completed list.*/
		return files;
	}
	
	/**Finds all of the sub-folders within a given folder. <br>
	 * Recursively applies itself to each sub-folder found and adds the result to the main list.*/
	public static ArrayList<File> getAllFoldersInFolder(File folder) {
		/**Stores the folders in the folder.*/
		ArrayList<File> folders = new ArrayList<File>();
		
		/**Checks if the file is a folder before beginning.*/
		if(folder.isDirectory()) {
			/**Adds the given folder to the list.*/
			folders.add(folder);
			
			/**Iterates over each file in the current directory.*/
			for(File file : folder.listFiles()) {
				/**Checks if the file is a directory.*/
				if(file.isDirectory()) {
					/**Adds the folder to the list.*/
					folders.add(file);
					/**Adds the folders within to the list recursively.*/
					folders.addAll(getAllFoldersInFolder(file));
				}
			}
		} else throw new IllegalArgumentException("\"" + folder.getName() + "\" is not a folder!");
		
		/**Returns the completed list.*/
		return folders;
	}

	/**Default serialisation ID, unimportant.*/
	private static final long serialVersionUID = 1L;
	/**Stores the contents of the file being represented as a string.
	 * Initially initialised as a blank string, signifying an empty file.*/
	private String contents = "";

	/**Constructor for the FileHandler class, takes in the location of the file being represented.*/
	public FileHandler(String location) {
		/**Accesses the super constructor from the parent class.*/
		/**Creates the underlying file representation object upon which this class is abstracted from.*/
		super(location);
		/**Reads the contents of the file, as a single string, into the contents field.*/
		/**First, checks if the file exists, if so, then reads the file.*/
		if(this.exists()) {
			contents = read();
		}
	}

	/**Reads the contents of the file and returns the result as a string.*/
	public String read() {
		/**Stores the contents of the file as a string.*/
		/**Initially initialises the contents as a blank string.*/
		String contents = "";

		/**Given the possibility of an error whilst reading the file, an exception is thrown to handle this.*/
		try {
			/**Creates a FileReader object to read from the file.*/
			/**The FileReader is wrapped within a BufferedReader object.*/
			/**This object takes the data read in by the FileReader and buffers it into chunks.*/
			/**This approach to file reading is less resource-intensive for larger files.*/
			/**The reader is instructed to read from this file.*/
			/**Hence a reference to this class in input as the parameter for the FileReader.*/
			BufferedReader reader = new BufferedReader(new FileReader(this));
			/**Loops through each of the lines within the file.*/
			/**Due to the default reading method being streaming each of the lines as a buffer, this process is simplified.*/
			/**Using a for-each loop one can iterate over each element in the buffer store as an array.*/
			for(Object line : reader.lines().toArray()) {
				/**Adds each character to the string.*/
				contents += (String) line + "\n";
			}

			/**Trims the contents string to remove the final, unnecessary line-break.*/
			contents = contents.trim();
			/**Closes the reader, signifying the reading process has been completed.*/
			/**This allows the program to free the utilised resources.*/
			reader.close();
		} catch (IOException e) {
			/**Given the occurrence of an exception, simply prints the error log.*/
			e.printStackTrace();
		}

		/**Returns the obtained contents of the file.*/
		return contents;
	}

	/**Writes to this file. <br>
	 * Overwrites the contents of the file with the specified parameters.*/
	public void write(String data) {
		/**Given the possibility of an error whilst writing to the file, an exception is thrown to handle this.*/
		try {
			/**Gets the parent folder of this file.*/
			File parent = getParentFile();
			/**First checks if the parent folder of the file exists.*/
			/**A new file cannot be created if the folder it is stored in doesn't exist.*/
			if(!parent.exists()) {
				/**If the parent folder doesn't exist, creates all the necessary directories up to and including itself.*/
				parent.mkdirs();
			}

			/**Creates a FileWriter to write the string to this file.*/
			/**To signify this, this instance of the class is passed in as the file to write in the parameters.*/
			BufferedWriter writer = new BufferedWriter(new FileWriter(this));
			/**Writes the string to the file.*/
			writer.write(data);
			/**Closes the writer, signifying the end of the writing process and freeing the used resources.*/
			/**This also subsequently applies the changes to the file made.*/
			writer.close();
		} catch (IOException e) {
			/**Given the occurrence of an exception, simply prints the error log.*/
			e.printStackTrace();
		}
	}

	/**Updates the contents of the file on the file system with the contents stored in this instance of the class.*/
	public void update() {
		/**Simply calls the write function with the contents field as the parameters.*/
		/**This overwrites the contents of the file on the system with the contents stored within this class.*/
		write(contents);
	}

	/**Gets the value of the contents field.*/
	public String getContents() {
		return this.contents;
	}

	/**Sets the value of the contents field.*/
	public void setContents(String contents) {
		this.contents = contents;
	}
}