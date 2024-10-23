package com.mindex.challenge.data;
/*
This is the reportingStructure object. The constructor is empty and both fields have "getters" and "setters".
This object represents the number of people reporting under an employee.
 */
public class ReportingStructure {

    private Employee employee;
    private int numberOfReports;

    public ReportingStructure() {
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public int getNumberOfReports() {
        return numberOfReports;
    }

    public void setNumberOfReports(int numberOfReports) {
        this.numberOfReports = numberOfReports;
    }

}
