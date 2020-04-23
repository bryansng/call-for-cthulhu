package ie.ucd.objects;

import ie.ucd.interfaces.BitCodeSolutionInterface;

public class BitCodeSolution implements BitCodeSolutionInterface {
    private String solution;
    private double satisfaction;

    public BitCodeSolution(String solution) {
        this.solution = solution;
        this.satisfaction = 0.0;
    }

    public BitCodeSolution(String solution, double satisfaction) {
        this.satisfaction = satisfaction;
        this.solution = solution;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public double getSatisfaction() {
        return satisfaction;
    }

    public void setSatisfaction(double satisfaction) {
        this.satisfaction = satisfaction;
    }
}
