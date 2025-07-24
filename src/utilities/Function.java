package utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Function<D, R> {
	public abstract R apply(D d);

	public List<R> map(Collection<D> ds) {
		List<R> output = new ArrayList<>(ds.size());
		for (D d : ds) {
			output.add(this.apply(d));
		}
		return output;
	}

	public List<R> map(D[] ds) {
		List<R> output = new ArrayList<>(ds.length);
		for (D d : ds) {
			output.add(this.apply(d));
		}
		return output;
	}

	public List<R> mapNotNull(Collection<D> ds) {
		List<R> output = new ArrayList<>(ds.size());
		for (D d : ds) {
			R r = this.apply(d);
			if (r != null) {
				output.add(r);
			}
		}
		return output;
	}

	public List<R> mapNotNull(D[] ds) {
		List<R> output = new ArrayList<>(ds.length);
		for (D d : ds) {
			R r = this.apply(d);
			if (r != null) {
				output.add(r);
			}
		}
		return output;
	}

	public Function<D, D> identity() {
		return new Function<>() {
            @Override
            public D apply(D d) {
                return d;
            }
        };
	}

	public static Function<Void, Void> nullFunction() {
		return new Function<>() {
            @Override
            public Void apply(Void r) {
                return null;
            }
        };
	}

	public static <E> Function<E, Boolean> trueFunction() {
		return new Function<>() {
            @Override
            public Boolean apply(E e) {
                return true;
            }
        };
	}

	public static <E> Function<E, Boolean> falseFunction() {
		return new Function<>() {
            @Override
            public Boolean apply(E e) {
                return false;
            }
        };
	}
}
