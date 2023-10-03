package nordnetservice.dto;

public class Tuple3<T1,T2,T3> {
    private final T1 _first;
    private final T2 _second;
    private final T3 _third;
    public Tuple3(T1 first, T2 second, T3 third) {
        this._first = first;
        this._second = second;
        this._third = third;
    }
    public T1 first() {
        return _first;
    }
    public T2 second() {
        return _second;
    }
    public T3 third() {
        return _third;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tuple3)) {
            return false;
        }
        Tuple3<T1,T2,T3> other = (Tuple3<T1,T2,T3>)obj;

        return ((_first.equals(other.first())) &&
                (_second.equals(other.second())) &&
                (_third.equals(other.third())));
    }
    @Override
    public int hashCode() {
        return (_first.hashCode() ^ _second.hashCode() ^ _third.hashCode());
    }
}
