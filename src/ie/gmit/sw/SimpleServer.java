package ie.gmit.sw;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import ie.gmit.ctrl.Consumer;

/**
 * The class SimpleServer is use to manage client connection
 */
public class SimpleServer {

	private static ServerSocket serverSocket;
	private Socket socket;
	private BlockingQueue<String> sharedQueue;
	public static File file;

	/**
	 * The method runServer() is use to run server on specified port
	 * 
	 * @param portNumber
	 * @throws IOException
	 */
	public void runServer(Integer portNumber) throws IOException {
		
		// Open the server socket
		serverSocket = new ServerSocket(portNumber);
		// Initialize blocking queue
		sharedQueue = new LinkedBlockingQueue<String>();

		// Start one consumer thread
		new Thread(new Consumer(sharedQueue)).start();

		while (true) {
			// Waiting for client connection
			socket = serverSocket.accept();
			new Thread(new HandleClient(socket, sharedQueue)).start();
		}

	}

	/**
	 * The method main() is the starting point of server. It parse the args and
	 * extract portnumber and directory path
	 * 
	 * @param args
	 *            having portnumber and directory path
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException {

		// Check arguments
		if (args == null || args.length == 0) {
			System.out.println("Error : argument must contains port number and directory path");
			return;
		}

		// Check port number
		Integer portNumber = 0;

		try {
			portNumber = Integer.parseInt(args[0]);
		} catch (Exception e) {
			System.out.println("Port number should be a number");
			return;
		}

		// Check directory
		file = new File(args[1]);

		if (!file.exists()) {
			System.out.println("Directory doesnot exists");
			return;
		}

		if (!file.isDirectory()) {
			System.out.println("Second argument should be a directory");
			return;
		}

		new SimpleServer().runServer(portNumber);
	}
}
