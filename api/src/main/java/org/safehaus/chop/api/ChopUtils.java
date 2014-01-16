package org.safehaus.chop.api;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import static org.safehaus.chop.api.Constants.RUNNER_WAR;


/**
 * General useful utility methods used in the Chop System.
 */
public class ChopUtils {
    private static final Logger LOG = LoggerFactory.getLogger( ChopUtils.class );
    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();


    static {
        System.setProperty ( "javax.net.ssl.trustStore", "jssecacerts" );
    }


    /**
     * Calculates the testBase: the portion of the key or the path to the test's
     * runner.war but not including it. This usually has the 'tests'
     * container/folder in it followed by the shortened version UUID: for
     * example a project whose war is tests/70a4673b/runner.war will have
     * the testBase of tests/70a4673b/. The last '/' will always be included.
     *
     * @param project the project who's testBase to calculate
     * @return the testBase of the project
     * @throws NullPointerException if the project is null or it's loadKey property is null
     */
    public static String getTestBase( Project project ) {
        Preconditions.checkNotNull( project, "The project cannot be null." );
        return getTestBase( project.getLoadKey() );
    }


    /**
     * Calculates the testBase: the portion of the key or the path to the test's
     * runner.war but not including it. This usually has the 'tests'
     * container/folder in it followed by the shortened version UUID: for
     * example a project whose war is 'tests/70a4673b/runner.war' will have
     * the testBase of tests/70a4673b/. The last '/' will always be included.
     *
     * @param loadKey the loadKey of a project: i.e. 'tests/70a4673b/runner.war'
     * @return the testBase of the project
     * @throws NullPointerException if the loadKey is null
     */
    public static String getTestBase( String loadKey ) {
        Preconditions.checkNotNull( loadKey, "The loadKey argument cannot be null." );
        return loadKey.substring( 0, loadKey.length() - RUNNER_WAR.length() );
    }


    public static void installCert( String host, int port, char[] passphrase ) throws Exception {

        if ( passphrase == null ) {
            passphrase = "changeit".toCharArray();
        }

        File file = new File( "jssecacerts" );
        if ( !file.isFile() ) {
            char SEP = File.separatorChar;
            File dir = new File( System.getProperty( "java.home" ) + SEP + "lib" + SEP + "security" );
            file = new File( dir, "jssecacerts" );
            if ( !file.isFile() ) {
                file = new File( dir, "cacerts" );
            }
        }
        LOG.debug( "Loading KeyStore {}", file );
        InputStream in = new FileInputStream( file );
        KeyStore ks = KeyStore.getInstance( KeyStore.getDefaultType() );
        ks.load( in, passphrase );
        in.close();

        SSLContext context = SSLContext.getInstance( "TLS" );
        TrustManagerFactory tmf = TrustManagerFactory.getInstance( TrustManagerFactory.getDefaultAlgorithm() );
        tmf.init( ks );
        X509TrustManager defaultTrustManager = ( X509TrustManager ) tmf.getTrustManagers()[0];
        SavingTrustManager tm = new SavingTrustManager( defaultTrustManager );
        context.init( null, new TrustManager[] { tm }, null );
        SSLSocketFactory factory = context.getSocketFactory();

        // Try to reconnect in case there are newly launched instances and they're not fully up yet
        SSLSocket socket = null;
        int trial = 0;
        boolean success = false;
        ConnectException connectException = null;
        do {
            try {
                LOG.info( "Opening connection to {}:{}", host, port );
                socket = ( SSLSocket ) factory.createSocket( host, port );
                socket.setSoTimeout( 10000 );
                success = true;
            }
            catch ( ConnectException e ) {
                connectException = e;
                Thread.sleep( 1500 );
            }
        }
        while ( !success && trial++ < 10 );

        if( !success ) {
            throw connectException;
        }

        try {
            LOG.debug( "Starting SSL handshake..." );
            socket.startHandshake();
            socket.close();
            LOG.debug( "No errors, certificate is already trusted" );
        }
        catch ( SSLException e ) {
            LOG.debug( "Cert is NOT trusted: {}", e.getMessage() );
        }

        X509Certificate[] chain = tm.chain;
        if ( chain == null ) {
            LOG.warn( "Could not obtain server certificate chain" );
            return;
        }

        new BufferedReader( new InputStreamReader( System.in ) );

        LOG.debug( "Server sent " + chain.length + " certificate(s):" );
        MessageDigest sha1 = MessageDigest.getInstance( "SHA1" );
        MessageDigest md5 = MessageDigest.getInstance( "MD5" );
        for ( int i = 0; i < chain.length; i++ ) {
            X509Certificate cert = chain[i];
            LOG.debug( " " + ( i + 1 ) + " Subject " + cert.getSubjectDN() );
            LOG.debug( "   Issuer  " + cert.getIssuerDN() );
            sha1.update( cert.getEncoded() );
            LOG.debug( "   sha1    " + toHexString( sha1.digest() ) );
            md5.update( cert.getEncoded() );
            LOG.debug( "   md5     " + toHexString( md5.digest() ) );
        }

        int k = 0;

        X509Certificate cert = chain[k];
        String alias = host + "-" + ( k + 1 );
        ks.setCertificateEntry( alias, cert );

        OutputStream out = new FileOutputStream( "jssecacerts" );
        ks.store( out, passphrase );
        out.close();

        LOG.debug( "cert = {}", cert );
        LOG.debug( "Added certificate to keystore 'jssecacerts' using alias '" + alias + "'" );
    }


    private static String toHexString( byte[] bytes ) {
        StringBuilder sb = new StringBuilder( bytes.length * 3 );
        for ( int b : bytes ) {
            b &= 0xff;
            sb.append( HEX_DIGITS[b >> 4] );
            sb.append( HEX_DIGITS[b & 15] );
            sb.append( ' ' );
        }
        return sb.toString();
    }


    private static class SavingTrustManager implements X509TrustManager {

        private final X509TrustManager tm;
        private X509Certificate[] chain;


        SavingTrustManager( X509TrustManager tm ) {
            this.tm = tm;
        }


        public X509Certificate[] getAcceptedIssuers() {
            throw new UnsupportedOperationException();
        }


        public void checkClientTrusted( X509Certificate[] chain, String authType ) throws CertificateException {
            throw new UnsupportedOperationException();
        }


        public void checkServerTrusted( X509Certificate[] chain, String authType ) throws CertificateException {
            this.chain = chain;
            tm.checkServerTrusted( chain, authType );
        }
    }
}
