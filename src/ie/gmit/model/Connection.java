package ie.gmit.model;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * The Connection class holds the connection data of client and server
 */
public class Connection {

	protected Socket socket;
	protected ObjectOutputStream out;
	protected ObjectInputStream in;

}
