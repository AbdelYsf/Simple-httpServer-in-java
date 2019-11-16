package Abdel.ysf.coreJava;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;

public class HttpServer implements Runnable {
	static final int LESTENING_PORT = 7970;

	static final String INDEX_FILE = "index.html";
	static final String NOT_FOUND_ERROR_FILE = "404.html";
	static final File ROOT_PATH = new File(".");

	private Socket connection;

	public HttpServer(Socket socket) {

		this.connection = socket;
	}

	@Override
	public void run() {
		BufferedReader bf = null;
		PrintWriter pw = null;
		BufferedOutputStream bos = null;
		String requestedFile = null;

		try {
			// read characters from the client via input stream on the socket
			bf = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			// character output stream (for headers)
			pw = new PrintWriter(connection.getOutputStream());
			// binary output stream (for requested data)
			bos = new BufferedOutputStream(connection.getOutputStream());
			
			String input =bf.readLine();
			System.out.println(input);
			
			StringTokenizer st = new StringTokenizer(input);
			// getting the http method
			String httpMethod = st.nextToken().toUpperCase(); 
			// getting the requested File
			requestedFile = st.nextToken().toLowerCase();
			
			// only GET and HEAD methods
			if(httpMethod.equals("GET") ) {
				
				if(requestedFile.endsWith("/")) {
					requestedFile = requestedFile + INDEX_FILE;
				}
				
				File file = new File(ROOT_PATH, requestedFile);
				int fileLength = (int)file.length();
				String mimecontentType =  getContentType(requestedFile);
				
				//
				byte[] requestedData = readDataFromFile(file, fileLength)	;
				// set response header
				
				
				pw.println("Server: Simple Java HTTP Server by Abdel: 1.0");
				pw.println("Date: " + new Date());
				pw.println("Content-type: " + mimecontentType);
				pw.println("Content-length: " + fileLength);
				pw.println(); // blank line between headers and content.. very important 
				pw.flush(); // flush character output stream buffer
				
				// file
				bos.write(requestedData, 0, fileLength);
				bos.flush();
				
				}else {
					
					/// other methods are not supported yet
					/// to be implemented in the future incha'allah ...
				}
			
			}catch(FileNotFoundException e) {
				try {
					fileNotFoundHandler(pw, bos, requestedFile);
				} catch (IOException ee) {
					System.err.println("Error with file not found exception : " + ee.getMessage());
				}
			
			} catch (Exception e) {
				System.err.println("Server error : " + e.getMessage());
				e.printStackTrace();
				
			}finally {
				
				try {
					bf.close();
					pw.close();
					bos.close();
					connection.close(); //closing the socket connection
				} catch (Exception e) {
					System.err.println("Error while closing the streams : " + e.getMessage());
				} 
			}

		}
	
	
	
	private String getContentType(String requestedFile) {
		if ( requestedFile.endsWith(".html"))
			return "text/html";
		else
			return "text/plain";
	}
	
	private byte[] readDataFromFile(File file, int length) throws IOException {
		
		
		byte[] data = new byte[length]; 
		try(FileInputStream fis= new FileInputStream(file)) {
			
		fis.read(data);
		}
		
		return data;
		
	}
	private void fileNotFoundHandler(PrintWriter pw, BufferedOutputStream bos, String requestedFile) throws IOException {
		File file = new File(ROOT_PATH, NOT_FOUND_ERROR_FILE);
		int fileLength = (int) file.length();
		String content = "text/html";
		byte[] fileData = readDataFromFile(file, fileLength);
		
		pw.println("HTTP/1.1 404 File Not Found");
		pw.println("Server: Simple Java HTTP Server by Abdel: 1.0");
		pw.println("Date: " + new Date());
		pw.println("Content-type: " + content);
		pw.println("Content-length: " + fileLength);
		pw.println(); // blank line between headers and content, very important !
		pw.flush(); // flush character output stream buffer
		
		bos.write(fileData, 0, fileLength);
		bos.flush();
		
		
	}
}
