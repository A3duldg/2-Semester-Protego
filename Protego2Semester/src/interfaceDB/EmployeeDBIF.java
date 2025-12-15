package interfaceDB;

import database.DataAccessException;

import java.util.ArrayList;
import model.Employee;
import model.Shift;

public interface EmployeeDBIF {

    void connectShiftToEmployee(Employee employee, Shift shift) throws DataAccessException;

    Employee getEmployeeId(int employeeId) throws DataAccessException;

    default Employee findActiveEmployee(int employeeId) throws DataAccessException { 
        return getEmployeeId(employeeId); 
    }

    ArrayList<Employee> getAllEmployees() throws DataAccessException;
}
