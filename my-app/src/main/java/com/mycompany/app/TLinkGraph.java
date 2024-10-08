package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

public class TLinkGraph extends PlannerObject {
    private final DirectedGraph directedGraph;

    public TLinkGraph(int planStepNum) {
        this.directedGraph = new DirectedGraph(planStepNum);
    }

    public TLinkGraph(@NotNull TLinkGraph tLinkGraph) {
        this.directedGraph = new DirectedGraph(tLinkGraph.directedGraph);
    }

    public void addTLink(@NotNull TLink tLink) {
        directedGraph.addEdge(tLink.getBeforePlanStepIndex(), tLink.getAfterPlanStepIndex());
    }

    public void removeTLink(@NotNull TLink tLink) {
        directedGraph.removeEdge(tLink.getBeforePlanStepIndex(), tLink.getAfterPlanStepIndex());
    }

    public void setPlanStepNum(int planStepNum) {
        directedGraph.setV(planStepNum);
    }

    public boolean isCyclic() {
        return this.directedGraph.isCyclic();
    }

    public boolean hasPath(int from, int to) {
        return this.directedGraph.hasPath(from, to);
    }

    @Override
    public String toString() {
        return directedGraph.toString();
    }

    @Override
    public PlannerObject copy() {
        return new TLinkGraph(this);
    }

    @Override
    public String toStringIdentifier() {
        return this.directedGraph.toStringIdentifier();
    }
}
