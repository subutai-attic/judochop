package org.apache.usergrid.chop.client.ssh;


import java.io.IOException;
import java.io.InputStream;


public class Utils {

    public static final String DEFAULT_USER = "ubuntu";


    public static String checkAck( InputStream in ) throws IOException {
        int b = in.read();

        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if( b == 0 || b == -1 ) {
            return null;
        }

        if( b == 1 || b == 2 ) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c=in.read();
                sb.append((char)c);
            }
            while ( c != '\n' );

            return sb.toString();
        }
        throw new RuntimeException( "Invalid value, this shouldn't have gotten here" );
    }


    public static String convertToNumericalForm( String fileMode ) {

        if ( fileMode.length() != 9 ) {
            throw new RuntimeException( "File mode string should be 9 characters long: " + fileMode );
        }

        int[] permissions = new int[3];

        for( int i = 0; i < 3; i++ ) {
            if( fileMode.charAt( i * 3 ) == 'r' ) {
                permissions[ i ] += 4;
            }
            if( fileMode.charAt( i * 3 + 1 ) == 'w' ) {
                permissions[ i ] += 2;
            }
            if( fileMode.charAt( i * 3 + 2 ) == 'x' ) {
                permissions[ i ] += 1;
            }
        }

        StringBuilder sb = new StringBuilder( 3 );
        return sb.append( permissions[ 0 ] )
                 .append( permissions[1] )
                 .append( permissions[2] )
                 .toString();
    }
}
