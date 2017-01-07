package ie.gmit.sw;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import ie.gmit.model.Connection;
import ie.gmit.util.Constants;
import ie.gmit.util.ParseUtil;

/**
 * The class SimpleClient is use to connect to server, fetch config file's
 * information and let the user to interact server by providing a menu
 */
public class SimpleClient extends Connection {

	private Scanner scanner;
	private File file;

	/**
	 * The method start() is use to start the make connection to server, get
	 * input from user and communicate with server
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception {
		// Parse XML
		Map<String, String> configMap = new HashMap<String, String>();

		try {
			configMap = ParseUtil.parseXML(new File("config.xml"));
		} catch (Exception e) {
			System.out.println("Unable to parse xml file");
			return;
		}

		// Check directory
		file = new File(configMap.get("download-dir"));

		if (!file.exists()) {
			file.mkdir();
		}

		if (Boolean.FALSE.equals(file.isDirectory())) {
			System.out.println("Download directory should be a directory");
			return;
		}

		scanner = new Scanner(System.in);

		while (true) {

			printMenu();

			String input = scanner.nextLine();

			if (Constants.COMMAND_CONNECT.equals(input)) {

				if (in == null || out == null) {
					try {
						socket = new Socket(configMap.get("server-host"),
								Integer.parseInt(configMap.get("server-port")));
						out = new ObjectOutputStream(socket.getOutputStream());
						in = new ObjectInputStream(socket.getInputStream());
					} catch (Exception e) {
						System.out.println("Unable to connect to server. Check ip address and port number");
					}
				} else {
					System.out.println("Already connected");
				}

			} else if (Constants.COMMAND_LIST.equals(input)) {

				if (in == null || out == null) {
					System.out.println("Not connected to server");
					continue;
				}

				out.writeObject(input);

				// list of files and directory
				List<String> files = (List<String>) in.readObject();

				for (String fileName : files) {
					System.out.println(fileName);
				}

			} else if (Constants.COMMAND_DOWNLOAD.equals(input)) {

				if (in == null || out == null) {
					System.out.println("Not connected to server");
					continue;
				}

				try {
					downloadFile();
				} catch (Exception e) {
					System.out.println("An error occurred while downloading the file");
				}
			} else if (Constants.COMMAND_QUIT.equals(input)) {
				System.out.println("Thankyou");
				break;
			} else {
				System.out.println("Invalid Command");
			}

		}
	}

	/**
	 * The main() method is the starting point
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		try {
			new SimpleClient().start();
		} catch (Exception e) {
		}
	}

	/**
	 * The method printMenu() is use to print menu to the console
	 */
	private void printMenu() {
		System.out.println();
		System.out.println("1) Connect to Server");
		System.out.println("2) Print File Listing");
		System.out.println("3) Download File");
		System.out.println("4) Quit");
		System.out.println();
		System.out.print("Type Option [1-4] > ");
	}

	/**
	 * The method downloadFile() is use to ask user which file to download and
	 * it communicate with the server till download complete
	 * 
	 * @throws Exception
	 */
	private void downloadFile() throws Exception {

		System.out.print("Enter file name > ");

		String fileName = scanner.nextLine();

		// Tell server to ready for download
		out.writeObject(Constants.COMMAND_DOWNLOAD);

		// Ask server to start file transfer
		out.writeObject(fileName);

		String isFileExists = (String) in.readObject();
		
		if (!Constants.FILE_EXISTS.equals(isFileExists)) {
			System.out.println("Request file doesnot exists on server");
			return;
		}

		// get file size from the server
		Integer fileSize = (Integer) in.readObject();

		// Open byte stream to read files

		int current = 0;
		int bytesRead;
		BufferedOutputStream bufferedOutputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			System.out.println("Downloading the file ...");

			// receive file
			byte[] byteArray = new byte[fileSize];
			InputStream inputStream = socket.getInputStream();
			fileOutputStream = new FileOutputStream(file.getAbsolutePath() + "\\" + fileName);
			bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
			bytesRead = inputStream.read(byteArray, Constants.INITIAL_START_POINT, byteArray.length);
			current = bytesRead;

			// Read all the bytes from input stream
			do {
				bytesRead = inputStream.read(byteArray, current, (byteArray.length - current));
				if (bytesRead >= 0) {
					current += bytesRead;
				}
			} while (current < fileSize);

			bufferedOutputStream.write(byteArray, Constants.INITIAL_START_POINT, current);
			bufferedOutputStream.flush();
			System.out.println("The file " + fileName + " is downloaded (" + current + " bytes read)");
		} finally {
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
			if (bufferedOutputStream != null) {
				bufferedOutputStream.close();
			}
		}
	}
}
