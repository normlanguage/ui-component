package dev.normlanguage.ui.component;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class Steps extends HBox {
    private final ObservableList<String> steps = FXCollections.observableArrayList();
    private final IntegerProperty currentStep = new SimpleIntegerProperty(this, "currentStep") {
        @Override public void set(int value) {
            if (value < 0 || (steps.isEmpty() ? value != 0 : value >= steps.size()))
                throw new IndexOutOfBoundsException(value);
            super.set(value);
        }
    };

    public Steps(String... titles) {
        super(8);
        getStyleClass().add("norm-steps");
        steps.addListener((javafx.collections.ListChangeListener<String>) change -> refresh());
        currentStep.addListener(observable -> refresh());
        steps.addAll(titles);
    }
    public ObservableList<String> getSteps() { return steps; }
    public IntegerProperty currentStepProperty() { return currentStep; }
    public int getCurrentStep() { return currentStep.get(); }
    public void setCurrentStep(int index) {
        if (steps.isEmpty()) throw new IndexOutOfBoundsException(index);
        currentStep.set(index);
    }
    private void refresh() {
        if (currentStep.get() >= steps.size()) currentStep.set(Math.max(0, steps.size() - 1));
        getChildren().clear();
        for (int i = 0; i < steps.size(); i++) {
            if (i > 0) getChildren().add(new Label("→"));
            var step = new Label((i + 1) + ". " + steps.get(i));
            step.getStyleClass().add(i < getCurrentStep() ? "norm-step-complete"
                    : i == getCurrentStep() ? "norm-step-current" : "norm-step-waiting");
            getChildren().add(step);
        }
    }
}
