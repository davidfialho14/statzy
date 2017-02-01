package core;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;

public class GroupStatisticsTest {

    private GroupStatistics groupStatistics;

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    @Before
    public void setUp() throws Exception {
        groupStatistics = new GroupStatistics(2);
    }

    @Test
    public void getCountAndMeansAndStdDevs_From3SetsOfValues_ObtainsCorrectCount3AndCorrectMeansAndStdDevs() throws Exception {

        groupStatistics.addEntry(Arrays.asList(2.0, 5.5));
        groupStatistics.addEntry(Arrays.asList(1.0, 4.5));
        groupStatistics.addEntry(Arrays.asList(3.0, 6.5));

        collector.checkThat(groupStatistics.getCount(), is(3L));
        collector.checkThat(groupStatistics.getMeans(), is(Arrays.asList(2.0, 5.5)));
        collector.checkThat(groupStatistics.getStandardDeviations(), is(Arrays.asList(1.0, 1.0)));
    }


}