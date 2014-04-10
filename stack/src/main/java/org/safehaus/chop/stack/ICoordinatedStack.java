package org.safehaus.chop.stack;


import org.apache.usergrid.chop.api.Commit;
import org.apache.usergrid.chop.api.Module;
import org.apache.usergrid.chop.api.Runner;


public interface ICoordinatedStack extends Stack {


    Commit getCommit();


    Module getModule();


    User getUser();


    StackState getState();


    Iterable<Runner> getRunners();
}
