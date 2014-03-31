package ro.kuberam.libs.java.crypto.junit;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.DigestOutputStream;
import java.security.MessageDigest;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import ro.kuberam.tests.junit.BaseTest;

public class CryptoModuleTests extends BaseTest {
	
	@Test
	public void test01() throws Exception {
		InputStream document = getClass().getResourceAsStream(
				"../resources/doc-1.xml");
		InputStream digitalCertificate = getClass().getResourceAsStream(
				"../resources/digital-certificate.xml");

		System.out.println(IOUtils.toString(digitalCertificate));
	}

	@Test
	public void pipedStreams1Test() throws Exception {
		final String message = "String for tests.";

		PipedInputStream in = new PipedInputStream();
		final PipedOutputStream outp = new PipedOutputStream(in);
		new Thread(new Runnable() {
			public void run() {
				try {
					outp.write(message.getBytes("UTF-8"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

		System.out.println("result: " + in);
	}

	@Test
	public void pipedStreams2Test() throws Exception {
		InputStream document = getClass().getResourceAsStream(
				"../resources/doc-1.xml");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int next = document.read();
		while (next > -1) {
			bos.write(next);
			next = document.read();
		}
		bos.flush();
		byte[] result = bos.toByteArray();

		PipedOutputStream poStream = new PipedOutputStream();
		PipedInputStream piStream = new PipedInputStream();

		// piped input stream connect to the piped output stream
		piStream.connect(poStream);

		// Writes specified byte array.
		poStream.write(result);

		// Reads the next byte of data from this piped input stream.
		for (int i = 0; i < result.length; i++) {
			System.out.println(piStream.read());
		}

		// Closes piped input stream
		poStream.close();

		// Closes piped output stream
		piStream.close();
	}

	@Test
	public void digestOutputStreamTest() throws Exception {
		try {
			FileOutputStream fos = new FileOutputStream("/home/claudius/workspace-claudius/expath-crypto/src/org/expath/crypto/tests/resources/string.txt");
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			DigestOutputStream dos = new DigestOutputStream(fos, md);
			ObjectOutputStream oos = new ObjectOutputStream(dos);
			String data = "This have I thought good to deliver thee, "+
				"that thou mightst not lose the dues of rejoicing " +
				"by being ignorant of what greatness is promised thee.";
			oos.writeObject(data);
			dos.on(false);
			byte[] digest = md.digest();
			oos.writeObject(digest);
			int digestLength = digest.length;
			System.out.println("length: " + digestLength);
		    BigInteger bi = new BigInteger(1, digest);
		    String result = bi.toString(digestLength);
		    if (result.length() % 2 != 0) {
		    	result = "0" + result;
		    }

			System.out.println("result: " + result);
		} catch (Exception e) {
			System.out.println(e);
		}
	}	public InputStream openStream() throws IOException {
		final PipedOutputStream out = new PipedOutputStream();
		PipedInputStream in = new PipedInputStream(out);

		Runnable exporter = new Runnable() {
			public void run() {
				try {
					out.write("message".getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				IOUtils.closeQuietly(out);
			}
		};

		// executor.submit(exporter);

		return in;
	}

	public static void main(String[] args) throws Exception {

	}

}
