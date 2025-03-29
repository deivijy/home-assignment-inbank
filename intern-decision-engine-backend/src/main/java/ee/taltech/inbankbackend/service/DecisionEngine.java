package ee.taltech.inbankbackend.service;

import com.github.vladislavgoltjajev.personalcode.locale.estonia.EstonianPersonalCodeValidator;
import ee.taltech.inbankbackend.config.CountryConfig;
import ee.taltech.inbankbackend.config.DecisionEngineConstants;
import ee.taltech.inbankbackend.exceptions.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

/**
 * A service class that provides a method for calculating an approved loan amount and period for a customer.
 * The loan amount is calculated based on the customer's credit modifier,
 * which is determined by the last four digits of their ID code.
 */
@Service
public class DecisionEngine {

    // Used to check for the validity of the presented ID code.
    private final EstonianPersonalCodeValidator validator = new EstonianPersonalCodeValidator();

    /**
     * Calculates the maximum loan amount and period for the customer based on their ID code,
     * the requested loan amount and the loan period.
     * The loan period must be between 12 and 60 months (inclusive).
     * The loan amount must be between 2000 and 10000€ months (inclusive).
     *
     * @param personalCode ID code of the customer that made the request.
     * @param loanAmount Requested loan amount
     * @param loanPeriod Requested loan period
     * @return A Decision object containing the approved loan amount and period, and an error message (if any)
     * @throws InvalidPersonalCodeException If the provided personal ID code is invalid
     * @throws InvalidLoanAmountException If the requested loan amount is invalid
     * @throws InvalidLoanPeriodException If the requested loan period is invalid
     * @throws NoValidLoanException If there is no valid loan found for the given ID code, loan amount and loan period
     */
    public Decision calculateApprovedLoan(String personalCode, Long loanAmount, int loanPeriod, String countryCode)
            throws InvalidPersonalCodeException, InvalidLoanAmountException, InvalidLoanPeriodException,
            NoValidLoanException, InvalidAgeException {
        try {
            verifyInputs(personalCode, loanAmount, loanPeriod, countryCode);
        } catch (Exception e) {
            return new Decision(null, null, e.getMessage());
        }

        int creditModifier = getCreditModifier(personalCode);

        if (creditModifier == 0) {
            throw new NoValidLoanException("No valid loan found!");
        }

        return highestValidLoanAmount(creditModifier, loanPeriod);
    }

    /**
     * Calculates the largest valid loan for the current credit modifier and loan period.
     *
     * @return Largest valid loan amount
     */
    private Decision highestValidLoanAmount(int creditModifier, int loanPeriod) throws NoValidLoanException {
        for (int period = loanPeriod; period <= DecisionEngineConstants.MAXIMUM_LOAN_PERIOD; period += 6) {
            for (int amount = DecisionEngineConstants.MAXIMUM_LOAN_AMOUNT; amount >= DecisionEngineConstants.MINIMUM_LOAN_AMOUNT; amount -= 100) {
                double creditScore = ((double) creditModifier / amount) * period / 10;
                if (creditScore >= 0.1) {
                    return new Decision(amount, period, null);
                }
            }
        }

        throw new NoValidLoanException("No valid loan found!");
    }

    /**
     * Calculates the credit modifier of the customer to according to the last four digits of their ID code.
     * Debt - 0000...2499
     * Segment 1 - 2500...4999
     * Segment 2 - 5000...7499
     * Segment 3 - 7500...9999
     *
     * @param personalCode ID code of the customer that made the request.
     * @return Segment to which the customer belongs.
     */
    private int getCreditModifier(String personalCode) {
        return switch (personalCode) {
            case "49002010976" -> DecisionEngineConstants.SEGMENT_1_CREDIT_MODIFIER;
            case "49002010987" -> DecisionEngineConstants.SEGMENT_2_CREDIT_MODIFIER;
            case "49002010998", "34501024327", "51501020008"  -> DecisionEngineConstants.SEGMENT_3_CREDIT_MODIFIER;

            default -> 0;
        };
    }

    /**
     * Verify that all inputs are valid according to business rules.
     * If inputs are invalid, then throws corresponding exceptions.
     *
     * @param personalCode Provided personal ID code
     * @param loanAmount Requested loan amount
     * @param loanPeriod Requested loan period
     * @throws InvalidPersonalCodeException If the provided personal ID code is invalid
     * @throws InvalidLoanAmountException If the requested loan amount is invalid
     * @throws InvalidLoanPeriodException If the requested loan period is invalid
     */
    private void verifyInputs(String personalCode, Long loanAmount, int loanPeriod, String countryCode)
            throws InvalidPersonalCodeException, InvalidLoanAmountException, InvalidLoanPeriodException, InvalidAgeException {

        if (!validator.isValid(personalCode)) {
            throw new InvalidPersonalCodeException("Invalid personal ID code!");
        }
        if (!(DecisionEngineConstants.MINIMUM_LOAN_AMOUNT <= loanAmount)
                || !(loanAmount <= DecisionEngineConstants.MAXIMUM_LOAN_AMOUNT)) {
            throw new InvalidLoanAmountException("Invalid loan amount!");
        }
        if (!(DecisionEngineConstants.MINIMUM_LOAN_PERIOD <= loanPeriod)
                || !(loanPeriod <= DecisionEngineConstants.MAXIMUM_LOAN_PERIOD)) {
            throw new InvalidLoanPeriodException("Invalid loan period!");
        }

        LocalDate birthDate = getBirthDate(personalCode);
        validateAge(birthDate, countryCode);
    }

    private void validateAge(LocalDate birthDate, String countryCode) throws InvalidAgeException {
        int ageInYears = Period.between(birthDate, LocalDate.now()).getYears();
        CountryConfig countryConfig = DecisionEngineConstants.EXPECTED_LIFETIMES.get(countryCode);

        if (ageInYears < countryConfig.getMinimumAgeInYears()) {
            throw new InvalidAgeException("Customer is underage");
        }

        int maxAllowedAge = countryConfig.getMaximumAgeInYears() - (DecisionEngineConstants.MAXIMUM_LOAN_PERIOD / 12);

        if (ageInYears > maxAllowedAge) {
            throw new InvalidAgeException("Customer is too old");
        }
    }

    private LocalDate getBirthDate(String personalCode) throws InvalidPersonalCodeException {
        try {
            int centuryAndSex = Integer.parseInt(personalCode.substring(0, 1));
            int year = Integer.parseInt(personalCode.substring(1, 3));
            int month = Integer.parseInt(personalCode.substring(3, 5));
            int day = Integer.parseInt(personalCode.substring(5, 7));

            int century;
            if (centuryAndSex == 3 || centuryAndSex == 4) {
                century = 1900;
            } else if (centuryAndSex == 5 || centuryAndSex == 6) {
                century = 2000;
            } else {
                throw new InvalidPersonalCodeException("Invalid personal ID code!");
            }

            return LocalDate.of(century + year, month, day);
        } catch (Exception e) {
            throw new InvalidPersonalCodeException("Invalid personal ID code!");
        }
    }
}
