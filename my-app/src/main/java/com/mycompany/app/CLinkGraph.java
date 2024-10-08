package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

public class CLinkGraph extends PlannerObject {
    private final DirectedGraph directedGraph;

    public CLinkGraph(int planStepNum) {
        this.directedGraph = new DirectedGraph(planStepNum);
    }

    public CLinkGraph(@NotNull CLinkGraph cLinkGraph) {
        this.directedGraph = new DirectedGraph(cLinkGraph.directedGraph);
    }

    public void addCLink(@NotNull CLink cLink) {
        directedGraph.addEdge(cLink.getFrom().getPlanStepIndex(), cLink.getTo().getPlanStepIndex());
    }

    public void removeCLink(@NotNull CLink cLink) {
        directedGraph.removeEdge(cLink.getFrom().getPlanStepIndex(), cLink.getTo().getPlanStepIndex());
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
        return new CLinkGraph(this);
    }

    @Override
    public String toStringIdentifier() {
        return this.directedGraph.toStringIdentifier();
    }
}

