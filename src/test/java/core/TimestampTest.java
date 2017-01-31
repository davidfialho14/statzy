package core;

import org.junit.Ignore;
import org.junit.Test;

import static core.Unit.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TimestampTest {

    @Test
    public void truncateTo_YearsOnDate20160612AndTime121212_Date20160121AndTime000000() throws Exception {

        assertThat(Timestamp.of(2016, 6, 12, 12, 12, 12).truncatedTo(YEARS),
                is(Timestamp.of(2016, 1, 1, 0, 0, 0)));
    }

    @Test
    public void truncateTo_MonthsOnDate20160612AndTime121212_Date20160601AndTime000000() throws Exception {

        assertThat(Timestamp.of(2016, 6, 12, 12, 12, 12).truncatedTo(MONTHS),
                is(Timestamp.of(2016, 6, 1, 0, 0, 0)));
    }

    @Test
    public void truncateTo_DaysOnDate20160612AndTime121212_Date20160612AndTime000000() throws Exception {

        assertThat(Timestamp.of(2016, 6, 12, 12, 12, 12).truncatedTo(DAYS),
                is(Timestamp.of(2016, 6, 12, 0, 0, 0)));
    }

    @Test
    public void truncateTo_HoursOnDate20160612AndTime121212_Date20160612AndTime120000() throws Exception {

        assertThat(Timestamp.of(2016, 6, 12, 12, 12, 12).truncatedTo(HOURS),
                is(Timestamp.of(2016, 6, 12, 12, 0, 0)));
    }

    @Test
    public void truncateTo_MinutesOnDate20160612AndTime121212_Date20160612AndTime121200() throws Exception {

        assertThat(Timestamp.of(2016, 6, 12, 12, 12, 12).truncatedTo(MINUTES),
                is(Timestamp.of(2016, 6, 12, 12, 12, 0)));
    }

    @Test
    public void truncateTo_MinutesOnDate20160612AndTime121212_Date20160612AndTime121212() throws Exception {

        assertThat(Timestamp.of(2016, 6, 12, 12, 12, 12).truncatedTo(SECONDS),
                is(Timestamp.of(2016, 6, 12, 12, 12, 12)));
    }

    @Test
    public void plus_1YearOnDate20161010AndTime101010_Date20171010AndTime101010() throws Exception {

        Timestamp timestamp = Timestamp.of(2016, 10, 10, 10, 10, 10);
        Period period = Period.of(1, YEARS);

        assertThat(timestamp.plus(period),
                is(Timestamp.of(2017, 10, 10, 10, 10, 10)));
    }

    @Test
    public void plus_1MonthOnDate20161010AndTime101010_Date20161110AndTime101010() throws Exception {

        Timestamp timestamp = Timestamp.of(2016, 10, 10, 10, 10, 10);
        Period period = Period.of(1, MONTHS);

        assertThat(timestamp.plus(period),
                is(Timestamp.of(2016, 11, 10, 10, 10, 10)));
    }

    @Test
    public void plus_3MonthsOnDate20161010AndTime101010_Date20170201AndTime101010() throws Exception {

        Timestamp timestamp = Timestamp.of(2016, 10, 10, 10, 10, 10);
        Period period = Period.of(3, MONTHS);

        assertThat(timestamp.plus(period),
                is(Timestamp.of(2017, 1, 10, 10, 10, 10)));
    }

    @Test
    public void plus_1MonthOnDate20160130AndTime101010_Date20160229AndTime101010() throws Exception {

        Timestamp timestamp = Timestamp.of(2016, 1, 30, 10, 10, 10);
        Period period = Period.of(1, MONTHS);

        assertThat(timestamp.plus(period),
                is(Timestamp.of(2016, 2, 29, 10, 10, 10)));
    }

}