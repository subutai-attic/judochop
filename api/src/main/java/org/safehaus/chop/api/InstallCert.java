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


/** Class used to add the server's certificate to the KeyStore with your trusted certificates. */
public class InstallCert {
    private static final Logger LOG = LoggerFactory.getLogger( InstallCert.class );

    static {
        System.setProperty ( "javax.net.ssl.trustStore", "jssecacerts" );
    }


    public static void installCert( String host, int port, char[] passphrase ) throws Exception {

        if ( passphrase == null ) {
            passphrase = "changeit".toCharArray();
        }

        File file = new File( "jssecacerts" );
        if ( file.isFile() == false ) {
            char SEP = File.separatorChar;
            File dir = new File( System.getProperty( "java.home" ) + SEP + "lib" + SEP + "security" );
            file = new File( dir, "jssecacerts" );
            if ( file.isFile() == false ) {
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

        BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );

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


    private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();


    private static String toHexString( byte[] bytes ) {
        StringBuilder sb = new StringBuilder( bytes.length * 3 );
        for ( int b : bytes ) {
            b &= 0xff;
            sb.append( HEXDIGITS[b >> 4] );
            sb.append( HEXDIGITS[b & 15] );
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