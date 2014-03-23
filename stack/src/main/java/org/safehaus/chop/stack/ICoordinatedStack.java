package org.safehaus.chop.stack;


import java.util.List;

import org.safehaus.chop.api.Commit;
import org.safehaus.chop.api.Module;
import org.safehaus.chop.api.Runner;


public interface ICoordinatedStack extends Stack {


    Commit getCommit();


    Module getModule();


    User getUser();


    StackState getState();


    Iterable<Runner> getRunners();
}
