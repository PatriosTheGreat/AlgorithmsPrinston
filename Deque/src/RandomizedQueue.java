import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return _size;
    }

    public void enqueue(Item item) {
        if(item == null) {
            throw new IllegalArgumentException();
        }

        resizeIfNeeded();
        _items[_size] = item;
        _size++;
    }

    public Item dequeue() {
        checkIsEmpty();
        int randomIndex = StdRandom.uniform(size());

        Item randomItem = _items[randomIndex];
        Item last = _items[_size - 1];
        _items[randomIndex] = last;
        _size--;

        return randomItem;
    }

    public Item sample() {
        checkIsEmpty();

        return _items[StdRandom.uniform(size())];
    }

    @Override
    public Iterator<Item> iterator() {
        return new RandomQueueIterator();
    }

    public static void main(String[] args) {
        RandomizedQueue<Integer> queue = new RandomizedQueue<>();
        queue.enqueue(3);
        queue.enqueue(4);
        queue.enqueue(5);

        for (Integer item: queue) {
            System.out.println(item);
        }

        for (Integer item: queue) {
            System.out.println(item);
        }

        System.out.println(queue.sample());

        System.out.println(queue.dequeue());
        System.out.println(queue.dequeue());
        System.out.println(queue.dequeue());
    }

    private void checkIsEmpty() {
        if(isEmpty()) {
            throw new NoSuchElementException();
        }
    }

    private Item[] _items;
    private int _size = 0;

    private void resizeIfNeeded() {
        if(_items != null && _items.length > (_size + 1)) {
            return;
        }

        Item[] newArray = (Item[])new Object[_items == null ? 2 : (_items.length * 2)];

        for(int i = 0; i < size(); i++){
            newArray[i] = _items[i];
        }

        _items = newArray;
    }

    private class RandomQueueIterator implements Iterator<Item> {
        public RandomQueueIterator() {
            _indexesMap = new int[size()];
            for(int i = 0; i < size(); i++){
                _indexesMap[i] = i;
            }

            StdRandom.shuffle(_indexesMap);
        }

        @Override
        public boolean hasNext() {
            return _currentIndex < size();
        }

        @Override
        public Item next() {
            if(!hasNext()) {
                throw new NoSuchElementException();
            }

            Item next = _items[_indexesMap[_currentIndex]];
            _currentIndex++;
            return next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private int _currentIndex = 0;
        private int[] _indexesMap;
    }
}
