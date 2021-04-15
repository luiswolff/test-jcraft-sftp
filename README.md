# Test JCraft JSch (SFTP Client)

This project show how to use the Java library JSch in order to upload data to an SFTP Server.
It also includes a sample JUnit test.

## Usage

There are tow ways to use this project.

 1. With a local SFTP Server the project can upload the `sample.txt` file.
 2. With the provided JUnit test a temporary SFTP-Server will be started.

### Local SFTP Server

With the default java launch mechanism this project can connect it self to a local SFTP server.
The server has to be fulfill the following criteria:

 * Available under the port 22
 * has a user **foo** that is identified by the password **pass**
 * provides a directory **upload** relative to the default directory

If you have Docker installed you can setup the required SFTP server with the following command:

``` 
$ docker run -p 22:22 -d atomz/sftp foo:pass:::upload
```

To run the program just do the following command:

```
$ mvn compile exec:java -Dexec.mainClass=com.github.luiswolff.tests.JSchSftpClient
```

After that you can see a file `upload/jschFile.txt` on the SFTP server with the content of the local file `sample.txt`.

### JUnit

You can run the tests with the following command.

```
$ mvn test
```

The program will start a local SFTP server, the stores data in the users temporary directory.
Then a test file using the JSch library will be uploaded.
After that the test checks, that the expected file was created and it has the expected content.
All temporary data will be cleaned up after the tests shuts down.

## Dependencies

The projects uses the following Maven dependencies:

 * **com.jcraft:jsch:0.1.55 - compile**: A Java library for connecting to a SFTP server.
 * **org.apache.sshd:sshd-core:2.6.0 - test**: A Java library for providing a SSH server
 * **org.apache.sshd:sshd-sftp:2.6.0 - test**: A extension to SSHD-Core library to add a FTP subsystem to the SSH server.
 * **org.slf4j:slf4j-simple:1.7.30 - test**: Implementation for the SLF4J API that came into the class path from SSHD-SFTP.
 * **junit:junit:4.13.1 - test**: Test driver Framework