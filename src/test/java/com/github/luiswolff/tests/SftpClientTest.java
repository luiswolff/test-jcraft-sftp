package com.github.luiswolff.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Rule;
import org.junit.Test;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class SftpClientTest {

	@Rule
	public SftpServerRule sftpServerRule = new SftpServerRule();

	@Test
	public void testUpload() throws JSchException, SftpException, IOException {
		String fileContent = "Test - " + System.currentTimeMillis();
		String fileName = "jschFile.txt";
		
		try (JSchSftpClient upload = new JSchSftpClient(null, "localhost", sftpServerRule.getPort(),
				SftpServerRule.USERNAME, SftpServerRule.PASSWORD)) {
			upload.put(new ByteArrayInputStream(fileContent.getBytes()), fileName);
		}
		
		File uploadedFile = new File(sftpServerRule.getRoot(), fileName);
		assertTrue("Couldn't find uploaded file.", uploadedFile.exists());
		assertEquals("File content don't match", fileContent, new String(Files.readAllBytes(uploadedFile.toPath())));
	}

}
