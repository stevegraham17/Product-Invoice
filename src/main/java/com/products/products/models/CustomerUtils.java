package com.products.products.models;

public class CustomerUtils {

   /* public static boolean isBusinessCustomer(Invoice invoice) {
        if (invoice == null) return false;
        String gstin = invoice.getCustomerGstin();
        return gstin != null && !gstin.trim().isEmpty() && isValidGstin(gstin.trim().toUpperCase());
    }

    public static boolean isValidGstin(String gstin) {
        String regex = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z][A-Z0-9]Z[A-Z0-9]$";
        return gstin.matches(regex);
    }*/
	
	public static CustomerType getCustomerType(Invoice invoice) {
        if (invoice == null) return null;

        String gstin = invoice.getCustomerGstin();
        if (gstin != null && !gstin.trim().isEmpty() && isValidGstin(gstin.trim().toUpperCase())) {
            return CustomerType.BUSINESS; // Valid GST -> B2B
        }
        return CustomerType.CONSUMER; // No GST -> B2C
    }

    // Validate GSTIN format
    public static boolean isValidGstin(String gstin) {
        if (gstin == null) return false;
        String regex = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z][A-Z0-9]Z[A-Z0-9]$";
        return gstin.matches(regex);
    }
}



