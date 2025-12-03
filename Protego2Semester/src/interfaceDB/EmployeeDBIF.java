package interfaceDB;
import database.DataAccessException;
import model.Employee;
import model.Shift;

public interface EmployeeDBIF {


	public void connectShiftToEmployee(Employee employee, Shift shift) throws DataAccessException;
}
