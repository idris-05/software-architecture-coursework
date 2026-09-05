package calculatorApp.pipe;

// carrying data between adjacent components.
public class Pipe<T> {
    private T data;

    public void push(T data) {
        this.data = data;
    }

    public T pull() {
        // Consuming read: once pulled, the slot is emptied.
        T current = data;
        data = null;
        return current;
    }
}