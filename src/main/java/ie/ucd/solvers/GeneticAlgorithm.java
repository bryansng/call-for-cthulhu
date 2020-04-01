package ie.ucd.solvers;

import ie.ucd.Common;
import ie.ucd.objects.*;

import java.util.*;

public class GeneticAlgorithm {

    HashMap<Integer, String> generateAllBitCodes(ArrayList<Student> students) {
        String currentBitCode = "";
        HashMap<Integer, String> allStudentsBitCode = new HashMap<Integer, String>(); //key-value = studentID-bitCode
        HashSet<String> usedBitCodes = new HashSet<String>();
        boolean isFirstIteration = true;
        for (Student student : students) {
            if (isFirstIteration) {
                currentBitCode = get1stBitCode();
                allStudentsBitCode.put(student.getId(), currentBitCode);
                usedBitCodes.add(currentBitCode);
                isFirstIteration = false;
            } else {
                String nextBitCode = getNextBitCode(currentBitCode);
                while (usedBitCodes.contains(nextBitCode))
                    nextBitCode = getNextBitCode(currentBitCode);
                allStudentsBitCode.put(student.getId(), nextBitCode);
                currentBitCode = nextBitCode;
            }
            if (Common.SHOW_GA_DEBUG)
                System.out.println("StudentID-BitCode : " + student.getId() + "-" + currentBitCode);
        }
        return allStudentsBitCode;
    }

    String get1stBitCode() {
        return "0000000000";
    }

    String getNextBitCode(String previousBitCode) {
        //pick random bit 0-9 to flip
        Random random = new Random();
        int randomIndex = random.nextInt(100) / 10;

        //char array makes replacing a bit more elegant and convenient
        char[] nextBitCode = previousBitCode.toCharArray();
        char[] flipTo = new char[1];

        //check what the randomly chosen bit is and flip it
        if (Character.compare(nextBitCode[randomIndex], '0') == 0) {
            flipTo[0] = '1';
            nextBitCode[randomIndex] = flipTo[0];
        } else if (Character.compare(nextBitCode[randomIndex], '1') == 0) {
            flipTo[0] = '0';
            nextBitCode[randomIndex] = flipTo[0];
        }
        //parse char array to String and return
        return String.valueOf(nextBitCode);
    }

}
