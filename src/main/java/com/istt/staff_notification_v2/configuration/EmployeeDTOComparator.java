package com.istt.staff_notification_v2.configuration;

import java.util.Comparator;

import com.istt.staff_notification_v2.dto.EmployeeDTO;

public class EmployeeDTOComparator implements Comparator<EmployeeDTO> {
    public static String changeName(String input) { //moveLastWordToFront
        int lastSpaceIndex = input.lastIndexOf(' ');
        if (lastSpaceIndex == -1) {
            return input; // No space found, return the whole string
        }
        String lastWord = input.substring(lastSpaceIndex + 1);
        String remainingString = input.substring(0, lastSpaceIndex);
        return lastWord + " " + remainingString;
    }
    @Override
    public int compare(EmployeeDTO p1, EmployeeDTO p2) {
    	String name1 = changeName(p1.getFullname());
    	String name2 = changeName(p2.getFullname());
        if (p1.getDepartment()== p2.getDepartment()) {
            return name1.compareTo(name2);
        } else {
            return p1.getDepartment().getDepartmentId().compareTo(p2.getDepartment().getDepartmentId());
        }
    }
}
