package org.ee.crypt;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hasher {
	private MessageDigest digest;
	private byte[] output;

	public Hasher(String algorithm) throws NoSuchAlgorithmException {
		digest = MessageDigest.getInstance(algorithm);
	}

	public Hasher digest() {
		output = digest.digest();
		return this;
	}

	public Hasher digest(byte[] input) {
		output = digest.digest(input);
		return this;
	}

	public Hasher reset() {
		digest.reset();
		output = null;
		return this;
	}

	public Hasher update(byte input) {
		digest.update(input);
		return this;
	}

	public Hasher update(byte[] input) {
		digest.update(input);
		return this;
	}

	public Hasher update(byte[] input, int offset, int len) {
		digest.update(input, offset, len);
		return this;
	}

	public Hasher update(ByteBuffer input) {
		digest.update(input);
		return this;
	}

	public byte[] getOutput() {
		return output;
	}

	public String toString(String charsetName) throws UnsupportedEncodingException {
		return new String(output, charsetName);
	}

	public String toString(int radix) {
		return new BigInteger(1, output).toString(radix);
	}
}
