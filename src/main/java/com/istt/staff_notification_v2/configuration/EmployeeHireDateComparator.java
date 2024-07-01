package com.istt.staff_notification_v2.configuration;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.regex.Pattern;

import com.istt.staff_notification_v2.entity.Employee;

public class EmployeeHireDateComparator implements Comparator<Employee>{
	public static String changeName(String input) {
        // Normalize the input string to decompose accents
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        // Remove the accents using a regular expression
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String result = pattern.matcher(normalized).replaceAll("");
        // Replace special Vietnamese characters
        result = result.replaceAll("Đ", "D").replaceAll("đ", "d");
        int lastSpaceIndex = input.lastIndexOf(' ');
        if (lastSpaceIndex == -1) {
            return result; // No space found, return the whole string
        }
        String lastWord = result.substring(lastSpaceIndex + 1);
        String remainingString = result.substring(0, lastSpaceIndex);
        return lastWord + " " + remainingString;
    }
    @Override
    public int compare(Employee p1, Employee p2) {
    	String name1 = changeName(p1.getFullname().trim());
    	String name2 = changeName(p2.getFullname().trim());
        if (p1.getHiredate().equals(p2.getHiredate())) {
            return name1.compareTo(name2);
        } else {
            return p1.getHiredate().compareTo(p2.getHiredate());
        }
    }
}
