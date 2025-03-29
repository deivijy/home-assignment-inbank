package ee.taltech.inbankbackend.config;

import java.util.Map;

/**
 * Holds all necessary constants for the decision engine.
 */
public class DecisionEngineConstants {
    public static final Integer MINIMUM_LOAN_AMOUNT = 2000;
    public static final Integer MAXIMUM_LOAN_AMOUNT = 10000;
    public static final Integer MAXIMUM_LOAN_PERIOD = 48;
    public static final Integer MINIMUM_LOAN_PERIOD = 12;
    public static final Integer SEGMENT_1_CREDIT_MODIFIER = 100;
    public static final Integer SEGMENT_2_CREDIT_MODIFIER = 300;
    public static final Integer SEGMENT_3_CREDIT_MODIFIER = 1000;
    public static final Integer MINIMUM_AGE = 18;
    // I would keep this in a database and use these from there.
    public static final Map<String, CountryConfig> EXPECTED_LIFETIMES = Map.of(
            "EE", new CountryConfig("EE", MINIMUM_AGE, 79),
            "LV", new CountryConfig("LV", MINIMUM_AGE, 75),
            "LT", new CountryConfig("LT", MINIMUM_AGE, 78)
    );
}
