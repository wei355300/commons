package com.github.sunnysuperman.commons.utils;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SSLContextFactory {
	private String protocol;
	private String keyAlgorithm;
	private String trustAlgorithm;

	public SSLContextFactory() {
		this(null, null, null);
	}

	public SSLContextFactory(String protocol, String keyAlgorithm, String trustAlgorithm) {
		super();
		this.protocol = protocol == null ? "TLSv1.2" : protocol;
		this.keyAlgorithm = keyAlgorithm == null ? "SunX509" : keyAlgorithm;
		this.trustAlgorithm = trustAlgorithm == null ? this.keyAlgorithm : trustAlgorithm;
	}

	public SSLContext getSSLContext(InputStream keystore, String keystorePassword, String keyPassword,
			InputStream truststore, String truststorePassword) throws Exception {
		SSLContext context = SSLContext.getInstance(protocol);
		context.init(getKeyManagers(keystore, keystorePassword, keyPassword),
				getTrustManagers(truststore, truststorePassword), null);
		return context;
	}

	public KeyManager[] getKeyManagers(InputStream keystore, String keystorePassword, String keyPassword)
			throws Exception {
		KeyManager[] keyManagers = null;
		if (keystore != null) {
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(keystore, keystorePassword.toCharArray());
			keystore.close();
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(keyAlgorithm);
			kmf.init(ks, (keyPassword != null ? keyPassword : keystorePassword).toCharArray());
			keyManagers = kmf.getKeyManagers();
		}
		return keyManagers;
	}

	public TrustManager[] getTrustManagers(InputStream truststore, String truststorePassword) throws Exception {
		TrustManager[] trustManagers = null;
		if (truststore != null) {
			KeyStore tks = KeyStore.getInstance("JKS");
			tks.load(truststore, truststorePassword.toCharArray());
			truststore.close();
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(trustAlgorithm);
			tmf.init(tks);
			trustManagers = tmf.getTrustManagers();
		} else {
			trustManagers = getTrustAllManagers();
		}
		return trustManagers;
	}

	public static TrustManager[] getTrustAllManagers() {
		return new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		} };
	}
}
