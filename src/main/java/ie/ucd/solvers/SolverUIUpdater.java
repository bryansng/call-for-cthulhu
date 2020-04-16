package ie.ucd.solvers;

import ie.ucd.Settings;
import ie.ucd.objects.CandidateSolution;
import ie.ucd.ui.common.sheets.StudentSheet;
import ie.ucd.ui.interfaces.VisualizerInterface;
import ie.ucd.ui.solver.SolverPane;

public interface SolverUIUpdater {
	// adds to queue, only when animation is enabled.
	default void uiAddToCurrQueueAnimate(StudentSheet currSheet, CandidateSolution currSolution) {
		if (Settings.enableAnimation && currSheet != null) {
			currSheet.addToQueue(currSolution);
		}
	}

	default void uiAddToBestQueueAnimate(StudentSheet bestSheet, CandidateSolution bestSolution) {
		if (Settings.enableAnimation && bestSheet != null) {
			bestSheet.addToQueue(bestSolution);
		}
	}

	// adds to queue, only when animation is disabled.
	default void uiAddToCurrQueueNoAnimate(StudentSheet currSheet, CandidateSolution currSolution) {
		if (!Settings.enableAnimation && currSheet != null) {
			currSheet.addToQueue(currSolution);
		}
	}

	default void uiAddToBestQueueNoAnimate(StudentSheet bestSheet, CandidateSolution bestSolution) {
		if (!Settings.enableAnimation && bestSheet != null) {
			bestSheet.addToQueue(bestSolution);
		}
	}

	default void uiSignalNewGraph(VisualizerInterface visualizer) {
		if (visualizer != null)
			visualizer.newSeries();
	}

	default void uiAddToGraph(VisualizerInterface visualizer, Double currEnergy, Double bestEnergy, Integer loopNumber) {
		if (visualizer != null)
			visualizer.addToQueue(currEnergy, bestEnergy, loopNumber);
	}

	default void uiAddToProgressIndicator(SolverPane solverPane, Double start, Double current, Double end) {
		if (solverPane != null) {
			solverPane.getProgressIndidactor().addToQueue(start, current, end);
		}
	}

	default void uiSignalProcessingDone(SolverPane solverPane) {
		if (solverPane != null) {
			solverPane.setDoneProcessing(true);
		}
	}
}