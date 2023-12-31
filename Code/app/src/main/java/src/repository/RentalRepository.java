package src.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import src.VO.RentalAgreement;

public class RentalRepository {
    public RentalRepository() {
        rentalAgreementLibrary = new HashMap<>();
    }

    private Map<String, RentalAgreement> rentalAgreementLibrary;

    // Add a new rental agreement to the database.
    public void AddRentalAgreement(RentalAgreement rentalAgreement) {
        rentalAgreementLibrary.put(rentalAgreement.getRentalAgreementSerialNumber(), rentalAgreement);
    }
    
    // Get a rental agreement by serial number.
    public RentalAgreement GetRentalAgreementBySerialNumber(String serialNumber) throws NullPointerException {
        RentalAgreement rentalAgreement = rentalAgreementLibrary.get(serialNumber);

        return rentalAgreement;
    }

    // Get list of all rental agreement serial numbers.
    public List<String> GetRentalAgreementSerialNumbers() {
        List<String> serialNumbers = new ArrayList<>();
        
        rentalAgreementLibrary.forEach((key, value) -> {
            serialNumbers.add(key);
        });

        return serialNumbers;
    }
}
