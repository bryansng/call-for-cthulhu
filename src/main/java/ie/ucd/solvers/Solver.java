package ie.ucd.solvers;

import ie.ucd.objects.CandidateSolution;

public abstract class Solver implements Runnable {
	// http://tutorials.jenkov.com/java-concurrency/creating-and-starting-threads.html#pause-a-thread
	// Mainly from this one: https://stackoverflow.com/questions/27409708/how-can-i-interrupt-and-resume-a-thread-without-the-use-of-deprecated-methods
	protected volatile boolean isSuspended;
	protected volatile boolean isStopped;
	protected volatile boolean isOneStep;

	public void suspend() {
		this.isSuspended = true;
	}

	public synchronized void resume() {
		this.isSuspended = false;
		notify();
	}

	public synchronized void stop() {
		this.isStopped = true;
		notify();
	}

	public synchronized void oneStep() {
		this.isOneStep = true;
		this.isSuspended = false;
		notify();
	}

	protected synchronized void oneStepDone() {
		this.isSuspended = true;
		notify();
	}

	public void restart() {
		this.isSuspended = false;
		this.isStopped = false;
		this.isOneStep = false;
	}

	public abstract CandidateSolution getBestSolution();
}