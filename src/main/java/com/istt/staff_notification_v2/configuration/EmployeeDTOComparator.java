package com.istt.staff_notification_v2.configuration;

import java.util.Comparator;

import com.istt.staff_notification_v2.dto.EmployeeDTO;
import com.istt.staff_notification_v2.utils.utils;

public class EmployeeDTOComparator implements Comparator<EmployeeDTO> {
    @Override
    public int compare(EmployeeDTO p1, EmployeeDTO p2) {
    	String name1 = utils.changeName(p1.getFullname().trim());
    	String name2 = utils.changeName(p2.getFullname().trim());
        if (p1.getDepartment()== p2.getDepartment()) {
            return name1.compareTo(name2);
        } else {
            return p1.getDepartment().getDepartmentId().compareTo(p2.getDepartment().getDepartmentId());
        }
    }
}
