package com.github.luiswolff.tests;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class JSchSftpClient implements AutoCloseable {
	
	private static final String KNOWN_HOSTS_FILE = System.getProperty("user.home") + "\\.ssh\\known_hosts";
	private static final String REMOTE_HOST = "localhost";
	private static final String USERNAME = "foo";
	private static final String PASSWORD = "pass";
	
	private final Session session;
	private final ChannelSftp channelSftp;

	JSchSftpClient(String knownHostsFile, String remoteHost, int port, String username, String password) throws JSchException {
		JSch jsch = new JSch();
		if (knownHostsFile != null) {
			jsch.setKnownHosts(knownHostsFile);
		} 
		session = jsch.getSession(username, remoteHost, port);
		session.setPassword(password);
		if (knownHostsFile == null) {
			/*
			 * I think, this configuration would not be a good idea for a productive environment.
			 * One important concept of SSH a TSL is the the host of the server has to authenticate is self, too.
			 * If the client dosn't check finger prints he can't verify whether he is talking to the correct server.
			 * If host keys are checked the client can recognizes for example DNS attacks or wrong configuration.
			 */
			session.setConfig( "StrictHostKeyChecking", "no" );
		}
		session.connect();
		
		try {
			channelSftp = (ChannelSftp) session.openChannel("sftp");
			channelSftp.connect();
		} catch (JSchException e) {
			session.disconnect();
			throw e;
		}
	}

	void put(InputStream source, String dest) throws SftpException {
		channelSftp.put(source, dest);
	}

	@Override
	public void close() {
		if (channelSftp != null) {
			channelSftp.disconnect();
		}
		if (session != null) {
			session.disconnect();
		}
	}

	public static void main(String[] args) throws JSchException, SftpException, IOException {
		try (JSchSftpClient upload = new JSchSftpClient(KNOWN_HOSTS_FILE, REMOTE_HOST, 22, USERNAME, PASSWORD); InputStream source = new FileInputStream("sample.txt")) {
			upload.put(source, "upload/jschFile.txt");
		}
	}

}
