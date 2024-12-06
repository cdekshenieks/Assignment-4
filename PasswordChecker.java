import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class PasswordChecker {
    // Separate Chaining Hash Table
    static class HashTableSeparateChaining {
        private LinkedList<Pair>[] table;

        public HashTableSeparateChaining(int size) {
            table = new LinkedList[size];
            for (int i = 0; i < size; i++) {
                table[i] = new LinkedList<>();
            }
        }

        private int hashCode(String key, int multiplier) {
            int hash = 0;
            int skip = Math.max(1, key.length() / 8);
            for (int i = 0; i < key.length(); i += skip) {
                hash = (hash * multiplier) + key.charAt(i);
            }
            return Math.abs(hash % this.table.length);
        }

        public void insert(String key, int value, int multiplier) {
            int index = hashCode(key, multiplier);
            for (Pair pair : table[index]) {
                if (pair.key.equals(key)) {
                    return; // Avoid duplicates
                }
            }
            table[index].add(new Pair(key, value));
        }

        public boolean search(String key, int multiplier) {
            int index = hashCode(key, multiplier);
            int comparisons = 0;
            for (Pair pair : table[index]) {
                comparisons++;
                if (pair.key.equals(key)) {
                    System.out.println("Search cost for separate chaining: " + comparisons);// print comparisons
                    return true;
                }
            }
            System.out.println("Search cost for separate chaining: " + comparisons);// print comparisons
            return false;
        }


    }

    // Linear Probing Hash Table
    static class HashTableLinearProbing {
        private String[] keys;
        private int[] values;
        public int comparisons;

        public HashTableLinearProbing(int size) {
            this.comparisons = 0;
            keys = new String[size];
            values = new int[size];
        }

        private int hashCode(String key, int multiplier) {
            int hash = 0;
            for (int i = 0; i < key.length(); i++) {
                hash = (hash * multiplier) + key.charAt(i);
            }
            return Math.abs(hash % keys.length);
        }

        public void insert(String key, int value, int multiplier) {
            int index = hashCode(key, multiplier);
            while (keys[index] != null) {
                if (keys[index].equals(key)) {
                    return; // Avoid duplicates
                }
                index = (index + 1) % keys.length; // Linear probing
            }
            keys[index] = key;
            values[index] = value;
        }

        public boolean search(String key, int multiplier) {
            int index = hashCode(key, multiplier);
            comparisons = 0;
            while (keys[index] != null) {
                comparisons++;
                if (keys[index].equals(key)) {
                    System.out.println("Search cost for linear probing: " + comparisons);
                    return true;
                }
                index = (index + 1) % keys.length; // Linear probing
            }
            System.out.println("Search cost for linear probing: " + comparisons);
            return false;
        }

    }

    // Pair class for separate chaining
    static class Pair {
        String key;
        int value;

        Pair(String key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    // Password strength checker
    public static boolean isStrongPassword(String password, HashTableSeparateChaining dictionarySC, HashTableLinearProbing dictionaryLP, int multiplier) {
        if (password.length() < 8) {
            return false;
        }

        // Check if the password is in the dictionary
        if (dictionarySC.search(password, multiplier) || dictionaryLP.search(password, multiplier)) {
            return false;
        }

        // Check if it's a dictionary word followed by a digit
        String modifiedPassword = password.substring(0, password.length() - 1);
        if (dictionarySC.search(modifiedPassword, multiplier) || dictionaryLP.search(modifiedPassword, multiplier)) {
            return false;
        }

        return true;
    }


    public static void main(String[] args) {
        // Initialize hash tables
        HashTableSeparateChaining dictionarySC = new HashTableSeparateChaining(1000);
        HashTableLinearProbing dictionaryLP = new HashTableLinearProbing(20000);
        Scanner scanner = new Scanner(System.in);

        // Load dictionary words
        try {
            // Change this path to the actual file path of the dictionary
            File dictionaryFile = new File("Dictionary.csv");
            Scanner fileScanner = new Scanner(dictionaryFile);
            int lineNumber = 1;

            while (fileScanner.hasNextLine()) {
                String word = fileScanner.nextLine().trim();
                if (!word.isEmpty()) {
                    dictionarySC.insert(word, lineNumber++, 37); // Insert into separate chaining hash table
                    dictionaryLP.insert(word, lineNumber++, 37); // Insert into linear probing hash table
                }
            }
            fileScanner.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("Dictionary file not found.");
            return; // Exit if dictionary file is not found
        }

        // Test passwords
        System.out.println("Enter the passwords that you want to test (separate with a comma): ");
        String[] passwords = scanner.nextLine().split(", ");
        int multiplier = 37;

        for (String password : passwords) {
            boolean isStrong = isStrongPassword(password, dictionarySC, dictionaryLP, multiplier);
            System.out.println("Password: " + password + " is " + (isStrong ? "strong" : "weak"));
            System.out.println();
        }
    }
}

