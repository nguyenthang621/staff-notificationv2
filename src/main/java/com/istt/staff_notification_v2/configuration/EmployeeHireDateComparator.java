package com.istt.staff_notification_v2.configuration;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.regex.Pattern;

import com.istt.staff_notification_v2.entity.Employee;
import com.istt.staff_notification_v2.utils.utils;

public class EmployeeHireDateComparator implements Comparator<Employee>{
    @Override
    public int compare(Employee p1, Employee p2) {
    	String name1 = utils.changeName(p1.getFullname().trim());
    	String name2 = utils.changeName(p2.getFullname().trim());
        if (p1.getHiredate().equals(p2.getHiredate())) {
            return name1.compareTo(name2);
        } else {
            return p1.getHiredate().compareTo(p2.getHiredate());
        }
    }
}
