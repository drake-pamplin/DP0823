package src.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import src.VO.RentalAgreement;
import src.VO.Tool;
import src.exception.InvalidArgException;
import src.repository.RentalRepository;
import src.utils.Constants;

public class RentalService {
    public RentalService(RentalRepository rentalRepository, ToolService toolService) {
        this.rentalRepository = rentalRepository;
        this.toolService = toolService;
    }

    private RentalRepository rentalRepository;
    private ToolService toolService;

    // Determine if args are valid.
    private String AreRentalAgreementArgsValid(
        int rentalAgreementDiscountPercent,
        int rentalAgreementRentalDays,
        String rentalAgreementToolCode
    ) {
        String invalidField = "";
        if (rentalAgreementDiscountPercent < 0 || rentalAgreementDiscountPercent > 100) {
            invalidField = Constants.fieldDiscountPercent;
        }
        if (invalidField.isEmpty() && rentalAgreementRentalDays < 1) {
            invalidField = Constants.fieldRentalDays;
        }
        if (invalidField.isEmpty()) {
            try {
                toolService.GetToolByCode(rentalAgreementToolCode);
            } catch (InvalidArgException e) {
                invalidField = Constants.fieldToolCode;
            }
        }
        
        return invalidField;
    }
    
    // Generates new rental agreement.
    private RentalAgreement CreateNewRentalAgreement(
        int rentalAgreementChargeDays,
        Date rentalAgreementCheckoutDate,
        float rentalAgreementDailyRentalCharge,
        float rentalAgreementDiscountAmount,
        int rentalAgreementDiscountPercent,
        Date rentalAgreementDueDate,
        float rentalAgreementFinalCharge,
        float rentalAgreementPreDiscountCharge,
        int rentalAgreementRentalDays,
        String rentalAgreementToolBrand,
        String rentalAgreementToolCode,
        String rentalAgreementToolType
    ) {
        // Generate unique serial number based on time.
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(rentalAgreementCheckoutDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        String rentalAgreementSerialNumber = String.format("%s%s%s%s%s%s",
            year, month, day, hour, minute, second
        );
        
        RentalAgreement rentalAgreement = new RentalAgreement(
            rentalAgreementChargeDays,
            rentalAgreementCheckoutDate,
            rentalAgreementDailyRentalCharge,
            rentalAgreementDiscountAmount,
            rentalAgreementDiscountPercent,
            rentalAgreementDueDate,
            rentalAgreementFinalCharge,
            rentalAgreementPreDiscountCharge,
            rentalAgreementRentalDays,
            rentalAgreementSerialNumber,
            rentalAgreementToolBrand,
            rentalAgreementToolCode,
            rentalAgreementToolType
        );
        
        return rentalAgreement;
    }

    // Method to generate a new rental agreement.
    public RentalAgreement GenerateRentalAgreement(
        Date testCheckoutDate,
        int rentalAgreementDiscountPercent,
        int rentalAgreementRentalDays,
        String rentalAgreementToolCode
    ) throws InvalidArgException {
        // Validate args.
        String valid = AreRentalAgreementArgsValid(
            rentalAgreementDiscountPercent,
            rentalAgreementRentalDays,
            rentalAgreementToolCode
        );
        if (!valid.isEmpty()) {
            String errorMessage = "";
            if (valid.equals(Constants.fieldDiscountPercent)) {
                errorMessage = Constants.exceptionMessageInvalidDiscountPercent;
            }
            if (valid.equals(Constants.fieldRentalDays)) {
                errorMessage = Constants.exceptionMessageInvalidRentalDaysQuantity;
            }
            if (valid.equals(Constants.fieldToolCode)) {
                errorMessage = String.format(Constants.exceptionMessageInvalidArg, valid, rentalAgreementToolCode);
            }
            throw new InvalidArgException(Constants.exceptionMessageInvalidArgGeneric, valid, errorMessage);
        }
        
        // Calculated from checkout date and rental days.
        Date rentalAgreementCheckoutDate = new Date();
        if (testCheckoutDate != null) {
            rentalAgreementCheckoutDate = testCheckoutDate;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(rentalAgreementCheckoutDate);
        calendar.add(Calendar.DATE, rentalAgreementRentalDays);
        Date rentalAgreementDueDate = calendar.getTime();
        
        // Retrieved from tool.
        Tool tool = toolService.GetToolByCode(rentalAgreementToolCode);
        String rentalAgreementToolBrand = tool.getToolBrand();
        String rentalAgreementToolType = tool.getToolType();
        float rentalAgreementDailyRentalCharge = tool.getToolDailyCharge();
        
        // Subtract non-chargeable days from total day count.
        // Determine number of chargeable days in the rental duration.
        int rentalAgreementChargeDays = rentalAgreementRentalDays;
        if (!tool.getToolChargeWeekend()) {
            rentalAgreementChargeDays -= GetWeekendDaysForPeriod(rentalAgreementCheckoutDate, rentalAgreementRentalDays);
        }
        if (!tool.getToolChargeHoliday()) {
            rentalAgreementChargeDays -= GetHolidaysForPeriod(rentalAgreementCheckoutDate, rentalAgreementRentalDays);
        }

        // Calculated from number of charge days * the daily charge.
        float rentalAgreementPreDiscountCharge = rentalAgreementChargeDays * tool.getToolDailyCharge();
        rentalAgreementPreDiscountCharge = RoundFloat(rentalAgreementPreDiscountCharge);

        // Calucated from pre-discount charge * discount percent.
        float rentalAgreementDiscountAmount = rentalAgreementPreDiscountCharge * ((float)rentalAgreementDiscountPercent / 100f);
        rentalAgreementDiscountAmount = RoundFloat(rentalAgreementDiscountAmount);

        // Calculated from pre-discount charge - discount charge.
        float rentalAgreementFinalCharge = rentalAgreementPreDiscountCharge - rentalAgreementDiscountAmount;

        // Generated when creating the rental agreement.
        RentalAgreement rentalAgreement = CreateNewRentalAgreement(
            rentalAgreementChargeDays,
            rentalAgreementCheckoutDate,
            rentalAgreementDailyRentalCharge,
            rentalAgreementDiscountAmount,
            rentalAgreementDiscountPercent,
            rentalAgreementDueDate,
            rentalAgreementFinalCharge,
            rentalAgreementPreDiscountCharge,
            rentalAgreementRentalDays,
            rentalAgreementToolBrand,
            rentalAgreementToolCode,
            rentalAgreementToolType
        );
        rentalRepository.AddRentalAgreement(rentalAgreement);
        
        return rentalAgreement;
    }

    private int GetHolidaysForPeriod(Date checkoutDate, int checkoutDays) {
        int holidays = 0;

        Calendar calendar = Calendar.getInstance();
        for (int dayIndex = 0; dayIndex <= checkoutDays; dayIndex++) {
            calendar.setTime(checkoutDate);
            calendar.add(Calendar.DATE, dayIndex);
            if (IsDayEligibleIndependenceDay(calendar, checkoutDays, dayIndex)) {
                holidays++;
            }
            if (IsDayEligibleLaborDay(calendar, checkoutDays, dayIndex)) {
                holidays++;
            }
        }

        return holidays;
    }
    
    public RentalAgreement GetRentalAgreementBySerialNumber(String serialNumber) throws InvalidArgException {
        RentalAgreement rentalAgreement = null;
        rentalAgreement = rentalRepository.GetRentalAgreementBySerialNumber(serialNumber);
        if (rentalAgreement == null) {
            String errorMessage = String.format(Constants.exceptionMessageInvalidArg, Constants.fieldSerialNumber, serialNumber);
            throw new InvalidArgException(Constants.exceptionMessageInvalidArgGeneric, Constants.fieldSerialNumber, errorMessage);
        }
        
        return rentalAgreement;
    }
    
    public List<String> GetRentalAgreementsSerialNumberList() {
        List<String> rentalAgreementList = rentalRepository.GetRentalAgreementSerialNumbers();
        
        return rentalAgreementList;
    }

    private int GetWeekendDaysForPeriod(Date checkoutDate, int checkoutDays) {
        int weekendDays = 0;

        Calendar calendar = Calendar.getInstance();
        for (int dayIndex = 0; dayIndex <= checkoutDays; dayIndex++) {
            calendar.setTime(checkoutDate);
            calendar.add(Calendar.DATE, dayIndex);
            if (IsDayWeekend(calendar)) {
                weekendDays++;
            }
        }
        
        return weekendDays;
    }
    
    private boolean IsDayEligibleIndependenceDay(Calendar calendar, int checkoutDays, int dayIndex) {
        if (Calendar.JULY != calendar.get(Calendar.MONTH)) {
            return false;
        }
        if (4 != calendar.get(Calendar.DAY_OF_MONTH)) {
            return false;
        }
        if (Calendar.SATURDAY == calendar.get(calendar.get(Calendar.DAY_OF_WEEK))) {
            if (dayIndex == 0) {
                return false;
            }
        }
        if (Calendar.SUNDAY == calendar.get(calendar.get(Calendar.DAY_OF_WEEK))) {
            if (checkoutDays - dayIndex == 1) {
                return false;
            }
        }
        return true;
    }

    private boolean IsDayEligibleLaborDay(Calendar calendar, int checkoutDays, int dayIndex) {
        if (Calendar.SEPTEMBER != calendar.get(Calendar.MONTH)) {
            return false;
        }
        if (Calendar.DAY_OF_MONTH > 7) {
            return false;
        }
        if (Calendar.MONDAY != calendar.get(Calendar.DAY_OF_WEEK)) {
            return false;
        }
        return true;
    }
    
    private boolean IsDayWeekend(Calendar calendar) {
        if (Calendar.SATURDAY == calendar.get(Calendar.DAY_OF_WEEK) || Calendar.SUNDAY == calendar.get(Calendar.DAY_OF_WEEK)) {
            return true;
        } 
        
        return false;
    }

    private float RoundFloat(float value) {
        BigDecimal valueToRound = new BigDecimal(value);
        BigDecimal rounded = valueToRound.setScale(2, RoundingMode.HALF_UP);
        // BigDecimal rounded = valueToRound.round(new MathContext(3, RoundingMode.HALF_UP));
        return rounded.floatValue();
    }
}
