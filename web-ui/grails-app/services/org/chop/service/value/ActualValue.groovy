package org.chop.service.value

class ActualValue extends Value {

    protected void calc(Map<String, String> json) {
        value += json.actualTime
    }
}
