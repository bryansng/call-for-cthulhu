package ie.ucd.solvers;

import ie.ucd.Common;
import ie.ucd.interfaces.GeneticAlgorithmInterface;
import ie.ucd.objects.Project;
import ie.ucd.objects.Student;

import java.util.*;

public class GeneticAlgorithm implements GeneticAlgorithmInterface {
    private double mutationChance;
    private double crossoverChance;
    private double pickFittestParentsChance;
    private int numberOfGenerations;
    private int sizeOfPopulation;
    private ArrayList<Student> finalSolution = new ArrayList<Student>();
    private double finalSolutionFitness;

    private final Random random = new Random();

    public GeneticAlgorithm() {
        this.mutationChance = 0.05;
        this.crossoverChance = 0.4;
        this.numberOfGenerations = 125;
        this.sizeOfPopulation = 50;
        this.pickFittestParentsChance = 0.85;
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

    public double getFinalSolutionFitness() {
        return finalSolutionFitness;
    }

    public void run(ArrayList<Project> projects, ArrayList<Student> students) {
        System.out.println("Running Genetic Algorithm...");
        //generate bit codes to represent chromosomes
        ArrayList<String> allBitCodes = generateAllBitCodes(students.size());
        //generate population for generation 0
        ArrayList<String> population = generateInitialPopulation(allBitCodes);
        ArrayList<String> nextPopulation = new ArrayList<String>();
        boolean isLastGeneration = false;

        for (int i = 1; i <= numberOfGenerations; i++) {
            if (i == numberOfGenerations) {
                isLastGeneration = true;
            }
            System.out.println("Creating Generation #" + i);
            //store satisfaction for each solution in a population
            ArrayList<Double> globalSatisfactionList = new ArrayList<Double>();

            for (String aSolution : population) {
                students = assignProjectsFromSolution(aSolution, projects, students);
                ArrayList<Project> updatedProjects = updateProjects(projects, students.size());
                double globalSatisfaction = calculateGlobalSatisfaction(updatedProjects, students);
                if (Common.SHOW_GA_DEBUG)
                    System.out.println("globalSatisfaction: " + globalSatisfaction);
                globalSatisfactionList.add(globalSatisfaction);
            }

            //get fittest solution from population
            int fittestSolutionIndex = getFittestSolutionIndex(globalSatisfactionList);
            double fittestSolutionStrength = globalSatisfactionList.get(fittestSolutionIndex);
            String fittestSolution = population.get(fittestSolutionIndex);
            if (Common.SHOW_GA_DEBUG) {
                System.out.println("Fittest solution strength: " + fittestSolutionStrength);
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
            } else {
                //get best final solution from final generation
                finalSolutionFitness = globalSatisfactionList.get(fittestSolutionIndex);
                finalSolution = assignProjectsFromSolution(fittestSolution, projects, students);
                System.out.println("Genetic Algorithm simulation complete.");
            }
            population.clear();
            population = new ArrayList<String>(nextPopulation);
            nextPopulation.clear();
        }
    }

    private String[] chooseParents(ArrayList<String> population, ArrayList<Double> globalSatisfactionList) {
        String[] parents = new String[2];
        double max = 1.0, secondMax = 1.0; //arbitrary positive values
        //convert to array to make process easier
        String[] populationArray = populationToArray(population);
        double[] satisfactionArray = satisfactionToArray(globalSatisfactionList);
        int bound = (int) pickFittestParentsChance * 1000;
        int probabilityIndex = random.nextInt(1000);

        if (probabilityIndex < bound) {
            //get two fittest parents
            for (int i = 0; i < sizeOfPopulation; i++) {
                if (satisfactionArray[i] > max) {
                    secondMax = max;
                    parents[1] = parents[0];
                    max = satisfactionArray[i];
                    parents[0] = populationArray[i];
                } else if (satisfactionArray[i] > secondMax && satisfactionArray[i] < max) {
                    secondMax = satisfactionArray[i];
                    parents[1] = populationArray[i];
                }
            }
        } else {
            int index = 0;
            //get the fittest and a random parent to encourage diversity
            for (int i = 0; i < sizeOfPopulation; i++) {
                if (satisfactionArray[i] > max) {
                    max = satisfactionArray[i];
                    parents[0] = populationArray[i];
                    index = i;
                }
            }

            int randomIndex = random.nextInt(sizeOfPopulation);
            while (randomIndex == index) {
                randomIndex = random.nextInt(sizeOfPopulation);
            }
            parents[1] = populationArray[randomIndex];
        }
        if (Common.SHOW_GA_DEBUG) {
            System.out.println("Parent's strength: " + max + " " + secondMax);
        }
        return parents;
    }

    private String crossover(String parentA, String parentB) {
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

    private String mutate(String aSolution) {
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

    private Double calculateGlobalSatisfaction(ArrayList<Project> projects, ArrayList<Student> students) {
        Double satisfaction = 0.0;
        for (Student student : students) {
            satisfaction += student.calculateSatisfaction();
        }
        for (Project project : projects) {
            satisfaction += project.calculateSatisfaction();
        }
        return satisfaction;
    }

    private ArrayList<String> generateAllBitCodes(int numberOfBitCodes) {
        String currentBitCode = get1stBitCode();
        ArrayList<String> allBitCodes = new ArrayList<String>();
        allBitCodes.add(currentBitCode);
        //generate remaining bit codes
        for (int i = 1; i < numberOfBitCodes; i++) {
            String nextBitCode = getNextBitCode(currentBitCode);
            //ensure new bit code is not a duplicate of an older one
            while (allBitCodes.contains(nextBitCode))
                nextBitCode = getNextBitCode(currentBitCode);
            allBitCodes.add(nextBitCode);
            currentBitCode = nextBitCode;
        }
        return allBitCodes;
    }

    private String get1stBitCode() {
        return "0000000000";
    }

    private String getNextBitCode(String previousBitCode) {
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

    private int toDecimal(String bitCode) {
        return Integer.parseInt(bitCode, 2);
    }

    private ArrayList<String> generateInitialPopulation(ArrayList<String> allBitCodes) {
        //create the first solution which usually has very good strength
        int[] orderOfBitCodes = get1stOrderOfBitCodes(allBitCodes.size());
        String aSolution = generateCandidateSolution(allBitCodes, orderOfBitCodes);
        aSolution = mutate(aSolution);
        //add first solution to population
        ArrayList<String> population = new ArrayList<String>();
        population.add(aSolution);


        //each following solution builds on the previous one by swapping around
        //any two random positions in orderOfBitCodes
        for (int i = 1; i < sizeOfPopulation; i++) {
            int[] previousOrder = orderOfBitCodes;
            orderOfBitCodes = getNextOrderOfBitCodes(previousOrder);
            aSolution = generateCandidateSolution(allBitCodes, orderOfBitCodes);
            aSolution = mutate(aSolution);
            while (population.contains(aSolution)) {
                orderOfBitCodes = getNextOrderOfBitCodes(orderOfBitCodes);
                aSolution = generateCandidateSolution(allBitCodes, orderOfBitCodes);
                aSolution = mutate(aSolution);
            }
            population.add(aSolution);
        }


        return population;
    }

    private String generateCandidateSolution(ArrayList<String> allBitCodes, int[] orderOfBitCodes) {
        String candidateSolution = "";
        for (int index : orderOfBitCodes) {
            candidateSolution = candidateSolution.concat(allBitCodes.get(index));
        }
        return candidateSolution;
    }

    private int[] get1stOrderOfBitCodes(int numberOfBitCodes) {
        int randomIndex;
        if (Common.SHOW_GA_DEBUG) {
            System.out.println("Generating first order");
        }
        int[] orderOfBitCodes = new int[numberOfBitCodes];
        HashSet<Integer> usedNumbers = new HashSet<Integer>();
        //generate a random arrangement pattern for bit codes in a solution
        for (int i = 0; i < numberOfBitCodes; i++) {
            randomIndex = random.nextInt(numberOfBitCodes * 10) / 10;
            while (usedNumbers.contains(randomIndex))
                randomIndex = random.nextInt(numberOfBitCodes * 10) / 10;
            orderOfBitCodes[i] = randomIndex;
            usedNumbers.add(randomIndex);
            if (Common.SHOW_GA_DEBUG) {
                System.out.println(randomIndex);
            }
        }
        return orderOfBitCodes;
    }

    private int[] getNextOrderOfBitCodes(int[] previousOrderOfBitCodes) {
        int randomIndex = random.nextInt(previousOrderOfBitCodes.length - 1);
        //swap number at randomIndex with the next one
        int temp = previousOrderOfBitCodes[randomIndex];
        previousOrderOfBitCodes[randomIndex] = previousOrderOfBitCodes[randomIndex + 1];
        previousOrderOfBitCodes[randomIndex + 1] = temp;
        return previousOrderOfBitCodes;
    }

    private ArrayList<Student> assignProjectsFromSolution(String aSolution, ArrayList<Project> projects, ArrayList<Student> students) {
        int[] studentRankings = getRanking(aSolution, students.size());
        ArrayList<Student> assignedStudents = new ArrayList<Student>();
        ArrayList<Student> unassignedStudents = new ArrayList<Student>();
        HashSet<Project> usedProjects = new HashSet<Project>();

        for (int rank = 1; rank <= students.size(); rank++) {
            for (int j = 0; j < studentRankings.length; j++) {
                if (rank == studentRankings[j]) {
                    Student currentStudent = students.get(j);
                    ArrayList<Project> currentPrefList = currentStudent.getPreferenceList();
                    boolean isAssigned = false;
                    for (Project project : currentPrefList) {
                        if (usedProjects.contains(project))
                            continue;
                        currentStudent.setProjectAssigned(project, 0);
                        usedProjects.add(project);
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
            while (usedProjects.contains(randomProject)) {
                randomProject = projects.get(random.nextInt(projects.size()));
            }
            student.setProjectAssigned(randomProject, 0);
            assignedStudents.add(student);
        }
        return assignedStudents;
    }

    private int[] getRanking(String aSolution, int numberOfBitCodes) {
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
        for (Object object : globalSatisfactionTemp) {
            satisfactionArray[i] = (double) object;
            if (Common.SHOW_GA_DEBUG)
                System.out.println("satisfactionFromArray-- " + satisfactionArray[i]);
            i++;
        }
        return satisfactionArray;
    }

    private ArrayList<Project> updateProjects(ArrayList<Project> projects, int numberOfProjectsAssigned) {
        /* this function was introduced with abstraction in mind
        / essentially, to calculate fitness, we do not need to know which project
        / was given to whom it only matters how many projects were or were not assigned
        / ArrayList of Student objects returned by run() already contains the
        / assigned Project object */
        ArrayList<Project> updatedProjects = new ArrayList<Project>();
        int i;
        for (i = 0; i < numberOfProjectsAssigned; i++) {
            Project project = projects.get(i);
            project.setNumStudentsAssigned(1);
            updatedProjects.add(project);
        }
        while (i < projects.size()) {
            Project project = projects.get(i);
            project.setNumStudentsAssigned(0);
            updatedProjects.add(project);
            i++;
        }
        return updatedProjects;
    }
}