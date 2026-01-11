package core.userDefinedTask.manualBuild;

import core.background.AbstractBackgroundEntityManager;

public final class ManuallyBuildActionConstructorManager extends AbstractBackgroundEntityManager<ManuallyBuildActionConstructor> {

	public static ManuallyBuildActionConstructorManager of() {
		return new ManuallyBuildActionConstructorManager();
	}

	public String addNew() {
		return add(ManuallyBuildActionConstructor.of());
	}

	public String addNew(ManuallyBuildAction action) {
		return add(ManuallyBuildActionConstructor.of(action));
	}
}
