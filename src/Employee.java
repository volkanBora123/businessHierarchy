public class Employee {
    Branch branch;

    String position;
    String branchname;
    String district;
    int monthlyBonus;
    int promotionPoints;
    String name;
    Employee(String branchname,String name,String position,String district){
        this.branchname = branchname;
        this.name = name;
        this.position = position;
        this.district = district;
    }
    public void linkToBranch(String branchname,String district,StringToBranchHashSet stringToBranchHashSet){
        int index = stringToBranchHashSet.search(branchname +" "  + district);
        if(index != -1){
            this.branch = stringToBranchHashSet.initialValueArray[index];
        }
    }}

