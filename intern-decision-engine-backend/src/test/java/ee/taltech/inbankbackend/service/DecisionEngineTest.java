package ee.taltech.inbankbackend.service;

import com.github.vladislavgoltjajev.personalcode.locale.estonia.EstonianPersonalCodeValidator;
import ee.taltech.inbankbackend.config.DecisionEngineConstants;
import ee.taltech.inbankbackend.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DecisionEngineTest {

    @InjectMocks
    private DecisionEngine decisionEngine;

    private String debtorPersonalCode;
    private String segment1PersonalCode;
    private String segment2PersonalCode;
    private String segment3PersonalCode;
    private String underagePersonalCode;
    private String oldPersonalCode;

    @BeforeEach
    void setUp() {
        debtorPersonalCode = "49002010965";
        segment1PersonalCode = "49002010976";
        segment2PersonalCode = "49002010987";
        segment3PersonalCode = "49002010998";
        underagePersonalCode = "51501020008";
        oldPersonalCode = "34501024327";
    }

    @Test
    void testDebtorPersonalCode() {
        assertThrows(NoValidLoanException.class,
                () -> decisionEngine.calculateApprovedLoan(debtorPersonalCode, 4000L, 12, "EE"));
    }

    @Test
    void testSegment1PersonalCode() throws InvalidLoanPeriodException, NoValidLoanException,
            InvalidPersonalCodeException, InvalidLoanAmountException, InvalidAgeException {
        Decision decision = decisionEngine.calculateApprovedLoan(segment1PersonalCode, 4000L, 12, "EE");
        assertEquals(2400, decision.getLoanAmount());
        assertEquals(24, decision.getLoanPeriod());
    }

    @Test
    void testSegment2PersonalCode() throws InvalidLoanPeriodException, NoValidLoanException,
            InvalidPersonalCodeException, InvalidLoanAmountException, InvalidAgeException {
        Decision decision = decisionEngine.calculateApprovedLoan(segment2PersonalCode, 4000L, 12, "EE");
        assertEquals(3600, decision.getLoanAmount());
        assertEquals(12, decision.getLoanPeriod());
    }

    @Test
    void testSegment3PersonalCode() throws InvalidLoanPeriodException, NoValidLoanException,
            InvalidPersonalCodeException, InvalidLoanAmountException, InvalidAgeException {
        Decision decision = decisionEngine.calculateApprovedLoan(segment3PersonalCode, 4000L, 12, "EE");
        assertEquals(10000, decision.getLoanAmount());
        assertEquals(12, decision.getLoanPeriod());
    }

    @Test
    void testInvalidPersonalCode() {
        String invalidPersonalCode = "12345678901";
        assertThrows(InvalidPersonalCodeException.class,
                () -> decisionEngine.calculateApprovedLoan(invalidPersonalCode, 4000L, 12, "EE"));
    }

    @Test
    void testInvalidLoanAmount() {
        Long tooLowLoanAmount = DecisionEngineConstants.MINIMUM_LOAN_AMOUNT - 1L;
        Long tooHighLoanAmount = DecisionEngineConstants.MAXIMUM_LOAN_AMOUNT + 1L;

        assertThrows(InvalidLoanAmountException.class,
                () -> decisionEngine.calculateApprovedLoan(segment1PersonalCode, tooLowLoanAmount, 12, "EE"));

        assertThrows(InvalidLoanAmountException.class,
                () -> decisionEngine.calculateApprovedLoan(segment1PersonalCode, tooHighLoanAmount, 12, "EE"));
    }

    @Test
    void testInvalidLoanPeriod() {
        int tooShortLoanPeriod = DecisionEngineConstants.MINIMUM_LOAN_PERIOD - 1;
        int tooLongLoanPeriod = DecisionEngineConstants.MAXIMUM_LOAN_PERIOD + 1;

        assertThrows(InvalidLoanPeriodException.class,
                () -> decisionEngine.calculateApprovedLoan(segment1PersonalCode, 4000L, tooShortLoanPeriod, "EE"));

        assertThrows(InvalidLoanPeriodException.class,
                () -> decisionEngine.calculateApprovedLoan(segment1PersonalCode, 4000L, tooLongLoanPeriod, "EE"));
    }

    @Test
    void testFindSuitableLoanPeriod() throws InvalidLoanPeriodException, NoValidLoanException,
            InvalidPersonalCodeException, InvalidLoanAmountException, InvalidAgeException {
        Decision decision = decisionEngine.calculateApprovedLoan(segment2PersonalCode, 2000L, 12, "EE");
        assertEquals(3600, decision.getLoanAmount());
        assertEquals(12, decision.getLoanPeriod());
    }

    @Test
    void testNoValidLoanFound() {
        assertThrows(NoValidLoanException.class,
                () -> decisionEngine.calculateApprovedLoan(debtorPersonalCode, 10000L, 48, "EE"));
    }

    @Test
    void testUnderage() {
        assertThrows(InvalidAgeException.class,
                () -> decisionEngine.calculateApprovedLoan(underagePersonalCode, 4000L, 12, "EE"));
    }

    @Test
    void testOldPersonalCode() {
        assertThrows(InvalidAgeException.class,
                () -> decisionEngine.calculateApprovedLoan(oldPersonalCode, 4000L, 12, "EE"));
    }

}

