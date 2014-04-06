package com.loadimpact.teamcity_plugin.eval;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A bounded queue, that drops old elements when full.
 *
 * @author jens
 */
@SuppressWarnings("unchecked")
@Deprecated
public class BoundedDroppingQueue <ElementType> implements Iterable<ElementType> {
    private static int defaultSize = 1;
    private final Object[] elements;
    private int putIdx = 0;
    private int getIdx = 0;
    private int size   = 0;

    /**
     * Creates the queue with the given capacity.
     * @param size  the (fixed) capacity
     */
    public BoundedDroppingQueue(int size) {
        elements = new Object[size];
    }

    public BoundedDroppingQueue() {
        this(defaultSize);
    }

    public static void setDefaultSize(int defaultSize) {
        BoundedDroppingQueue.defaultSize = defaultSize;
    }

    /**
     * Inserts an element and overwrites old one when full.
     * @param x     element to insert
     */
    public void put(ElementType x) {
        if (full()) {
            getIdx = (getIdx + 1) % elements.length;
        } else {
            size++;
        }

        elements[putIdx] = x;
        putIdx = (putIdx + 1) % elements.length;
    }

    /**
     * Removes the 'first' element.
     * @return first element in the queue
     * @throws IllegalArgumentException if empty
     */
    public ElementType get() {
        if (empty()) throw new IllegalArgumentException("Empty queue");

        ElementType x = (ElementType) elements[getIdx];
        getIdx = (getIdx + 1) % elements.length;
        size--;
        return x;
    }

    /**
     * Returns its current size
     * @return number of elements in the queue
     */
    public int size() {
        return size;
    }

    /**
     * Returns true if size==0
     * @return true if empty
     */
    public boolean empty() {
        return size() == 0;
    }

    /**
     * Returns true if size==capacity
     * @return true if full
     */
    public boolean full() {
        return size() == elements.length;
    }

    /**
     * Returns an iterator intended usage in a foreach loop
     * @return an iterator that iterates over all elements in the queue
     */
    public Iterator<ElementType> iterator() {
        return new Iterator<ElementType>() {
            int idx = getIdx;
            int N = size;

            public boolean hasNext() {
                return N > 0;
            }

            public ElementType next() {
                ElementType x = (ElementType) elements[idx];
                idx = (idx + 1) % elements.length;
                N--;
                return x;
            }

            public void remove() {
                throw new UnsupportedOperationException("remove");
            }
        };
    }

    /**
     * Returns a new list with all the elements in order 
     * @return a list
     */
    public List<ElementType> toList() {
        List<ElementType> result = new ArrayList<ElementType>(size());
        for (ElementType e : this) result.add(e);
        return result;
    }

    /**
     * Returns a string with the elements
     * @return string with all elements
     */
    public String toString() {
        return toList().toString();
    }

}
