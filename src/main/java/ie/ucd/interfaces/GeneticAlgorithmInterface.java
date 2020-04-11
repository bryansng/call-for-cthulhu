package ie.ucd.interfaces;

import ie.ucd.objects.Project;
import ie.ucd.objects.Student;

import java.util.ArrayList;

public interface GeneticAlgorithmInterface {
    public void run(ArrayList<Project> projects, ArrayList<Student> students);

    public double getMutationChance();

    public void setMutationChance(double mutationChance);

    public double getCrossoverChance();

    public void setCrossoverChance(double crossoverChance);

    public int getNumberOfGenerations();

    public void setNumberOfGenerations(int numberOfGenerations);

    public int getSizeOfPopulation();

    public void setSizeOfPopulation(int sizeOfPopulation);
}
