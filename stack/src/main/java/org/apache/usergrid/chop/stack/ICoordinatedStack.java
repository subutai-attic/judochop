package org.apache.usergrid.chop.stack;


<<<<<<< HEAD:stack/src/main/java/org/safehaus/chop/stack/ICoordinatedStack.java
import org.apache.usergrid.chop.api.Commit;
import org.apache.usergrid.chop.api.Module;
import org.apache.usergrid.chop.api.Runner;
=======
import org.safehaus.chop.api.Commit;
import org.safehaus.chop.api.Module;
import org.safehaus.chop.api.Runner;
>>>>>>> dacd615ebeaae3a0dac592cb7f0367c81e8f4b22:stack/src/main/java/org/apache/usergrid/chop/stack/ICoordinatedStack.java


public interface ICoordinatedStack extends Stack {


    Commit getCommit();


    Module getModule();


    User getUser();


    StackState getState();


    Iterable<Runner> getRunners();
}
