package ee.taltech.inbankbackend.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CountryConfig {
    private final String countryCode;
    private final Integer minimumAgeInYears;
    private final Integer maximumAgeInYears;
}
