package org.chop.service.value

class ValueFactory {

    Value create(ValueType type) {

        Value value

        if (type == ValueType.MIN) {
            value = new MinValue()
        } else if (type == ValueType.MAX) {
            value = new MaxValue()
        } else if (type == ValueType.ACTUAL) {
            value = new ActualValue()
        } else {
            value = new AvgValue()
        }

        return value
    }
}
