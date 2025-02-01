import java.util.LinkedList;

public class Branch {
    String city;
    String district;
    Employee manager;
    int cookNum = 0;
    int cashierNum = 0;
    int courierNum = 0;
    int bonusMonthly = 0;
    int totalBonus = 0;
    LinkedList<Employee> queueToDismiss = new LinkedList<>();
    LinkedList<Employee> queueToCookPromote = new LinkedList<>();
    LinkedList<Employee> queueToCashierPromote = new LinkedList<>();

    Branch(String branch ,String district){
        this.city = branch;
        this.district = district;
    }
    void positionAdder(Employee employee){
        String position = employee.position;
        if(position.equals("MANAGER")) {
            manager = employee;
        }
        else if(position.equals("COOK")) {
            cookNum += 1;
        }
        else if (position.equals("CASHIER")) {
                cashierNum +=1;
        } else if (position.equals("COURIER")) {
            courierNum+=1;
        }
    }
}
