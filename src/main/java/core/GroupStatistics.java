package core;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GroupStatistics computes statistics for multiple sets of data. It keeps track of values for multiple
 * sets of values. The only thing required is that the number of values per set is exactly the same.
 *
 * It does not keep record in memory of each value added for computation. The statistics are computed on
 * the fly and, therefore, the collection of statistics that can be performed is limited to the ones that
 * can be computed incrementally.
 */
public class GroupStatistics {

    private final List<SummaryStatistics> itemsStatistics;

    /**
     * Creates a GroupStatistics with a specified number of data sets.
     *
     * @param dataSetCount the number of different data sets to compute for.
     */
    public GroupStatistics(int dataSetCount) {
        itemsStatistics = new ArrayList<>(dataSetCount);

        for (int i = 0; i < dataSetCount; i++) {
            itemsStatistics.add(new SummaryStatistics());
        }
    }

    /**
     * Adds a new entry of values for each data set. The input values must be always in the same order.
     *
     * @param values the list with the values for each data set, not null.
     */
    public void addEntry(List<Double> values) {

        if (values.size() != itemsStatistics.size()) {
            throw new IllegalArgumentException("Group statistics expected " + itemsStatistics.size() + " " +
                    "items but got " + values.size());
        }

        for (int i = 0; i < values.size(); i++) {
            itemsStatistics.get(i).addValue(values.get(i)); // there is not ned to check the get method call!
        }

    }

    /**
     * Returns the number of values in each data set.
     *
     * @return the number of values in each data set.
     */
    public long getCount() {
        return itemsStatistics.get(0).getN();
    }

    /**
     * Returns a list with the means for each data set. The returned list includes the means in the same
     * order as the values added to the GroupStatistics.
     *
     * @return the list with the means for each data set, not null.
     */
    public List<Double> getMeans() {
        return itemsStatistics.stream()
                .map(SummaryStatistics::getMean)
                .collect(Collectors.toList());
    }

    /**
     * Returns a list with the standard deviations for each data set. The returned list includes the standard
     * deviations in the same order as the values added to the GroupStatistics.
     *
     * @return the list with the standard deviations for each data set, not null.
     */
    public List<Double> getStandardDeviations() {
        return itemsStatistics.stream()
                .map(SummaryStatistics::getStandardDeviation)
                .collect(Collectors.toList());
    }

    /**
     * Clears all statistics for all data sets.
     */
    public void clear() {
        itemsStatistics.forEach(SummaryStatistics::clear);
    }

}
