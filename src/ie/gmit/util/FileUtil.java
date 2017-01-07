package ie.gmit.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The FileUtil class is use to write the text into the log file
 */
public class FileUtil {

	private static BufferedWriter bufferedWriter = null;
	private static FileWriter fileWriter = null;

	/**
	 * The method writeFile() is use to write text into the log.txt file
	 * 
	 * @param text
	 *            to be written in append mode
	 */
	public static synchronized void writeFile(String text) {

		if (bufferedWriter == null || fileWriter == null) {
			// Create or open new file
			try {
				fileWriter = new FileWriter("log.txt", true);
				bufferedWriter = new BufferedWriter(fileWriter);
			} catch (Exception e) {
			}
		}

		try {
			bufferedWriter.append(text);
			bufferedWriter.newLine();
		} catch (IOException e) {
			System.err.println("Unable to write into the file");
		} finally {
			try {
				if (bufferedWriter != null) {
					bufferedWriter.close();
					bufferedWriter = null;
				}

				if (fileWriter != null) {
					fileWriter.close();
					fileWriter = null;
				}
			} catch (IOException ex) {
			}
		}
	}

}
