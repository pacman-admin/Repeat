package core.userDefinedTask.manualBuild;

import core.languageHandler.compiler.ManualBuildNativeCompiler;
import utilities.json.JSONUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ManuallyBuildActionConstructor {

    private final List<ManuallyBuildStep> steps;

    private ManuallyBuildActionConstructor(List<ManuallyBuildStep> steps) {
        this.steps = steps;
    }

    public static ManuallyBuildActionConstructor of() {
        return new ManuallyBuildActionConstructor(new ArrayList<>());
    }

    public static ManuallyBuildActionConstructor of(ManuallyBuildAction action) {
        return new ManuallyBuildActionConstructor(new ArrayList<>(action.getSteps()));
    }

    public List<ManuallyBuildStep> getSteps() {
        return Collections.unmodifiableList(steps);
    }

    public void addStep(int index, ManuallyBuildStep step) {
        index = index + 1;
        if (index >= steps.size()) {
            index = steps.size() - 1;
        }
        if (index < 0) { // Edge case where list is empty.
            index = 0;
        }

        steps.add(index, step);
    }

    public void removeStep(int index) {
        if (index >= 0 && index < steps.size()) {
            steps.remove(index);
        }
    }

    public void moveStepUp(int index) {
        if (index <= 0 || index >= steps.size()) {
            return;
        }

        ManuallyBuildStep tmp = steps.get(index);
        steps.set(index, steps.get(index - 1));
        steps.set(index - 1, tmp);
    }

    public void moveStepDown(int index) {
        if (index < 0 || index >= steps.size()) {
            return;
        }

        ManuallyBuildStep tmp = steps.get(index);
        steps.set(index, steps.get(index + 1));
        steps.set(index + 1, tmp);
    }

    public String generateSource() {
        List<String> lines = Stream.concat(
                Stream.of(ManualBuildNativeCompiler.VERSION_PREFIX + ManualBuildNativeCompiler.VERSION),
                steps.stream().map(ManuallyBuildStep::jsonize).map(JSONUtility::jsonToSingleLineString)
        ).collect(Collectors.toList());
        return String.join("\n", lines);
    }
}
