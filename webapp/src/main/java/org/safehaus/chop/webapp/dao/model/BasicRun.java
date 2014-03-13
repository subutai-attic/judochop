package org.safehaus.chop.webapp.dao.model;

import org.safehaus.chop.api.*;

import java.util.Iterator;

public class BasicRun implements Run {

    private Commit commit;
    private Summary summary;

    public BasicRun(Commit commit, Summary summary) {
        this.commit = commit;
        this.summary = summary;
    }

    @Override
    public Commit getCommitVersion() {
        return commit;
    }

    @Override
    public Summary getSummary() {
        return summary;
    }

    @Override
    public Iterator<TestResult> getResults() {
        return null;
    }
}

