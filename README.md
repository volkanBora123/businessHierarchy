# Lahmacun Entrepreneur

## Project Description
Lahmacun Entrepreneur is a Java-based employee and branch management system designed for a chain of lahmacun restaurants. The system handles employee hiring, firing, promotions, monthly evaluations, and branch operations while ensuring performance efficiency for large datasets.

## Features
- **Employee Management**: Add, remove, and promote employees based on performance and branch requirements.
- **Branch Management**: Create new branches and track the number of employees in different positions.
- **Performance Tracking**: Update and evaluate employee performance, determining bonuses and promotions.
- **Hash-Based Data Storage**: Efficient storage and retrieval of employees and branches using custom hash structures.

## File Structure
- `Project2.java`: The main program that handles input files and processes operations.
- `Branch.java`: Represents a restaurant branch with employee tracking functionalities.
- `StringToBranchHashSet.java`: Implements a custom hash set for storing and managing branches.
- `StringToEmployeeHashSet.java`: Implements a custom hash set for storing and managing employees.

## How to Run
1. Compile the Java files:
   ```sh
   javac Project2.java Branch.java StringToBranchHashSet.java StringToEmployeeHashSet.java
   ```
2. Run the program with input files:
   ```sh
   java Project2 input1.txt input2.txt output.txt
   ```
   - `input1.txt`: Contains initial employee and branch data.
   - `input2.txt`: Contains monthly updates for promotions, dismissals, and performance changes.
   - `output.txt`: Stores the program's output.

## Input Format
### `input1.txt`
Each line represents an employee with the format:
```
<Branch>, <District>, <Employee Name>, <Position>
```
### `input2.txt`
Commands for monthly updates, such as:
```
ADD: <Branch>, <District>, <Employee Name>, <Position>
PERFORMANCE_UPDATE: <Branch>, <District>, <Employee Name>, <Performance Score>
LEAVE: <Branch>, <District>, <Employee Name>
PRINT_MONTHLY_BONUSES: <Branch>, <District>
PRINT_OVERALL_BONUSES: <Branch>, <District>
PRINT_MANAGER: <Branch>, <District>
```

## Notes
- The system uses a quadratic probing-based hashing approach for efficient data storage.
- Employees are automatically promoted or dismissed based on predefined performance rules.

## Author
Developed for **CmpE 250 (Data Structures and Algorithms) - Fall 2023** as part of the Lahmacun Entrepreneur project.

