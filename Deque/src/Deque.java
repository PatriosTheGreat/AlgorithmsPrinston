import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    public Deque() {
        _size = 0;
        _first = _last = null;
    }

    public boolean isEmpty() {
        return _first == null;
    }

    public int size() {
        return _size;
    }

    public void addFirst(Item item) {
        checkItem(item);
        if(isEmpty()) {
            _first = _last = new LinkedListNode(item);
            return;
        }

        LinkedListNode newFirst = new LinkedListNode(item);
        newFirst.setNext(_first);
        _first = newFirst;
        _size++;
    }

    public void addLast(Item item) {
        checkItem(item);
        if(isEmpty()) {
            _first = _last = new LinkedListNode(item);
            return;
        }

        LinkedListNode newLast = new LinkedListNode(item);
        newLast.setPrevious(_last);
        _last = newLast;
        _size++;
    }

    public Item removeFirst() {
        checkIsEmpty();
        Item item = _first.getItem();
        _first = _first.getNext();
        _size--;

        if(isEmpty()){
            _last = null;
        }

        return item;
    }

    public Item removeLast() {
        checkIsEmpty();
        Item item = _last.getItem();
        _last = _last.getPrevious();
        _size--;

        if(isEmpty()){
            _first = null;
        }

        return item;
    }

    @Override
    public Iterator<Item> iterator() {
        return new Iterator<Item>() {
            @Override
            public boolean hasNext() {
                return _currentNode != null;
            }

            @Override
            public Item next() {
                if(!hasNext()) {
                    throw new NoSuchElementException();
                }

                Item item = _currentNode.getItem();
                _currentNode = _currentNode.getNext();
                return item;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            private LinkedListNode _currentNode = _first;
        };
    }

    public static void main(String[] args) {
        Deque<Integer> deque = new Deque<>();
        deque.addFirst(3);
        deque.addFirst(4);
        deque.addLast(5);
        System.out.println(deque.removeLast());
        System.out.println(deque.removeLast());
        System.out.println(deque.removeFirst());
    }

    private void checkIsEmpty() {
        if(isEmpty()) {
            throw new NoSuchElementException();
        }
    }

    private void checkItem(Item item) {
        if(item == null) {
            throw new IllegalArgumentException();
        }
    }

    private LinkedListNode _first;
    private LinkedListNode _last;
    private int _size;

    private class LinkedListNode {
        public LinkedListNode(Item item) {
            _next = null;
            _previous = null;
            _item = item;
        }

        public LinkedListNode getNext() {
            return _next;
        }

        public LinkedListNode getPrevious() {
            return _previous;
        }

        public void setNext(LinkedListNode next) {
            _next = next;
        }

        public void setPrevious(LinkedListNode previous) {
            _previous = previous;
        }

        public Item getItem() {
            return _item;
        }

        private Item _item;
        private LinkedListNode _next;
        private LinkedListNode _previous;
    }
}
