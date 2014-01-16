package org.chop.service.value

abstract class Value {

    int failures = 0
    int ignores = 0
    double value = 0

    protected abstract void calc(Map<String, String> json)

    void merge(Map<String, String> json) {
        collect(json)
        calc(json)
    }

    private void collect(Map<String, String> json) {
        failures += json.failures
        ignores += json.ignores
    }
}
