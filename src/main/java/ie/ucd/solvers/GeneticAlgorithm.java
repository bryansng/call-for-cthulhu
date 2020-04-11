package ie.ucd.solvers;

import ie.ucd.Common;
import ie.ucd.objects.Project;
import ie.ucd.objects.Student;

import java.util.*;

public class GeneticAlgorithm {
    private double mutationChance;
    private double crossoverChance;
    private int numberOfGenerations;
    private int sizeOfPopulation;
    private final ArrayList<Project> projects;
    private final ArrayList<Student> students;
    private ArrayList<Student> finalSolution = new ArrayList<Student>();

    private final Random random = new Random();

    public GeneticAlgorithm(ArrayList<Project> projects, ArrayList<Student> students) {
        this.mutationChance = 0.08;
        this.crossoverChance = 0.4;
        this.numberOfGenerations = 125;
        this.sizeOfPopulation = 75;
        this.projects = projects;
        this. students = students;
    }

    public double getMutationChance() {
        return mutationChance;
    }

    public void setMutationChance(double mutationChance) {
        this.mutationChance = mutationChance;
    }

    public double getCrossoverChance() {
        return crossoverChance;
    }

    public void setCrossoverChance(double crossoverChance) {
        this.crossoverChance = crossoverChance;
    }

    public int getNumberOfGenerations() {
        return numberOfGenerations;
    }

    public void setNumberOfGenerations(int numberOfGenerations) {
        this.numberOfGenerations = numberOfGenerations;
    }

    public int getSizeOfPopulation() {
        return sizeOfPopulation;
    }

    public void setSizeOfPopulation(int sizeOfPopulation) {
        this.sizeOfPopulation = sizeOfPopulation;
    }

    public ArrayList<Student> getFinalSolution() {
        return finalSolution;
    }

    public void run() {
        ArrayList<Student> studentSolution = new ArrayList<Student>();
        //generate bit codes to represent chromosomes
        ArrayList<String> allBitCodes = generateAllBitCodes(students.size());
        //generate initial population for generation 0
        ArrayList<String> population = generateInitialPopulation(allBitCodes);
        ArrayList<String> nextPopulation = new ArrayList<String>();
        int bestFitnessOfAllGenerations = 0;
        String bestSolutionOfAllGenerations = "";

        boolean isLastGeneration = false;
        for (int i = 1; i <= numberOfGenerations; i++) {
            if (i == numberOfGenerations)
                isLastGeneration = true;
            if (Common.SHOW_GA_DEBUG)
                System.out.println("Generation: " + i);

            //store satisfaction for each solution in a population
            ArrayList<Double> globalSatisfactionList = new ArrayList<Double>();
            for (String aSolution : population) {
                studentSolution = assignProjectsFromSolution(students, projects, aSolution);
                double globalSatisfaction = calculateGlobalSatisfaction();
                if (Common.SHOW_GA_DEBUG)
                    System.out.println("globalSatisfaction: " + globalSatisfaction);
                globalSatisfactionList.add(globalSatisfaction);
            }

            //get fittest solution from population
            int fittestSolutionIndex = getFittestSolutionIndex(globalSatisfactionList);
            String fittestSolution = population.get(fittestSolutionIndex);
            finalSolution = assignProjectsFromSolution(students, projects, fittestSolution);

            if (Common.SHOW_GA_DEBUG) {
                System.out.println("Fittest solution strength : " + globalSatisfactionList.get(fittestSolutionIndex));
            }

            if (!isLastGeneration) {
                //generate population for next generation
                while (nextPopulation.size() <= sizeOfPopulation) {
                    String[] parents = chooseParents(population, globalSatisfactionList);
                    String offspring = crossover(parents[0], parents[1]);
                    if (!offspring.equals("")) {
                        offspring = mutate(offspring);
                        nextPopulation.add(offspring);
                    }
                }
            } else
                System.out.println("GA complete.");
            population.clear();
            population = new ArrayList<String>(nextPopulation);
        }
    }

    private int getFittestSolutionIndex(ArrayList<Double> globalSatisfactionList) {
        double max = 0.0;
        int maxIndex = 0;
        int index = 0;
        for (double globalSatisfaction : globalSatisfactionList) {
            if (globalSatisfaction > max) {
                max = globalSatisfaction;
                maxIndex = index;
            }
            index++;
        }
        return maxIndex;
    }


    private String[] chooseParents(ArrayList<String> population, ArrayList<Double> globalSatisfactionList) {
        String[] parents = {population.get(0), population.get(1)};
//        double max = globalSatisfactionList.get(0), secondMax = globalSatisfactionList.get(1);
//        //convert to array to make process easier
//        String[] populationArray = populationToArray(population);
//        double[] satisfactionArray = satisfactionToArray(globalSatisfactionList);
//        //get two fittest parents
//        if (Common.SHOW_GA_DEBUG) {
//            System.out.println(satisfactionArray[0]);
//            System.out.println(satisfactionArray[3]);
//            if (Common.SHOW_GA_DEBUG) {
//                System.out.println(populationArray[0]);
//            }
//        }
//        for (int i = 0; i < 50; i++) {
//            if (satisfactionArray[i] > max) {
//                if (Common.SHOW_GA_DEBUG) {
//                    System.out.println("from choose parents: " + satisfactionArray[i]);
//                }
//                secondMax = max;
//                parents[1] = parents[0];
//                max = satisfactionArray[i];
//                parents[0] = populationArray[i];
//            } else if (satisfactionArray[i] > secondMax && satisfactionArray[i] < max) {
//                secondMax = satisfactionArray[i];
//                parents[1] = populationArray[i];
//            }
//        }
//        if (Common.SHOW_GA_DEBUG) {
//            System.out.println("Parent's strength: ");
//        }


        int randomIndex1 = random.nextInt(population.size() * 10) / 10;
        parents[0] = population.get(randomIndex1);
        int randomIndex2 = random.nextInt(population.size() * 10) / 10;
        while (randomIndex1 == randomIndex2) {
            randomIndex2 = random.nextInt(population.size() * 10) / 10;
        }
        parents[1] = population.get(randomIndex2);
        return parents;
    }

    private String[] populationToArray(ArrayList<String> population) {
        Object[] populationTemp = population.toArray();
        String[] populationArray = new String[Common.MAX_ARRAY_SIZE];
        int i = 0;
        for (Object object : populationTemp) {
            populationArray[i] = (String) object;
            i++;
        }
        return populationArray;
    }

    private double[] satisfactionToArray(ArrayList<Double> globalSatisfactionList) {
        Object[] globalSatisfactionTemp = globalSatisfactionList.toArray();
        double[] satisfactionArray = new double[Common.MAX_ARRAY_SIZE];
        int i = 0;
        for (Object object : globalSatisfactionList) {
            satisfactionArray[i] = (double) object;
            if (Common.SHOW_GA_DEBUG)
                System.out.println("satisfaction-- " + satisfactionArray[i]);
            i++;
        }
        return satisfactionArray;
    }

    private Double calculateGlobalSatisfaction() {
        Double satisfaction = 0.0;
        for (Student student : students) {
            satisfaction += student.calculateSatisfaction();
        }
        for (Project project : projects) {
            satisfaction += project.calculateSatisfaction();
        }
        return satisfaction;
    }

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

    public String mutate(String aSolution) {
        char[] solutionArray = aSolution.toCharArray();

        //use mutation probability
        int bound = (int) (mutationChance * 1000);
        int probabilityIndex = random.nextInt(1000);

        if (probabilityIndex < bound) {
            int randomIndex = random.nextInt(aSolution.length() * 10) / 10;
            if (solutionArray[randomIndex] == '0')
                solutionArray[randomIndex] = '1';
            else
                solutionArray[randomIndex] = '0';
        }

        return String.valueOf(solutionArray);
    }

    public String crossover(String parentA, String parentB) {
        String offspring = "";
        //use crossover probability
        int bound = (int) (crossoverChance * 1000);
        int probabilityIndex = random.nextInt(1000);
        if (probabilityIndex < bound) {
            int crossoverIndex = random.nextInt(parentA.length() * 10) / 10;
            offspring = parentA.substring(0, crossoverIndex).concat(parentB.substring(crossoverIndex));
        }
        //if crossover didn't occur offspring will be a blank String
        return offspring;
    }

    public ArrayList<String> generateInitialPopulation(ArrayList<String> allBitCodes) {
        HashSet<Integer> orderOfBitCodes = new HashSet<Integer>();
        String aSolution = "";
        ArrayList<String> population = new ArrayList<String>();

        //generate population by generating a random order for bit codes
        //and then using that order to generate a solution string
        for (int i = 0; i < sizeOfPopulation; i++) {
            orderOfBitCodes = generateOrderOfBitCodes(allBitCodes.size());
            aSolution = generateCandidateSolution(allBitCodes, orderOfBitCodes);
            aSolution = mutate(aSolution);
            while (population.contains(aSolution)) {
                orderOfBitCodes = generateOrderOfBitCodes(allBitCodes.size());
                aSolution = generateCandidateSolution(allBitCodes, orderOfBitCodes);
                aSolution = mutate(aSolution);
            }
            population.add(aSolution);
        }
        return population;
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
        HashSet<Integer> orderOfBitCodes = new HashSet<Integer>();
        //generate a random arrangement pattern for bit codes in a solution
//        for (int i = 0; i < numberOfBitCodes; i++) {
//            randomIndex = random.nextInt(numberOfBitCodes * 10) / 10;
//            while (orderOfBitCodes.contains(randomIndex))
//                randomIndex = random.nextInt(numberOfBitCodes * 10) / 10;
//            orderOfBitCodes.add(randomIndex);
//        }
        return orderOfBitCodes;
    }

    public ArrayList<Student> assignProjectsFromSolution(ArrayList<Student> students, ArrayList<Project> projects, String aSolution) {
        int[] studentRankings = getRanking(aSolution, students.size());
        ArrayList<Student> assignedStudents = new ArrayList<Student>();
        ArrayList<Student> unassignedStudents = new ArrayList<Student>();

        for (int rank = 1; rank <= students.size(); rank++) {
            for (int j = 0; j < studentRankings.length; j++) {
                if (rank == studentRankings[j]) {
                    Student currentStudent = students.get(j);
                    ArrayList<Project> currentPrefList = currentStudent.getPreferenceList();
                    boolean isAssigned = false;
                    for (Project project : currentPrefList) {
                        if (project.getNumStudentsAssigned() > 0)
                            continue;
                        project.incrementStudentsAssigned();
                        currentStudent.setProjectAssigned(project, 0);
                        isAssigned = true;
                    }
                    if (isAssigned)
                        assignedStudents.add(currentStudent);
                    else
                        unassignedStudents.add(currentStudent);
                }
            }
        }

        for (Student student : unassignedStudents) {
            Project randomProject = projects.get(random.nextInt(projects.size()));
            while (randomProject.getNumStudentsAssigned() > 0) {
                randomProject = projects.get(random.nextInt(projects.size()));
            }
            student.setProjectAssigned(randomProject, 0);
            assignedStudents.add(student);
        }
        return assignedStudents;
    }

    public int[] getRanking(String aSolution, int numberOfBitCodes) {
        int low = 0, high = 10, offset = high - low;
        int[] solutionInDecimal = new int[numberOfBitCodes];

        for (int i = 0; i < numberOfBitCodes; i++) {
            solutionInDecimal[i] = toDecimal(aSolution.substring(low, high));
            low += offset;
            high += offset;
        }

        int[] ranking = new int[numberOfBitCodes];
        for (int i = 0; i < solutionInDecimal.length; i++) {
            int count = 0;
            for (int j = 0; j < solutionInDecimal.length; j++) {
                if (solutionInDecimal[j] > solutionInDecimal[i]) {
                    count++;
                }
            }
            ranking[i] = count + 1;
        }
        return ranking;
    }
}
