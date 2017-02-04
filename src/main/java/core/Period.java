package core;

/**
 * Defines a period of time. This period can be defined in any unit defined in the Unit enumerator.
 */
public class Period {

    private final int length;
    private final Unit unit;

    /**
     * Creates a period with a fixed length given in a certain unit of time.
     *
     * @param length the length for the period.
     * @param unit   the unit used for the length of the period, not null.
     */
    private Period(int length, Unit unit) {
        this.length = length;
        this.unit = unit;
    }

    /**
     * Obtains an instance of Period with the specified length given in the specified units.
     *
     * @param length the length of the period.
     * @param unit   the unit for the specified length.
     * @return the period, not null.
     */
    public static Period of(int length, Unit unit) {
        return new Period(length, unit);
    }

    /**
     * Returns the length of the period.
     *
     * @return the length of the period.
     */
    public int getLength() {
        return length;
    }

    /**
     * Returns the time unit the period length is specified on.
     *
     * @return the time unit the period length is specified on.
     */
    public Unit getUnit() {
        return unit;
    }

}
