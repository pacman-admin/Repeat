package utilities;

import java.util.ArrayList;
import java.util.List;

public abstract class Function<D, R> {
    public abstract R apply(D d);

    public List<R> map(List<D> ds) {
        List<R> output = new ArrayList<>(ds.size());
        for (D d : ds) {
            output.add(this.apply(d));
        }
        return output;
    }
}
