package com.leon.datalink.core.utils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * SSLUtils
 *
 * @author Leon
 */
public class SSLUtils {

    public static SSLSocketFactory getSocketFactory(InputStream resourceAsStream) throws Exception {
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");

        Certificate caCert = certFactory.generateCertificate(resourceAsStream);
        X509Certificate x509CaCert = (X509Certificate) caCert;

        KeyStore caKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        caKeyStore.load(null, null);
        caKeyStore.setCertificateEntry("cacert", x509CaCert);

        TrustManagerFactory tmFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmFactory.init(caKeyStore);

        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null, tmFactory.getTrustManagers(), null);

        return sslContext.getSocketFactory();
    }

}
