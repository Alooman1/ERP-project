package edu.univ.erp.domain;

// Shows statistics for a class like average marks, highest, lowest
public class ClassStats {
    private final String componentName;
    private final double average;
    private final double min;
    private final double max;
    private final int count;

    // Creates statistics for an exam component (quiz, midsem, etc.)
    public ClassStats(String componentName, double average, double min, double max, int count) {
        this.componentName = componentName;
        this.average = average;
        this.min = min;
        this.max = max;
        this.count = count;
    }

    // Get which exam component (quiz/midsem/endsem)
    public String getComponentName() { return componentName; }
    // Get average marks of class
    public double getAverage() { return average; }
    // Get lowest marks in class
    public double getMin() { return min; }
    // Get highest marks in class
    public double getMax() { return max; }
    // Get how many students in this stat
    public int getCount() { return count; }
}