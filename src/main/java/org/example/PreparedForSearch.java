package org.example;

public final class PreparedForSearch implements Comparable<PreparedForSearch> {

    private final String stringCondition;
    private final int byteSum;
    private final int lineByteSize;

    public PreparedForSearch(String stringCondition, int byteSum, int lineByteSize) {
        this.stringCondition = stringCondition;
        this.byteSum = byteSum;
        this.lineByteSize = lineByteSize;
    }

    public String getStringCondition() {
        return stringCondition;
    }

    public int getByteSum() {
        return byteSum;
    }

    public int getLineByteSize() {
        return lineByteSize;
    }

    @Override
    public String toString() {
        return "stringCondition: " + stringCondition + " byteSum: " + byteSum + " lineByteSize: " + lineByteSize;
    }

    @Override
    public int compareTo(PreparedForSearch o2) {
        if (getStringCondition() == null || o2.getStringCondition() == null) {
            throw new NullPointerException("Поля не могут быть пустыми");
        }
        try {
            return Integer.valueOf(getStringCondition()).compareTo(Integer.valueOf(o2.getStringCondition()));
        } catch (NumberFormatException nfe) {
            return getStringCondition().compareTo(o2.getStringCondition());
        }
    }
}
