package interfaceDB;
import database.DataAccessException;
import model.Employee;
import model.Manager;
import model.Shift;

public interface EmployeeDBIF {


	public void connectShiftToEmployee(Employee employee, Shift shift) throws DataAccessException;

	Employee getEmployeeId(int employeeId);

    default Employee findActiveEmployee (int employeeId) throws DataAccessException {
        return getEmployeeId(employeeId);
    }
}
