/* AUTHOR : Dominik Mendel
 * For demonstrative purposes only */
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * Problem statement: Availity receives enrollment files from various benefits
 *     management and enrollment solutions (I.e. HR platforms, payroll
 *     platforms).  Most of these files are typically in EDI format.  However,
 *     there are some files in CSV format.  For the files in CSV format, write a
 *     program in a language that makes sense to you that will read the content
 *     of the file and separate enrollees by insurance company in its own file.
 *     Additionally, sort the contents of each file by last and first name
 *     (ascending).  Lastly, if there are duplicate User Ids for the same
 *     Insurance Company, then only the record with the highest version should
 *     be included.
 *     The following data points are included in the file: • User
 *     Id (string) • First Name (string) • Last Name (string) • Version
 *     (integer) • Insurance Company (string) */
public class Solution {
    private static final String csvDelimiter = ",";

    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            for (String inputFilePath : args) {
                System.out.println("Reading and sorting file " + inputFilePath);
                String[] rawCsvData = readCsvFileToArray(inputFilePath);
                Map<String, TreeSet<Person>> sortedDataByCompanyMap = sortRawCsvData(rawCsvData);
                createCompanyCsvFile(sortedDataByCompanyMap);
            }
        }
    }

    // This class should belong in it's own file, but keeping solution contained to 1 file.
    private static class Person implements Comparable<Person> {
        protected String userId;
        protected String firstName;
        protected String lastName;
        protected String insuranceCompany;
        protected int version;

        //@todo might be able to remove userID as that is unique and may be part of the map
        public Person(String userId, String firstName, String lastName, int version, String insuranceCompany) {
            this.userId = userId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.insuranceCompany = insuranceCompany;
            this.version = version;
        }

        public String[] getPersonAttributes() {
            return new String[] {
                this.userId,
                this.firstName,
                this.lastName,
                Integer.toString(this.version),
                this.insuranceCompany
            };
        }

        /* Compares by Last name > first name > user ID > version */
        @Override
        public int compareTo(Person thatPerson) {
            // String.compareTo() compares lexicographically to Strings
            int comparedLastName = this.lastName.compareTo(thatPerson.lastName);
            if (comparedLastName > 1) {
                //
                return 1;
            } else if (comparedLastName < 1) {
                return -1;
            } else {
                // 0 case, or they are the same. Now compare first names
                int comparedFirstName = this.firstName.compareTo(thatPerson.lastName);
                if (comparedFirstName > 1) {
                    return 1;
                } else if (comparedFirstName < 1) {
                    return -1;
                } else {
                    // 0 case, or they are the same. Now compare User IDs
                    // Assuming people can have the same first + last name but be different people with different user IDs
                    int comparedUserId = this.userId.compareTo(thatPerson.userId);
                    if (comparedUserId > 1) {
                        return 1;
                    } else if (comparedUserId < 1) {
                        return -1;
                    } else {
                        // 0 case, or they are the same. Now compare versions
                        if (this.version > thatPerson.version) {
                            return 1;
                        } else if (this.version < thatPerson.version) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                }
            }
        }
    }

    private static String[] readCsvFileToArray(String filePath) {
        try {
            File file = new File(filePath);
            FileReader fileReader = new FileReader(file);
            BufferReader bufferReader = new BufferReader(fileReader);
            String line = "";

            String[] outputStrings;
            while ((line = bufferReader.readLine()) != null) {
                outputStrings = line.split(csvDelimiter);
            }

            bufferReader.close();

            return outputStrings;
        } catch (Exception exception) {
            System.out.println("Something went wrong reading CSV file. Exception : " + exception);
            return null;
        }
    }

    /* Sorts raw CSV string array data. Converts it to a map of string Companies to TreeSets.
     * Where a TreeSet is a sorted map of People objects. */
    private static Map<String, TreeSet<Person>> sortRawCsvData(String[] input) {
        if (input.length % 5 != 0) {
            throw new IllegalArgumentException("Invalid CSV file. Unknown number of elements that isn't divisible by 5 (total person attributes).");
        }

        Map<String, TreeSet<Person>> output = new HashMap<>();
        // Assuming CSV is ordered UserId -> First Name -> Last Name -> Version -> Insurance
        int count = 0;
        for (int index = 0; index < input.length; index+=5) {
            // Create a new person object with CSV attributes
            String[] personCsv = Arrays.copyOfRange(input, index, index + 5);
            Person person = new Person(personCsv[0], personCsv[1], personCsv[2], Integer.parseInt(personCsv[3]), personCsv[4]);

            String company = personCsv[4];
            // Get the TreeSet that coresponds to the Company name if it exists
            TreeSet<Person> companyTree = output.get(company);
            if (companyTree == null) {
                // Entry didn't exist. So create a new TreeSet and add it
                companyTree = new TreeSet<Person>();
                output.put(company, companyTree);
            }

            // Add the Person object created from the CSV data to the TreeSet
            // This will automatically be sorted by the Person compareTo() + TreeSet
            companyTree.add(person);
        }

        return output;
    }

    private static void createCompanyCsvFile(Map<String, TreeSet<Person>> input) {
        for (Map.Entry<String, TreeSet<Person>> entry : input.entrySet()) {
            createCompanyCsvFile(entry.getKey(), entry.getValue());
        }
    }

    private static void createCompanyCsvFile(String company, TreeSet<Person> sortedPersons) {
        try {
            System.out.println("Attempting to create CSV file for " + company);
            String fileName = company + ".csv";
            CSVWriter csvWriter = new CSVWriter();
            FileWriter fileWriter = new FileWriter(new File(fileName));
            // Get the descending set from the TreeSet
            NavigableSet<Person> navigableSet = sortedPersons.descendingSet();
            // Get the descendingIterator of this navigableSet to get correct order
            Iterator<Person> itr = navigableSet.descendingIterator();
            // Store each person into the CSV file
            while (itr.hasNext()) {
                Person person = itr.next();
                fileWriter.write(person.getPersonAttributes());
            }

            fileWriter.close();
            System.out.println("File created.");
        } catch (Exception exception) {
            System.out.println("Something went wrong writing CSV file" + company + " . Exception : " + exception);
        }
    }
}
