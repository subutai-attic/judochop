package org.safehaus.chop.webapp.service.calc.iterations;

import org.safehaus.chop.api.Run;
import org.safehaus.chop.api.RunResult;

import java.util.List;
import java.util.Map;

public class FailureFilter {

    static Map<Run, List<RunResult>> filter(Map<Run, List<RunResult>> runResults, String type) {

        for (List<RunResult> list : runResults.values()) {
            for (int i = 0; i < list.size(); i++) {
                RunResult runResult = list.get(i);

                if (runResult == null) {
                    continue;
                }

                if (("FAILED".equals(type) && runResult.getFailureCount() == 0)
                        || ("SUCCESS".equals(type) && runResult.getFailureCount() > 0)
                        ) {
                    list.set(i, null);
                }
            }
        }

        return runResults;
    }
}
