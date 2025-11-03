package core.userDefinedTask.internals;

public class SharedVariablesSubscriber {

	private final SharedVariablesSubscription subscription;
	private final ProcessingFunction f;

	private SharedVariablesSubscriber(SharedVariablesSubscription subscription, ProcessingFunction f) {
		this.subscription = subscription;
		this.f = f;
	}

	public static SharedVariablesSubscriber of(SharedVariablesSubscription subscription, ProcessingFunction f) {
		return new SharedVariablesSubscriber(subscription, f);
	}

	public void processEvent(SharedVariablesEvent e) {
		if (!subscription.includes(e)) {
			return;
		}

		f.process(e);
	}

	public interface ProcessingFunction {
		void process(SharedVariablesEvent e);
	}
}
