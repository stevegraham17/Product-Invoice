package com.products.products.contoller;

public class NumberToWords {
	  private static final String[] units = {
	            "", "One", "Two", "Three", "Four", "Five", "Six",
	            "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve",
	            "Thirteen", "Fourteen", "Fifteen", "Sixteen",
	            "Seventeen", "Eighteen", "Nineteen"
	    };

	    private static final String[] tens = {
	            "", "", "Twenty", "Thirty", "Forty", "Fifty",
	            "Sixty", "Seventy", "Eighty", "Ninety"
	    };

	    private static final String[] thousands = {
	            "", "Thousand", "Lakh", "Crore"
	    };

	    // Convert a number to words
	    public static String convert(long number) {
	        if (number == 0) {
	            return "Zero";
	        }

	        String words = "";
	        int thousandCounter = 0;

	        while (number > 0) {
	            int chunk;
	            if (thousandCounter == 1) { // Thousand
	                chunk = (int) (number % 1000);
	                number /= 1000;
	            } else { // Lakh and Crore (2 digits)
	                chunk = (int) (number % 100);
	                number /= 100;
	            }

	            if (chunk > 0) {
	                words = convertBelowThousand(chunk) + " " + thousands[thousandCounter] + " " + words;
	            }
	            thousandCounter++;
	        }

	        return words.trim() + " Rupees";
	    }

	    // Convert numbers below 1000
	    private static String convertBelowThousand(int number) {
	        String words = "";

	        if (number % 100 < 20) {
	            words = units[number % 100];
	            number /= 100;
	        } else {
	            words = units[number % 10];
	            number /= 10;

	            words = tens[number % 10] + (words.isEmpty() ? "" : " " + words);
	            number /= 10;
	        }

	        if (number > 0) {
	            words = units[number] + " Hundred" + (words.isEmpty() ? "" : " and " + words);
	        }

	        return words.trim();
	    }
}
