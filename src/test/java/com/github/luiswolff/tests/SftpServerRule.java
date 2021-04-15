package com.github.luiswolff.tests;

import java.io.IOException;
import java.util.Arrays;

import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;
import org.junit.rules.TemporaryFolder;

public class SftpServerRule extends TemporaryFolder {

	static final String USERNAME = "foo";
	static final String PASSWORD = "pass";
	private SshServer sftpServer;

	@Override
	protected void before() throws Throwable {
		super.before();

		sftpServer = SshServer.setUpDefaultServer();
		sftpServer.setHost("127.0.0.1");
		sftpServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
		sftpServer.setSubsystemFactories(Arrays.asList(new SftpSubsystemFactory()));
		sftpServer.setFileSystemFactory(new VirtualFileSystemFactory(getRoot().toPath()));
		sftpServer.setPasswordAuthenticator(this::authenticate);

		sftpServer.start();
	}

	private boolean authenticate(String username, String password, ServerSession session) {
		return USERNAME.equals(username) && PASSWORD.equals(password);
	}
	
	int getPort() {
		return sftpServer.getPort();
	}

	@Override
	protected void after() {
		super.after();

		if (sftpServer != null) {
			try {
				sftpServer.stop();
			} catch (IOException e) {
				// do nothing;
			}
		}
	}

}
