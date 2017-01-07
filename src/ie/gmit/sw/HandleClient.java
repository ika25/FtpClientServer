package ie.gmit.sw;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import ie.gmit.model.Connection;
import ie.gmit.util.Constants;

/**
 * The class HandleClient is use to run a new thread for each user. It handles
 * and entertains all the request for each user. This class also provide the
 * role of Producer which put the log request into the blocking queue
 */
public class HandleClient extends Connection implements Runnable {

	private final BlockingQueue<String> sharedQueue;
	private final String clientIpAddress;

	/**
	 * The constructor HandleClient() is use to initialize input and output
	 * Streams
	 * 
	 * @param socket
	 * @throws IOException
	 */
	public HandleClient(Socket socket, BlockingQueue<String> sharedQueue) throws IOException {
		this.socket = socket;
		this.out = new ObjectOutputStream(socket.getOutputStream());
		this.in = new ObjectInputStream(socket.getInputStream());
		this.sharedQueue = sharedQueue;
		this.clientIpAddress = socket.getRemoteSocketAddress().toString();
	}

	@Override
	public void run() {

		// Handle Client Requests
		try {
			while (true) {

				String operation = (String) this.in.readObject();

				if (Constants.COMMAND_LIST.equals(operation)) {

					String[] filesAndDirectories = SimpleServer.file.list();

					List<String> fileAndDirectoryNames = new ArrayList<>();

					String operationStatus = Constants.OPERATION_WARNING;

					if (filesAndDirectories != null) {
						for (String fileOrDirectoryName : filesAndDirectories) {
							fileAndDirectoryNames.add(fileOrDirectoryName);
						}

						operationStatus = Constants.OPERATION_INFO;
					}

					// Put request to blocking queue
					sharedQueue.put("[" + operationStatus + "] Listing requested by " + this.clientIpAddress + " at "
							+ DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalTime.now()) + " on "
							+ DateTimeFormatter.ofPattern("yyy/MM/dd").format(LocalDate.now()));

					out.writeObject(fileAndDirectoryNames);

				} else if (Constants.COMMAND_DOWNLOAD.equals(operation)) {

					String fileName = (String) in.readObject();

					// Start file transfer to client
					Boolean isFileTransfered = this.transferFile(SimpleServer.file.getAbsolutePath() + "\\" + fileName);

					// Put request to blocking queue
					sharedQueue.put("["
							+ ((Boolean.TRUE.equals(isFileTransfered)) ? Constants.OPERATION_INFO
									: Constants.OPERATION_ERROR)
							+ "] " + "Download " + fileName + " requested by " + this.clientIpAddress + " at "
							+ DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalTime.now()) + " on "
							+ DateTimeFormatter.ofPattern("yyy/MM/dd").format(LocalDate.now()));

				} else {
					out.writeObject("Operation not supported");
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The method transferFile() is use to transfer the file to the client. It
	 * first checks if file exists or not, if exists then it will transfer the
	 * file
	 * 
	 * @param fileName
	 *            name of the file to be transfered
	 * @return true if file transfered successfully else false
	 * @throws Exception
	 */
	private Boolean transferFile(String fileName) throws Exception {

		File file = new File(fileName);

		// Check if file exists
		if (!file.exists() || file.isDirectory()) {
			out.writeObject("File doesnot exists");
			return Boolean.FALSE;
		}

		out.writeObject(Constants.FILE_EXISTS);

		out.writeObject(new Integer((int) file.length()));

		FileInputStream fileInputStream = null;
		BufferedInputStream bufferedInputStream = null;
		OutputStream outputStream = null;
		try {
			// send file
			byte[] mybytearray = new byte[(int) file.length()];
			fileInputStream = new FileInputStream(file);
			bufferedInputStream = new BufferedInputStream(fileInputStream);
			bufferedInputStream.read(mybytearray, Constants.INITIAL_START_POINT, mybytearray.length);
			outputStream = socket.getOutputStream();
			System.out.println("The file is sending " + fileName + "(" + mybytearray.length + " bytes)");
			outputStream.write(mybytearray, Constants.INITIAL_START_POINT, mybytearray.length);
			outputStream.flush();
			System.out.println("Sent");

		} catch (Exception e) {
			e.printStackTrace();
			return Boolean.FALSE;
		}

		return Boolean.TRUE;

	}
}
