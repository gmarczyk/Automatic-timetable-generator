package pl.shared.utils;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.scheduler.shared.scheduling.utils.MathUtils;


public class MathUtilsTest {

    @Test
    public void getStdDev() {
        Assertions.assertEquals(2.16,
                MathUtils.getStdDev(new Double[] {
                        new Double(0), new Double(0), new Double(0), new Double(5) }), 0.2);
    }

}