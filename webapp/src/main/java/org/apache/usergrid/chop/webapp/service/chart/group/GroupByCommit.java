package org.apache.usergrid.chop.webapp.service.chart.group;

import org.safehaus.chop.api.Commit;
import org.safehaus.chop.api.Run;

import java.util.*;

public class GroupByCommit {

    // <commitId, List<Run>>
    private Map<String, List<Run>> commitRuns = new LinkedHashMap<String, List<Run>>();

    public GroupByCommit(List<Commit> commits, List<Run> runs) {
        putCommits(commits);
        putRuns(runs);
    }

    private void putCommits(List<Commit> commits) {
        for (Commit commit : commits) {
            commitRuns.put(commit.getId(), new ArrayList<Run>() );
        }
    }

    private void putRuns(List<Run> runs) {
        for (Run run : runs) {
            putRun(run);
        }
    }

    private void putRun(Run run) {
        List<Run> runs = commitRuns.get( run.getCommitId() );

        if (runs != null) {
            runs.add(run);
        }
    }

    public Map<String, List<Run>> get() {
        return commitRuns;
    }
}
