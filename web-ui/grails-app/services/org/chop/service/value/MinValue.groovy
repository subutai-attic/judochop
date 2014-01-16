package org.chop.service.value

class MinValue extends Value {

    MinValue() {
        value = Double.MAX_VALUE
    }

    protected void calc(Map<String, String> json) {
        if (json.minTime < value) {
            value = json.minTime
        }
    }
}
