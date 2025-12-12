package interfaceDB;

import database.DataAccessException;
import java.util.List;
import model.Employee;
import model.Shift;

public interface EmployeeDBIF {

    void connectShiftToEmployee(Employee employee, Shift shift) throws DataAccessException;

    Employee getEmployeeId(int employeeId) throws DataAccessException;

    default Employee findActiveEmployee(int employeeId) throws DataAccessException { //findActiveEmployee den her forstår jeg ikke helt hvorfor der er her.
        return getEmployeeId(employeeId); // nu sletter jeg den ikke men der er noget her der skal fixes for det er et interface så der skal slet ikke være return typer osv.
    }

    List<Employee> getAllEmployees() throws DataAccessException;
}
