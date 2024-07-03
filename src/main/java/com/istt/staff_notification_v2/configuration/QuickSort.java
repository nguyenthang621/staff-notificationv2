package com.istt.staff_notification_v2.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.istt.staff_notification_v2.entity.Employee;

public class QuickSort {
	public static void quickSort(ArrayList<Employee> arr, int low, int high, Comparator<Employee> comparator) {
        if (low < high) {
            int pi = partition(arr, low, high, comparator);
            quickSort(arr, low, pi - 1, comparator);
            quickSort(arr, pi + 1, high, comparator);
        }
    }

    private static int partition(ArrayList<Employee> arr, int low, int high, Comparator<Employee> comparator) {
        Employee pivot = arr.get(high);
        Long id_null = Long.valueOf(0);
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (arr.get(j).getStaffId()> pivot.getStaffId() ) {
                i++;
              Long temp = arr.get(i).getStaffId();
              Long temp2 = arr.get(j).getStaffId();
              arr.get(j).setStaffId(id_null);
              arr.get(i).setStaffId(temp2);
              arr.get(j).setStaffId(temp);
            }
        }
        Long temp = arr.get(i+1).getStaffId();
        Long temp2 = arr.get(high).getStaffId();
        arr.get(high).setStaffId(id_null);
        arr.get(i+1).setStaffId(temp2);
        arr.get(high).setStaffId(temp);
        return i + 1;
    }
    
    public static List<Employee> filterEmployee(List<Employee> list, Comparator<Employee> comparator){
    	if(list.size()==0) return null;
    	Collections.sort(list, new EmployeeComparator());
    	ArrayList<Employee> arr = new ArrayList<>(list);
    	quickSort(arr, 0, arr.size()-1, new EmployeeHireDateComparator());
    	list.clear();
    	list.addAll(arr);
    	return list;
    }
}
