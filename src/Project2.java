import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;
public class Project2 {
    // Main method for the program
    public static void main(String[] args) throws FileNotFoundException {
        PrintStream s = System.out;
        double x = System.currentTimeMillis();
        String filePath = args[0];
        String filePath2 = args[1];
        PrintStream o = new PrintStream(new File(args[2]));
        System.setOut(o);

        // Initialize data structures to store employees and branches
        StringToEmployeeHashSet employee_Point = new StringToEmployeeHashSet();
        StringToBranchHashSet branch_Point = new StringToBranchHashSet();

        // Process the initial data from the first input file
        initialTaker(filePath, employee_Point, branch_Point);

        // Check and update data monthly based on the second input file
        Scanner monthChecker = new Scanner(new File(filePath2));
        monthlyUpdater(monthChecker, employee_Point, branch_Point);
        System.setOut(s);
        System.out.println((System.currentTimeMillis()-x)/1000);
    }

    public static void initialTaker(String filePath, StringToEmployeeHashSet employee_Point, StringToBranchHashSet branch_Point){
        try (Scanner initialTaker = new Scanner(new File(filePath))) {
            // Read the file line by line
            while (initialTaker.hasNext()){
                String str = initialTaker.nextLine();
                String[] employeeNew = str.split(",");
                String district = employeeNew[1].strip();
                String branch = employeeNew[0].strip();
                String name = employeeNew[2].strip();
                String position = employeeNew[3].strip();
                Employee employee = new Employee(branch,name,position,district);
                if(branch_Point.search(branch+ " " + district) == -1){
                    Branch branchAdd = new Branch(branch ,district);
                    branch_Point.add(branch + " " + district, branchAdd);
                    employee.linkToBranch(branch,district,branch_Point);
                    employee_Point.add(branch + " " +district + " " + employee.name,employee);
                    branchAdd.positionAdder(employee);
                }
                else {
                    employee.linkToBranch(branch,district,branch_Point);
                    employee_Point.add(branch + " " +district + " " + employee.name,employee);
                    branch_Point.initialValueArray[branch_Point.search(branch+ " " + district)].positionAdder(employee);
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
    // Method to update data monthly based on input from a scanner
    public static void monthlyUpdater(Scanner initialTaker, StringToEmployeeHashSet employee_Point, StringToBranchHashSet branch_Point) {
        // Array representing months
        String[] monthList = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

        // Flag to indicate whether the current month has been found
        boolean monthNotFound = true;
        String str2 = initialTaker.nextLine();
        String month = ".";

        // Read the file line by line
        while (initialTaker.hasNextLine()) {
            if (monthNotFound) {
                month = str2.split(":")[0];
                monthNotFound = false;
            }

            // Check if the current line represents a valid month
            if (asList(month, monthList)) {
                while (initialTaker.hasNextLine()) {
                    String[] str = initialTaker.nextLine().split(":");
                    String function = str[0];
                    // Check if the line contains a valid month
                    if (asList(function, monthList)) {
                        month = function;
                        // Reset monthly bonuses for all branches
                        for (int i = 0; i < branch_Point.elNum; i++) {
                            if (branch_Point.initialValueArray[branch_Point.allIndexFilled[i]] != null) {
                                branch_Point.initialValueArray[branch_Point.allIndexFilled[i]].bonusMonthly = 0;
                            }
                        }
                        break;
                    } else {
                        // Execute actions based on the function specified in the line
                        if (function.equals("ADD")) {
                            // Add a new employee to the system
                            String[] employeeNew = str[1].split(",");
                            String district = employeeNew[1].strip();
                            String branch = employeeNew[0].strip();
                            String name = employeeNew[2].strip();
                            String position = employeeNew[3].strip();
                            Employee employee = new Employee(branch, name, position, district);
                            if (branch_Point.search(branch + " " + district) == -1) {
                                Branch branch1 = new Branch(branch, district);
                                employee.linkToBranch(branch, district, branch_Point);
                                employee_Point.add(branch + " " + district + " " + employee.name, employee);
                                branch_Point.add(branch + " " + district, branch1);
                            } else {
                                if (employee_Point.search(branch + " " + district + " " + name.strip()) == -1) {
                                    employee.linkToBranch(branch, district, branch_Point);
                                    employee_Point.add(branch + " " + district + " " + employee.name, employee);
                                    branch_Point.initialValueArray[branch_Point.search(branch + " " + district)].positionAdder(employee);
                                    for (Employee employee1 : employee.branch.queueToDismiss) {
                                        firing(employee1, employee_Point);
                                    }
                                    if (employee.position.equals("COOK") && employee.branch.cookNum == 2)
                                        firing(employee.branch.manager, employee_Point);
                                    else if (employee.position.equals("CASHİER") && employee.branch.cashierNum == 2) {
                                        promoteCashier(employee.branch,employee_Point);
                                    }
                                    while (!employee.branch.queueToCashierPromote.isEmpty()) {
                                        if (employee.branch.cashierNum == 1) break;
                                        promoteCashier(employee.branch,employee_Point);
                                    }
                                } else {
                                    System.out.println("Existing employee cannot be added again.");
                                }
                            }
                        } else if (function.equals("PERFORMANCE_UPDATE")) {
                            // Update the performance of an employee
                            String[] employeeNew = str[1].split(",");
                            String district = employeeNew[1].strip();
                            String branch = employeeNew[0].strip();
                            String name = employeeNew[2].strip();
                            int point = Integer.parseInt(employeeNew[3].strip());
                            int index = employee_Point.search(branch + " " + district + " " + name.strip());
                            if (index == -1) {
                                System.out.println("There is no such employee.");
                            } else {
                                Employee theEmployee = employee_Point.initialValueArray[index];
                                theEmployee.promotionPoints += point / 200;
                                if (point >= 0) {
                                    int bonus = point % 200;
                                    theEmployee.monthlyBonus += bonus;
                                    theEmployee.branch.bonusMonthly += bonus;
                                    theEmployee.branch.totalBonus += bonus;
                                }
                                addQueueCommonFunction(theEmployee, employee_Point);
                                firing(theEmployee, employee_Point);
                                while (!theEmployee.branch.queueToCashierPromote.isEmpty()) {
                                    if (theEmployee.branch.cashierNum == 1) break;
                                    promoteCashier(theEmployee.branch,employee_Point);
                                }
                                if (theEmployee.position.equals("COOK")) firing(theEmployee.branch.manager, employee_Point);
                            }
                        } else if (function.equals("LEAVE")) {
                            // Process an employee leaving
                            String[] employeeNew = str[1].split(",");
                            String district = employeeNew[1].strip();
                            String branch = employeeNew[0].strip();
                            String name = employeeNew[2].strip();
                            int index = employee_Point.search(branch + " " + district + " " + name);
                            if (index == -1) {
                                System.out.println("There is no such employee.");
                            } else {
                                Employee theEmployee = employee_Point.initialValueArray[index];
                                leave(theEmployee, employee_Point);
                                while (!theEmployee.branch.queueToCashierPromote.isEmpty()) {
                                    if (theEmployee.branch.cashierNum == 1) break;
                                    promoteCashier(theEmployee.branch,employee_Point);
                                }
                            }
                        } else if (function.equals("PRINT_MONTHLY_BONUSES")) {
                            // Print the total monthly bonuses for a specific branch
                            String[] branchName = str[1].split(",");
                            String city = branchName[0].strip();
                            String district = branchName[1].strip();
                            if (branch_Point.search(city + " " + district) != -1) {
                                System.out.println("Total bonuses for the " + district + " branch this month are: " + branch_Point.initialValueArray[branch_Point.search(city + " " + district)].bonusMonthly);
                            } else {
                                System.out.println("NO BRANCH");
                            }
                        } else if (function.equals("PRINT_OVERALL_BONUSES")) {
                            // Print the total overall bonuses for a specific branch
                            String[] branchName = str[1].split(",");
                            String city = branchName[0].strip();
                            String district = branchName[1].strip();
                            if (branch_Point.search(city + " " + district) != -1) {
                                System.out.println("Total bonuses for the " + district + " branch are: " + branch_Point.initialValueArray[branch_Point.search(city + " " + district)].totalBonus);
                            } else {
                                System.out.println("NO BRANCH");
                            }
                        } else if (function.equals("PRINT_MANAGER")) {
                            // Print the manager of a specific branch
                            String[] branchName = str[1].split(",");
                            String city = branchName[0].strip();
                            String district = branchName[1].strip();
                            if (branch_Point.search(city + " " + district) != -1) {
                                System.out.println("Manager of the " + district + " branch is " + branch_Point.initialValueArray[branch_Point.search(city + " " + district)].manager.name + ".");
                            } else {
                                System.out.println("NO BRANCH");
                            }
                        }
                    }
                }
            }
        }
    }

    public static void leave(Employee theEmployee, StringToEmployeeHashSet employee_Point) {
        // Check if the employee is a MANAGER
        if (theEmployee.position.equals("MANAGER")) {
            // Check if there are more than 1 cooks in the branch and there are cooks in the queue to be promoted
            if (theEmployee.branch.cookNum > 1 && !theEmployee.branch.queueToCookPromote.isEmpty()) {
                // If conditions are met, print leaving message, promote a cook, and update employee points
                System.out.println(theEmployee.name + " is leaving from branch: " + theEmployee.branch.district + ".");
                promoteCook(theEmployee.branch);
                employee_Point.delete(theEmployee.branchname + " " + theEmployee.district + " " + theEmployee.name);
            } else {
                // If conditions are not met, check promotion points for dismissal or bonus allocation
                if (theEmployee.promotionPoints <= leastPerformancePoint) {
                    theEmployee.branch.queueToDismiss.add(theEmployee);
                } else {
                    while (theEmployee.branch.queueToDismiss.contains(theEmployee))theEmployee.branch.queueToDismiss.remove(theEmployee);
                    theEmployee.monthlyBonus += notLeaveBonus;
                    theEmployee.branch.bonusMonthly += notLeaveBonus;
                    theEmployee.branch.totalBonus += notLeaveBonus;
                }
            }
        }
        // Check if the employee is a COOK
        else if (theEmployee.position.equals("COOK")) {
            // Check if there are more than 1 cooks in the branch or there are cooks in the queue to be promoted
            if (theEmployee.branch.cookNum > 1 || !theEmployee.branch.queueToCashierPromote.isEmpty()) {
                // If conditions are met, remove cook from promotion queue, update cook count, print leaving message, and update employee points
                theEmployee.branch.queueToCookPromote.remove(theEmployee);
                theEmployee.branch.cookNum -= 1;
                System.out.println(theEmployee.name + " is leaving from branch: " + theEmployee.branch.district + ".");
                employee_Point.delete(theEmployee.branchname + " " + theEmployee.district + " " + theEmployee.name);
            } else {
                // If conditions are not met, check promotion points for dismissal or bonus allocation
                if (theEmployee.promotionPoints <= leastPerformancePoint) {
                    theEmployee.branch.queueToDismiss.add(theEmployee);
                } else {
                    while (theEmployee.branch.queueToDismiss.contains(theEmployee))theEmployee.branch.queueToDismiss.remove(theEmployee);
                    theEmployee.monthlyBonus += notLeaveBonus;
                    theEmployee.branch.bonusMonthly += notLeaveBonus;
                    theEmployee.branch.totalBonus += notLeaveBonus;
                }
            }
        }
        // Check if the employee is a CASHIER
        else if (theEmployee.position.equals("CASHIER")) {
            // Check if there is more than 1 cashier in the branch
            if (theEmployee.branch.cashierNum > 1) {
                // If conditions are met, decrement cashier count, print leaving message, remove cashier from promotion queue, and update employee points
                theEmployee.branch.cashierNum -= 1;
                System.out.println(theEmployee.name + " is leaving from branch: " + theEmployee.branch.district + ".");
                theEmployee.branch.queueToCashierPromote.remove(theEmployee);
                employee_Point.delete(theEmployee.branchname + " " + theEmployee.district + " " + theEmployee.name);
            } else {
                // If conditions are not met, check promotion points for dismissal or bonus allocation
                if (theEmployee.promotionPoints <= leastPerformancePoint) {
                    theEmployee.branch.queueToDismiss.add(theEmployee);
                } else {
                    while (theEmployee.branch.queueToDismiss.contains(theEmployee))theEmployee.branch.queueToDismiss.remove(theEmployee);
                    theEmployee.monthlyBonus += notLeaveBonus;
                    theEmployee.branch.bonusMonthly += notLeaveBonus;
                    theEmployee.branch.totalBonus += notLeaveBonus;
                }
            }
        }
        // Check if the employee is a COURIER
        else if (theEmployee.position.equals("COURIER")) {
            // Check if there is more than 1 courier in the branch
            if (theEmployee.branch.courierNum > 1) {
                // If conditions are met, decrement courier count, print leaving message, and update employee points
                System.out.println(theEmployee.name + " is leaving from branch: " + theEmployee.branch.district + ".");
                theEmployee.branch.courierNum -= 1;
                employee_Point.delete(theEmployee.branchname + " " + theEmployee.district + " " + theEmployee.name);
            } else {
                // If conditions are not met, check promotion points for dismissal or bonus allocation
                if (theEmployee.promotionPoints <= leastPerformancePoint) {
                    theEmployee.branch.queueToDismiss.add(theEmployee);
                } else {
                    while (theEmployee.branch.queueToDismiss.contains(theEmployee))theEmployee.branch.queueToDismiss.remove(theEmployee);
                    theEmployee.monthlyBonus += notLeaveBonus;
                    theEmployee.branch.bonusMonthly += notLeaveBonus;
                    theEmployee.branch.totalBonus += notLeaveBonus;
                }
            }
        }
    }
    public static final int notLeaveBonus = 200;
    public static final int leastPerformancePoint = -5;
    /**
     * Common function to manage the promotion queues for Cashiers and Cooks based on their promotion points.
     * If the employee is a Cashier and has promotion points greater than or equal to 3, they are added to the
     * queueToCashierPromote. If the employee is a Cashier but does not meet the promotion points criteria, they
     * are removed from the queueToCashierPromote. Similarly, for Cooks, if their promotion points are greater than
     * or equal to 10, they are added to the queueToCookPromote; otherwise, they are removed from the queue.
     *
     * @param theEmployee The employee for whom the promotion queue is managed.
     */
    public static void addQueueCommonFunction(Employee theEmployee,StringToEmployeeHashSet employeeHashSet) {
        // Check if the employee is a Cashier or Cook
        if ("CASHIER".equals(theEmployee.position)) {
            // Add or remove the Cashier based on promotion points
            if (theEmployee.promotionPoints >= 3) {
                if(!theEmployee.branch.queueToCashierPromote.contains(theEmployee)) {
                    theEmployee.branch.queueToCashierPromote.add(theEmployee);
                }
            } else {
                theEmployee.branch.queueToCashierPromote.remove(theEmployee);
            }
            while (!theEmployee.branch.queueToCashierPromote.isEmpty()){
                if(theEmployee.branch.cashierNum == 1) break;
                promoteCashier(theEmployee.branch,employeeHashSet);
            }
        }
        if ("COOK".equals(theEmployee.position)) {
            // Add or remove the Cook based on promotion points
            if (theEmployee.promotionPoints >= 10) {
                if(!theEmployee.branch.queueToCookPromote.contains(theEmployee)) {
                    theEmployee.branch.queueToCookPromote.add(theEmployee);
                    firing(theEmployee.branch.manager,employeeHashSet);
                }
            } else {
                theEmployee.branch.queueToCookPromote.remove(theEmployee);
            }
        }
    }
    /**
     * Handles the firing process for an employee based on their promotion points and position.
     * If an employee's promotion points fall below or equal to -5, appropriate actions are taken
     * based on their position within the company.
     *
     * @param theEmployee The employee to be evaluated and potentially fired.
     * @param employee_Point A mapping of employee names to their corresponding promotion points.
     */
    public static void firing(Employee theEmployee, StringToEmployeeHashSet employee_Point) {
        // Check if the employee's promotion points are below or equal to -5
        if (theEmployee.promotionPoints <= leastPerformancePoint) {
            // Evaluate the position of the employee
            if (theEmployee.position.equals("MANAGER")) {
                // Check conditions for promoting a cook or adding the manager to the dismissal queue
                if (theEmployee.branch.cookNum > 1 && !theEmployee.branch.queueToCookPromote.isEmpty()) {
                    employee_Point.delete(theEmployee.branchname + " " + theEmployee.district + " " + theEmployee.name);
                    System.out.println(theEmployee.name +" is dismissed from branch: "+theEmployee.branch.district +".");
                    while (theEmployee.branch.queueToDismiss.contains(theEmployee))theEmployee.branch.queueToDismiss.remove(theEmployee);
                    promoteCook(theEmployee.branch);
                } else {
                    if(!theEmployee.branch.queueToDismiss.contains(theEmployee))theEmployee.branch.queueToDismiss.add(theEmployee);
                }
            } else if (theEmployee.position.equals("COOK")) {
                // Check conditions for promoting a cashier or adding the cook to the dismissal queue
                if (theEmployee.branch.cookNum > 1 || (!theEmployee.branch.queueToCashierPromote.isEmpty() && theEmployee.branch.cashierNum > 1)) {
                    theEmployee.branch.queueToCookPromote.remove(theEmployee);
                    employee_Point.delete(theEmployee.branchname + " " + theEmployee.district + " " + theEmployee.name);
                    System.out.println(theEmployee.name +" is dismissed from branch: "+theEmployee.branch.district +".");
                    while (theEmployee.branch.queueToDismiss.contains(theEmployee))theEmployee.branch.queueToDismiss.remove(theEmployee);
                    theEmployee.branch.cookNum -=1;
                    promoteCashier(theEmployee.branch,employee_Point);
                } else {
                    if(!theEmployee.branch.queueToDismiss.contains(theEmployee))theEmployee.branch.queueToDismiss.add(theEmployee);

                }
            } else if (theEmployee.position.equals("CASHIER")) {
                // Check conditions for dismissing a cashier or adding them to the dismissal queue
                if (theEmployee.branch.cashierNum > 1) {
                    theEmployee.branch.queueToCashierPromote.remove(theEmployee);
                    theEmployee.branch.cashierNum -=1;
                    employee_Point.delete(theEmployee.branchname + " " + theEmployee.district + " " + theEmployee.name);
                    System.out.println(theEmployee.name +" is dismissed from branch: "+theEmployee.branch.district +".");
                    while (theEmployee.branch.queueToDismiss.contains(theEmployee))theEmployee.branch.queueToDismiss.remove(theEmployee);

                } else {
                    if(!theEmployee.branch.queueToDismiss.contains(theEmployee))theEmployee.branch.queueToDismiss.add(theEmployee);
                }
            } else if (theEmployee.position.equals("COURIER")) {
                // Check conditions for dismissing a courier or adding them to the dismissal queue
                if (theEmployee.branch.courierNum > 1) {
                    theEmployee.branch.courierNum -=1;
                    employee_Point.delete(theEmployee.branchname + " " + theEmployee.district + " " + theEmployee.name);
                    System.out.println(theEmployee.name +" is dismissed from branch: "+theEmployee.branch.district +".");
                    while (theEmployee.branch.queueToDismiss.contains(theEmployee))theEmployee.branch.queueToDismiss.remove(theEmployee);

                } else {
                    if(!theEmployee.branch.queueToDismiss.contains(theEmployee))theEmployee.branch.queueToDismiss.add(theEmployee);
                }
            }
        }
    }
    // Method to promote a cook to manager
    public static void promoteCook(Branch branch){
        // Check if there are cooks in the promotion queue
        if(!branch.queueToCookPromote.isEmpty()&& branch.cookNum > 1) {
            // Pop the first cook from the promotion queue
            Employee newManager = branch.queueToCookPromote.pop();

            // Update employee details for promotion to manager
            newManager.position = "MANAGER";
            newManager.promotionPoints -= 10;

            // Update branch manager and cook counts
            branch.manager = newManager;
            newManager.branch.cookNum -= 1;

            // Print promotion message
            System.out.println(branch.manager.name + " is promoted from Cook to Manager.");
        }
    }

    // Method to promote a cashier to cook
    public static void promoteCashier(Branch branch,StringToEmployeeHashSet e){
        // Check if there are employees in the cashier promotion queue
        if(!branch.queueToCashierPromote.isEmpty() && branch.cashierNum > 1) {
            // Pop the first employee from the cashier promotion queue
            Employee newCook = branch.queueToCashierPromote.pop();

            // Update employee details for promotion to cook
            newCook.position = "COOK";
            newCook.branch.cookNum += 1;
            newCook.promotionPoints -= 3;
            newCook.branch.cashierNum -= 1;

            // Print promotion message
            System.out.println(newCook.name + " is promoted from Cashier to Cook.");
            if (newCook.promotionPoints >= 10) {
                if(!newCook.branch.queueToCookPromote.contains(newCook)) {
                    newCook.branch.queueToCookPromote.add(newCook);
                    firing(newCook.branch.manager,e);
                }
            } else {
                newCook.branch.queueToCookPromote.remove(newCook);
            }
        }
    }
    public static boolean asList(String s,String[] listStr){
        for (String a: listStr){
            if(s .equals(a))return true;
        }
        return false;
    }


}
