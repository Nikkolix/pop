package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DirectedGraph extends PlannerObject {
    //attributes
    private int V;
    private List<List<Integer>> adj;

    //constructors
    public DirectedGraph(int V) {
        this.V = V;
        adj = new ArrayList<>(V);

        for (int i = 0; i < V; i++)
            adj.add(new LinkedList<>());
    }

    public DirectedGraph(@NotNull DirectedGraph directedGraph) {
        this.V = directedGraph.V;
        this.adj = new ArrayList<>();
        for (List<Integer> next : directedGraph.adj) {
            this.adj.add(new LinkedList<>(next));
        }
    }

    //public methods
    public void addEdge(int source, int dest) {
        adj.get(source).add(dest);
    }

    public void removeEdge(int source, int dest) {
        List<Integer> next = adj.get(source);
        if (next.contains(dest)) {
            next.remove(next.indexOf(dest)); //ignore warning
        }
        adj.set(source, next);
    }

    public void setV(int V) {
        this.V = V;
        if (adj.size() > V) {
            adj = adj.subList(0, V);
            for (List<Integer> next : adj) {
                next.removeIf((i) -> i >= V);
            }
        }
        while (adj.size() < V) {
            adj.add(new LinkedList<>());
        }
    }

    public boolean isCyclic() {
        boolean[] visited = new boolean[V];
        boolean[] recStack = new boolean[V];

        for (int i = 0; i < V; i++)
            if (isCyclicUtil(i, visited, recStack))
                return true;

        return false;
    }

    public boolean hasPath(int from, int to) {
        boolean[] visited = new boolean[V];
        return hasPathUtil(visited, from, to);
    }

    private boolean hasPathUtil(boolean @NotNull [] visited, int from, int to) {
        if (visited[from]) {
            return false;
        }
        visited[from] = true;
        for (int next : adj.get(from)) {
            if (next == to) {
                return true;
            }
            if (hasPathUtil(visited, next, to)) {
                return true;
            }
        }

        return false;
    }

    //private methods
    private boolean isCyclicUtil(int i, boolean[] visited, boolean @NotNull [] recStack) {
        if (recStack[i])
            return true;

        if (visited[i])
            return false;
        visited[i] = true;
        recStack[i] = true;
        List<Integer> children = adj.get(i);

        for (Integer c : children)
            if (isCyclicUtil(c, visited, recStack))
                return true;

        recStack[i] = false;
        return false;
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < this.V; i = i + 1) {
            out.append(Integer.toString(i)).append(": ");
            for (Integer x : this.adj.get(i)) {
                out.append(Integer.toString(x)).append(",");
            }
            out.append("\r\n");
        }
        return out.toString();
    }

    @Override
    public PlannerObject copy() {
        return new DirectedGraph(this);
    }

    @Override
    public String toStringIdentifier() {
        PlannerList<PlannerList<PlannerInteger>> list = new PlannerList<>();
        for (List<Integer> next : this.adj) {
            list.add(new PlannerList<>());
            for (Integer n : next) {
                list.get(list.size() - 1).add(new PlannerInteger(n));
            }
        }
        return "[DG" + this.V + "/" + list.toStringIdentifier() + "]";
    }
}
