package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PlannerList<T extends PlannerObject> extends PlannerObject implements Collection<T> {
    private final ArrayList<T> arrayList;

    public PlannerList(@NotNull Collection<? extends T> collection) {
        this.arrayList = new ArrayList<>();
        for (T item : collection) {
            this.arrayList.add((T) item.copy());
        }
    }

    public PlannerList() {
        this.arrayList = new ArrayList<>();
    }

    public PlannerList<T> copy() {
        PlannerList<T> copy = new PlannerList<>();
        for (T entry : this.arrayList) {
            copy.add((T) entry.copy());
        }
        return copy;
    }

    public boolean add(@NotNull T t) {
        return this.arrayList.add((T) t.copy());
    }

    @Override
    public boolean remove(@NotNull Object o) {
        return this.arrayList.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return false;
    }

    public boolean addAll(@NotNull Collection<? extends T> collection) {
        boolean collector = false;
        for (T element : collection) {
            if (this.add(element)) {
                collector = true;
            }
        }
        return collector;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return this.arrayList.removeAll(c);
    }

    @Override
    public boolean removeIf(@NotNull Predicate<? super T> filter) {
        return this.arrayList.removeIf(filter);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return this.arrayList.retainAll(c);
    }

    public void set(int index, @NotNull T t) {
        this.arrayList.set(index, (T) t.copy());
    }

    public void clear() {
        this.arrayList.clear();
    }

    public void remove(int index) {
        this.arrayList.remove(index);
    }

    public void sort(@NotNull Comparator<? super T> comparator) {
        this.arrayList.sort(comparator);
    }

    public int size() {
        return this.arrayList.size();
    }

    @Override
    public boolean isEmpty() {
        return this.arrayList.isEmpty();
    }

    @Override
    public boolean contains(@NotNull Object o) {
        return this.arrayList.contains(o);
    }

    public T get(int index) {
        return this.arrayList.get(index);
    }

    public T getLast() {
        return this.arrayList.get(this.arrayList.size() - 1);
    }

    public PlannerList<T> subList(int fromIndex, int toIndex) {
        PlannerList<T> sublist = new PlannerList<>();
        sublist.addAll(this.arrayList.subList(fromIndex, toIndex));
        return sublist;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PlannerList<?> plannerList)) {
            return false;
        }
        if (this.size() != plannerList.size()) {
            return false;
        }
        for (int i = 0; i < this.size(); i = i + 1) {
            if (!this.get(i).equals(plannerList.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return toString("{", "}", ", ");
    }

    public String toString(String start, String end, String sep) {
        StringBuilder out = new StringBuilder(start);
        for (int i = 0; i < this.size() - 1; i = i + 1) {
            out.append(this.get(i)).append(sep);
        }
        if (this.size() > 0) {
            out.append(this.get(this.size() - 1));
        }
        out.append(end);
        return out.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return this.arrayList.iterator();
    }

    @Override
    public void forEach(@NotNull Consumer<? super T> action) {
        this.arrayList.forEach(action);
    }

    @Override
    public Object[] toArray() {
        return this.arrayList.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1 @NotNull [] a) {
        return this.arrayList.toArray(a);
    }

    @Override
    public <T1> T1[] toArray(IntFunction<T1[]> generator) {
        return this.arrayList.toArray(generator);
    }

    @Override
    public Spliterator<T> spliterator() {
        return this.arrayList.spliterator();
    }

    @Override
    public Stream<T> stream() {
        return this.arrayList.stream();
    }

    @Override
    public Stream<T> parallelStream() {
        return this.arrayList.parallelStream();
    }

    public String toStringIdentifier() {
        StringBuilder out = new StringBuilder("{");
        for (int i = 0; i < this.size(); i = i + 1) {
            out.append(this.get(i).toStringIdentifier()).append(",");
        }
        if (this.size() > 0) {
            out.append(this.get(this.size() - 1).toStringIdentifier());
        }
        out.append("}");
        return out.toString();
    }

    public void removeLast() {
        this.arrayList.remove(this.arrayList.size()-1);
    }
}
