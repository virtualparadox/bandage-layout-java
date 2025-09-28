package eu.virtualparadox.bandage.layout;

/**
 * Layout quality levels matching Bandage settings
 */
public enum LayoutQuality {
    FAST(0, 3, 1, 2),
    LOW(1, 12, 8, 2),
    MEDIUM(2, 30, 20, 4),
    HIGH(3, 60, 20, 6),
    MAXIMUM(4, 120, 20, 8);

    private final int value;
    private final int fixedIterations;
    private final int fineTuningIterations;
    private final int precision;

    LayoutQuality(int value, int fixedIterations, int fineTuningIterations, int precision) {
        this.value = value;
        this.fixedIterations = fixedIterations;
        this.fineTuningIterations = fineTuningIterations;
        this.precision = precision;
    }

    public int getValue() { return value; }
    public int getFixedIterations() { return fixedIterations; }
    public int getFineTuningIterations() { return fineTuningIterations; }
    public int getPrecision() { return precision; }
}