package Network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import Interfaces.IKomunikaceServeru;
import Interfaces.ITCP;


public class TCPComm implements ITCP, Runnable {

	/** Globalni promenne tridy **/
	private IKomunikaceServeru m_observer;
	private OutputStream m_output;
	private BufferedReader input;
	private String address;
	private int port;
	private boolean isServer = true;
	private int counterTimeOUt = 0;
	private Thread netThread;
	private Socket socket;

	/**
	 * Inicializace promennych pro uchovani adresy a portu
	 * 
	 * @param address
	 * @param port
	 * @param mMR
	 */
	public TCPComm(String address, int port) {

		this.address = address;
		this.port = port;

	}

	// ---------------------------------------------------------
	@Override
	public void send(String data) {
		assert (data != null);
		assert (m_output != null);
		try {
			m_output.write(data.getBytes());
		} catch (IOException e) {
			endConection();
		//	System.err.println("Caught IOException: " + e.getMessage());
		}
	}

	// ---------------------------------------------------------

	@Override
	public void registerObserver(IKomunikaceServeru observer) {
		m_observer = observer;
	}

	// ---------------------------------------------------------

	public void run() {

		socket = new Socket();
		
		try {
			socket.connect(new InetSocketAddress(address, port));
//			System.out.println("Spojeni");

			socket.setSoTimeout(30000);

			m_output = socket.getOutputStream();
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			m_observer.processData("Connect,");
			for (;;) {
				try {

					String data = new String();
					if ((data = input.readLine()) != null) {
						if (m_observer != null) {
							m_observer.processData(data);
							counterTimeOUt = 0;
						}
					} else {
						m_observer.processData("NoServer");
						endConection();
						break;
					}

				} catch (SocketTimeoutException e) {

					send("CheckConnect,\n");
				//	System.err.println("Caught TimeoutException: " + counterTimeOUt + " " + e.getMessage());

					if (counterTimeOUt > 0) {
						m_observer.processData("NoServer");
						endConection();
						break;
					}

					counterTimeOUt++;
				}

			}

		} catch (IOException e) {

			m_observer.processData("NoServer");
			System.err.println("Caught IOException: " + e.getMessage());
			endThread();
		}

	}

	// ---------------------------------------------------------

	public void endConection() {
		try {

			System.out.println("Ukonceni spojeni.");
			socket.close();
			m_output.close();
			input.close();
			endThread();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void start() {

		netThread = new Thread(this);
		netThread.start();

	}

	public void endThread() {
		try {
			netThread.join(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** Getrs and Setrs **/
	public boolean isServer() {
		return isServer;
	}

	public void setServer(boolean isServer) {
		this.isServer = isServer;
	}

	public int getCounterTimeOUt() {
		return counterTimeOUt;
	}

	public void setCounterTimeOUt(int counterTimeOUt) {
		this.counterTimeOUt = counterTimeOUt;
	}

}
