package org.chop.service.value

class AvgValue extends Value {

    private int count = 0

    protected void calc(Map<String, String> json) {
        doCalc(json.meanTime)
    }

    void add(Value another) {
        doCalc(another.value)
    }

    private doCalc(double d) {
        count++
        this.value = (value + d) / count
    }
}
