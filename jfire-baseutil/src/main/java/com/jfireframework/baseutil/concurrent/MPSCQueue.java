package com.jfireframework.baseutil.concurrent;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

/**
 * Created by 林斌 on 2016/9/10.
 */
public class MPSCQueue<E> implements Queue<E> {
    private volatile MPSCNode<E> head;
    private volatile MPSCNode<E> tail;
    private static final UnsafeReferenceFieldUpdater<MPSCQueue, MPSCNode> tailUpdater = new UnsafeReferenceFieldUpdater<MPSCQueue, MPSCNode>(MPSCQueue.class, "tail");

    @Override
    public E remove() {
        return poll();
    }

    @Override
    public E poll() {
        MPSCNode<E> node = peekNode();
        if (node != null) {
            head.next = null;
            head = node;
            return node.value;
        } else {
            return null;
        }
    }

    @Override
    public E element() {
        return peek();
    }

    @Override
    public E peek() {
        MPSCNode<E> node = peekNode();
        if (node != null) {
            return node.value;
        } else {
            return null;
        }
    }

    private MPSCNode<E> peekNode() {
        if (head != tail) {
            while (head == null || head.next == null) {
                ;
            }
            return head.next;
        } else {
            return null;
        }
    }

    private final static class MPSCNode<E> {
        private final E value;
        private volatile MPSCNode<E> next;

        public MPSCNode(E value) {
            this.value = value;
        }

        public E value() {
            return value;
        }

    }


    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return head == tail;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }


    @Override
    public Iterator iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray(Object[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(E o) {
        return offer(o);
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        for (E o : c) {
            offer(o);
        }
        return true;
    }


    @Override
    public void clear() {
        head = tail;
    }

    @Override
    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection c) {
        return false;
    }

    @Override
    public boolean containsAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean offer(E o) {
        if (o == null) {
            throw new NullPointerException();
        }
        if (tail == null) {
            MPSCNode init_tail = new MPSCNode(null);
            if (tailUpdater.compareAndSwap(this, null, init_tail)) {
                head = init_tail;
            }
        }
        MPSCNode<E> new_tail = new MPSCNode<E>(o);
        MPSCNode<E> old_tail = tail;
        if (tailUpdater.compareAndSwap(this, old_tail, new_tail)) {
            old_tail.next = new_tail;
            return true;
        }
        while (true) {
            old_tail = tail;
            if (tailUpdater.compareAndSwap(this, old_tail, new_tail)) {
                break;
            }
        }
        old_tail.next = new_tail;
        return true;
    }


}
