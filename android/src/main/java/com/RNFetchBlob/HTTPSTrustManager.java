package com.RNFetchBlob;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by eyow on 2016/5/25.
 *
 * @添加HTTPS信任
 */
public class HTTPSTrustManager implements X509TrustManager {
    private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[] {};

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        // To change body of implemented methods use File | Settings | File
        // Templates.
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        // To change body of implemented methods use File | Settings | File
        // Templates.
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

    public static SSLSocketFactory allowAllSSLSocketFactory() {
        SSLSocketFactory sslSocketFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[] { new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return _AcceptedIssuers;
                }
            } }, new SecureRandom());
            sslSocketFactory = sc.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return sslSocketFactory;
    }

    public static SSLSocketFactory buildSSLSocketFactory(InputStream inputStream) {
        KeyStore keyStore = null;
        try {
            keyStore = buildKeyStore(inputStream);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = null;
        try {
            tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, tmf.getTrustManagers(), null);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return sslContext.getSocketFactory();
    }

    private static KeyStore buildKeyStore(InputStream inputStream)
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        Certificate cert = readCert(inputStream);
        keyStore.setCertificateEntry("ca", cert);
        return keyStore;
    }

    private static Certificate readCert(InputStream inputStream) {
        Certificate ca = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            ca = cf.generateCertificate(inputStream);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return ca;
    }
}