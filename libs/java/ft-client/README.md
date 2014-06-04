File Transfer Client
=======

## Testing
For running the unit tests, one has to install a server accordingly, based upon the indications given below.
It has to exist a certain structure of directories and files on these servers, namely:
-- dir-with-rights
	-- tmp
	-- image-with-rights.gif
	-- test.txt
The server home folder has to be within the home folder of the user running the tests, as otherwise some needed cleaning operations would not be executed.
The server home folder is declared in "src/test/java/ro/kuberam/libs/java/ftclient/connection.properties".

### FTP unit tests
In order to run the FTP unit tests, one has to install a FTP server at the address 127.0.0.1, having the credentials stated in
"src/test/java/ro/kuberam/libs/java/ftclient/connection.properties".
In case when the server used is vsftpd, the needed configuration file is at "src/test/resources/ro/kuberam/libs/java/ftclient/vsftpd.conf".

### SFTP unit tests
In order to run the SFTP unit tests, one has to install a SFTP server at the address 127.0.0.1, having the credentials stated in
"src/test/java/ro/kuberam/libs/java/ftclient/connection.properties".
In case when the server used is openssh, the needed configuration file is at "src/test/resources/ro/kuberam/libs/java/ftclient/ssdh_config".

The configuration provided by this library already has a pair of keys to be used. The private key is used by default by the unit tests. while the other
one, located at "src/test/resources/ro/kuberam/libs/java/ftclient/sftp-public.key", has to be copied to the file that is stated in the server's configuration
file, under the AuthorizedKeysFile instruction. 