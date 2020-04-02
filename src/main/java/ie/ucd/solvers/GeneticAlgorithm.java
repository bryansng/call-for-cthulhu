package ie.ucd.solvers;

import ie.ucd.Common;
import ie.ucd.interfaces.*;

import java.lang.reflect.Array;
import java.util.*;

public class GeneticAlgorithm {

    public ArrayList<String> generateAllBitCodes(int numberOfBitCodes) {
        String currentBitCode = get1stBitCode();
        ArrayList<String> allBitCodes = new ArrayList<String>();
        allBitCodes.add(currentBitCode);

        if (Common.SHOW_GA_DEBUG) {
            System.out.println("1 : " + currentBitCode);
        }

        //generate remaining bit codes
        for (int i = 1; i < numberOfBitCodes; i++) {
            String nextBitCode = getNextBitCode(currentBitCode);

            //ensure new bit code is not a duplicate of an older one
            while (allBitCodes.contains(nextBitCode))
                nextBitCode = getNextBitCode(currentBitCode);

            allBitCodes.add(nextBitCode);
            currentBitCode = nextBitCode;

            if (Common.SHOW_GA_DEBUG) {
                System.out.println(i + 1 + " : " + currentBitCode);
            }
        }
        return allBitCodes;
    }

    public String get1stBitCode() {
        return "0000000000";
    }

    public String getNextBitCode(String previousBitCode) {
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

    public int toDecimal(String bitCode) {
        return Integer.parseInt(bitCode, 2);
    }

    public ArrayList<String> generateInitialPopulation(int sizeOfPopulation, ArrayList<String> allBitCodes) {
        Random random = new Random();
        HashSet<Integer> orderOfBitCodes = new HashSet<Integer>();
        String aSolution = "";
        ArrayList<String> initialPopulation = new ArrayList<String>();

        //generate population by generating a random order for bit codes
        //and then using that order to generate a solution
        for (int i = 0; i < sizeOfPopulation; i++) {
            orderOfBitCodes = generateOrderOfBitCodes(allBitCodes.size());
            aSolution = generateCandidateSolution(allBitCodes, orderOfBitCodes);
            while (initialPopulation.contains(aSolution)) {
                orderOfBitCodes = generateOrderOfBitCodes(allBitCodes.size());
                aSolution = generateCandidateSolution(allBitCodes, orderOfBitCodes);
            }
            initialPopulation.add(aSolution);
        }

        return initialPopulation;
    }

    public String generateCandidateSolution(ArrayList<String> allBitCodes, HashSet<Integer> orderOfBitCodes) {
        String candidateSolution = "";
        for (int index : orderOfBitCodes) {
            candidateSolution = candidateSolution.concat(allBitCodes.get(index));
        }
        return candidateSolution;
    }

    public HashSet<Integer> generateOrderOfBitCodes(int numberOfBitCodes) {
        int randomIndex;
        Random random = new Random();
        HashSet<Integer> orderOfBitCodes = new HashSet<Integer>();
        //generate a random arrangement pattern for bit codes in a solution
        for (int j = 0; j < numberOfBitCodes; j++) {
            randomIndex = random.nextInt(numberOfBitCodes * 10) / 10;
            while (orderOfBitCodes.contains(randomIndex))
                randomIndex = random.nextInt(numberOfBitCodes * 10) / 10;
            orderOfBitCodes.add(randomIndex);
        }
        return orderOfBitCodes;
    }


}
