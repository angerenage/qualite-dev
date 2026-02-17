package org.univ_paris8.iut.montreuil.arollet.qualite_dev.services.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.exceptions.ApiException;

import jakarta.ws.rs.core.Response;

public class PasswordHasher {
	private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
	private static final String PREFIX = "pbkdf2_sha256";
	private static final int ITERATIONS = 65536;
	private static final int KEY_LENGTH = 256;
	private static final int SALT_LENGTH = 16;
	private static final SecureRandom RANDOM = new SecureRandom();

	public String hash(String plainPassword) {
		if (plainPassword == null || plainPassword.trim().isEmpty()) {
			throw new ApiException(Response.Status.BAD_REQUEST, "password is required.");
		}

		byte[] salt = new byte[SALT_LENGTH];
		RANDOM.nextBytes(salt);
		byte[] hash = pbkdf2(plainPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
		return PREFIX + "$" + ITERATIONS + "$" + encodeBase64(salt) + "$" + encodeBase64(hash);
	}

	public boolean matches(String plainPassword, String storedHash) {
		if (plainPassword == null || storedHash == null || storedHash.trim().isEmpty()) {
			return false;
		}

		if (!isHashedFormat(storedHash)) {
			return false;
		}

		String[] chunks = storedHash.split("\\$");
		if (chunks.length != 4) {
			return false;
		}

		int iterations;
		try {
			iterations = Integer.parseInt(chunks[1]);
		} catch (NumberFormatException ex) {
			return false;
		}

		byte[] salt;
		byte[] expectedHash;
		try {
			salt = Base64.getDecoder().decode(chunks[2]);
			expectedHash = Base64.getDecoder().decode(chunks[3]);
		} catch (IllegalArgumentException ex) {
			return false;
		}

		byte[] computedHash = pbkdf2(plainPassword.toCharArray(), salt, iterations, expectedHash.length * 8);
		return MessageDigest.isEqual(expectedHash, computedHash);
	}

	public boolean isHashedFormat(String storedHash) {
		return storedHash != null && storedHash.startsWith(PREFIX + "$");
	}

	private byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength) {
		try {
			PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
			SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM);
			return secretKeyFactory.generateSecret(spec).getEncoded();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
			throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, "Password hashing subsystem is unavailable.");
		}
	}

	private String encodeBase64(byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}
}
