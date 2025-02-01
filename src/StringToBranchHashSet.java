public class StringToBranchHashSet {
    public int capacity = 1453;
    public static final double LOAD_FACTOR_THRESHOLD = 0.5;
    public String[] initialKeyArray = new String[capacity];
    public Branch[] initialValueArray = new Branch[capacity];
    public boolean[] trespassList = new boolean[capacity];
    public int[] allIndexFilled = new int[capacity];
    public int elNum = 0;

    public int search(String key){
        int indexShould = Math.abs(key.hashCode())%capacity;
        int retIndex = -1;
        int collisions = 1;
        if(initialKeyArray[indexShould] != null && (initialKeyArray[indexShould].equals(key))) return indexShould;
        while ((initialKeyArray[indexShould] != null && !(initialKeyArray[indexShould].equals(key)) )|| trespassList[indexShould]){
            if(initialKeyArray[indexShould] != null && (initialKeyArray[indexShould].equals(key))) {
                retIndex = indexShould;
                break;
            }
            indexShould += collisions;
            indexShould %= capacity;
            collisions+=2;
        }
        return retIndex;
    }
    public void add(String key, Branch val) {
        // Check if the key already exists in the hash set
        int index = search(key);

        // If the key is found, update the associated branch and return
        if (index != -1 && initialKeyArray[index].equals(key)) {
            return;
        }

        // If the key is not found, calculate the initial index based on the key's hash code
        if (index == -1) {
            index = Math.abs(key.hashCode()) % capacity;
            int collision = 1;
            while (trespassList[index]) {
                index += collision;
                collision += 2;

                // Wrap around to the beginning of the array if the end is reached
                if (index >= capacity) {
                    index  -= capacity;
                }
            }
        }

        // Mark the index as occupied and store the key-value pair
        trespassList[index] = true;
        initialKeyArray[index] = key;
        initialValueArray[index] = val;
        // Update the array tracking filled indices and increment the element count
        allIndexFilled[elNum] = index;
        elNum++;

        // Check if rehashing is needed based on the load factor threshold
        if ((double) elNum / capacity > LOAD_FACTOR_THRESHOLD) {
            reHash();
        }
    }


    private void reHash(){
        // Find the next prime number for the new capacity
        capacity = primeFinder(capacity);

        // Create new arrays with the updated capacity
        String[] newKeyArray = new String[capacity];
        Branch[] newValueArray = new Branch[capacity];
        boolean[] newTrespassList = new boolean[capacity];
        int[] newAllIndexFilled = new int[capacity];

        // Rehash each element in the existing arrays
        for (int i = 0; i < elNum; i++) {
            int indexNew = Math.abs(this.initialKeyArray[allIndexFilled[i]].hashCode()) % capacity;
            int collision = 1;
            while (newTrespassList[indexNew]) {
                indexNew += collision;
                collision += 2;
                // Wrap around to the beginning of the array if the end is reached
                if (indexNew >= capacity) {
                    indexNew = indexNew - capacity;
                }
            }
            newKeyArray[indexNew] = initialKeyArray[allIndexFilled[i]];
            newValueArray[indexNew] = initialValueArray[allIndexFilled[i]];
            newTrespassList[indexNew] = true;
            newAllIndexFilled[i] = indexNew;
        }

        // Update the table with the new arrays
        this.initialKeyArray = newKeyArray;
        this.initialValueArray = newValueArray;
        this.trespassList = newTrespassList;
        this.allIndexFilled = newAllIndexFilled;

    }
    public void delete(String key) {
        // Search for the index of the key in the hash table
        int index = search(key);

        // If the key is not found, no deletion is needed
        if (index == -1) {
            return;
        }

        // Mark the slot at the found index as vacant by setting key and value to null
        initialKeyArray[index] = null;
        initialValueArray[index] = null;
    }
    private static int primeFinder(int x) {
        // Initialize variables
        boolean notFound = true;
        int primeNominate = 2 * x + 1;

        // Continue searching for the next prime until found
        while (notFound) {
            // Assume the number is prime until proven otherwise
            notFound = false;

            // Check for divisors from 3 to the square root of primeNominate, incrementing by 2
            for (int i = 3; i <= (int) Math.sqrt(primeNominate); i = i + 2) {
                // If a divisor is found, increment primeNominate by 2 and set notFound to true
                if (primeNominate % i == 0) {
                    primeNominate += 2;
                    notFound = true;
                    break; // Exit the for loop once a divisor is found
                }
            }
        }

        // Return the next prime number
        return primeNominate;
    }
}
