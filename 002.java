package src.ding.show;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

class RunWith {
	public static void main(String[] args) throws IOException {
		try {
			String sstr = args[0];
			String eqs = "--mc";
			String wd = "";
			wd += "Now start output the arguments.\n";
			for(int i=0;i<args.length;i++) {
				wd += "The " + i + " of the string is:" + args[i] + "\n";
			}
			wd += "End output.\n";
			if (args[0].equals(eqs)) {
				sstr = args[1];
				wd += "MC Crack mode used." + "\n";
				int lgh = args.length - 1;
				int ne = 2;
				for(int c=2;c<lgh;c++) {
					if(args[c].equals("--username")) {
						ne++;
						break;
					}
					ne++;
				}
				for(int a=2;a<ne;a++) {
					sstr += " " + args[a];
			 	}
			 	String nme;
			 	try {
			 		FileReader fr = new FileReader("Username.txt");
			 		BufferedReader br = new BufferedReader(fr);
			 		nme = br.readLine();
			 		for(int n=0;n<2;n++) {
			 			nme = br.readLine();
			 		}
			 		wd += "Username has been set to " + nme + "\n";
			 		br.close();
			 	} 
			 	catch(FileNotFoundException e) {
			 		wd += "Username.txt not found. Creating new file...\nChange your MC's username in it." + "\n";
			 		BufferedWriter bw = new BufferedWriter(new FileWriter("Username.txt"));
			 		bw.write("#This file is used to crack MC's Username.");
			 		bw.newLine();
			 		bw.write("#By changing the name next line ,you change your name.");
			 		bw.newLine();
			 		bw.write("Player");
			 		bw.close();
			 		nme = "Player";
			 		System.exit(1);
			 	}
			 	sstr += " " + nme;
			 	for(int a=(++ne);a<lgh;a++) {
					sstr += " " + args[a];
			 	}
			} else {				
				
				for(int p=1;p<args.length;p++) {
					sstr += " " + args[p];
			 	}
			}
			wd += "Now execute " + sstr + "\n";
			AppOutputCapture run = new AppOutputCapture();
			run.Exec(sstr,wd);
		}
		catch (ArrayIndexOutOfBoundsException e){
			JOptionPane.showMessageDialog(null,"No arguments found.\nUsage:RunWin.jar program args...");
			System.exit(1);
		}	
	}
}

class AppOutputCapture {
	private static Process process;
	StringBuffer sb = new StringBuffer();
	JTextArea cta = new JTextArea();

	public void Exec(String stat,String wo) throws IOException {
		try{
		// Process the new program
		process = Runtime.getRuntime().exec(stat);
		}
		catch(IOException e) {
		JOptionPane.showMessageDialog(null,"Error while creating new process...\n" + e);
		System.exit(1);
		}
		// Get Stream the progran written in
		InputStream[] inStreams = new InputStream[] {process.getInputStream(),process.getErrorStream()};
		JScrollPane scrollPane = new JScrollPane(cta);
		cta.setEditable(false);
		cta.setFont(java.awt.Font.decode("monospaced"));
		JFrame frame = new JFrame("Console output");		
		frame.getContentPane().add(scrollPane,"Center"); //frame.getContentPane()
		frame.setSize(900,580);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		defl();
		Read(inStreams,wo);
		frame.addWindowListener(new WindowAdapter() {	
			public void windowClosing(WindowEvent evt) {
				int extvl = 0;
				process.destroy();
				try {
					extvl = process.waitFor(); // May be halted on under Win98
				}
				catch(InterruptedException e) {}
				JOptionPane.showMessageDialog(null,"Program exited with exitcode " + extvl);
				System.exit(extvl);
			}
		});
	}
	
	public void Read(InputStream[] inStreams,String wr) {
		showStart(wr);
		for(int i = 0; i < inStreams.length; ++i) {
			startConsoleReaderThread(inStreams[i]);
		}
	} // ConsoleTextArea()
	
	public void defl() throws IOException {
		final LoopedStreams ls = new LoopedStreams();
		//relocale System.out and stem.err
		PrintStream ps = new PrintStream(ls.getOutputStream());
		System.setOut(ps);
		System.setErr(ps);
		startConsoleReaderThread(ls.getInputStream());
	} // ConsoleTextArea()

	private void startConsoleReaderThread(InputStream inStream) {
		final BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
		new Thread(new Runnable() {
			public void run() {
				try {
					String s;
					while((s = br.readLine()) != null) {
						sb.setLength(0);
						cta.append(sb.append(s).append('\n').toString());
						cta.setCaretPosition(cta.getDocument().getLength());
					}
				}
				catch(IOException e) {
					JOptionPane.showMessageDialog(null,"Read Errormessage from BufferedReader" + e);
					System.exit(1);
				}
			}
		}).start();
	} // start ConsoleReaderThread()
	
	public void showStart(String stt) {
		sb.setLength(0);
		cta.append(sb.append("Show argument and run(V1.0),window based.All rights resvered.").append('\n').append('\n').toString());
		sb.setLength(0);
		cta.append(sb.append(stt).append('\n').toString());
	}
}

class LoopedStreams {
	private PipedOutputStream pipedOS = new PipedOutputStream();
	private boolean keepRunning = true;
	private ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream() {
		public void close() {
			keepRunning = false;
			try {
				super.close();
				pipedOS.close();
			}
			catch(IOException e) {
				System.out.println(e);
				System.exit(1);
			}
	 }
	};
	
	private PipedInputStream pipedIS = new PipedInputStream() {
		public void close() {
			keepRunning = false;
			try {
				super.close();
			}
			catch(IOException e) {
		  	System.out.println(e);
				System.exit(1);
			}
		}
	};
	
	public LoopedStreams() throws IOException {
		pipedOS.connect(pipedIS);
		startByteArrayReaderThread();
	} // LoopedStreams()
	
	public InputStream getInputStream() {
		return pipedIS;
	} // getInputStream()
	
	public OutputStream getOutputStream() {
		return byteArrayOS;
	} // getOutputStream()
	
	public void startByteArrayReaderThread() {
		new Thread(new Runnable() {
			public void run() {
				while(keepRunning) {
					// Check bytes in it
					if(byteArrayOS.size() > 0) {
						byte[] buffer = null;
						synchronized(byteArrayOS) {
							buffer = byteArrayOS.toByteArray();
							byteArrayOS.reset(); // Clear buffer
						}
						try {
							// Send the info. to PipedOutputStream
							pipedOS.write(buffer, 0, buffer.length);
						}
						catch(IOException e) {
							System.out.println(e);
							System.exit(1);
						}
					}
					else { // No info. can be used. Thread went into sleep
						try {
							// Check for info. per second in ByteArrayOutputStream
							Thread.sleep(1000);
						}
						catch(InterruptedException e) {}
					}
				}
			}
		}).start();
	} // startByteArrayReaderThread()
}   // LoopedStreams