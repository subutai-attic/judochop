package org.chop.service.value

class MaxValue extends Value {

    protected void calc(Map<String, String> json) {
        if (json.maxTime > value) {
            value = json.maxTime
        }
    }
}
