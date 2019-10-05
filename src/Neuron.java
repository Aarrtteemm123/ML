import java.io.Serializable;

public class Neuron implements Serializable {
    private float result;
    private float input;
    private float error;
    private float deltaWeightArr[];
    private float weightArr[];

    public void setCountConnection(int countConnection) {
        weightArr = new float[countConnection];
        deltaWeightArr = new float[countConnection];
        for (int i = 0; i < weightArr.length; i++) {
            weightArr[i] = (float) (Math.random() - 0.5);
        }
    }

    public void resetWeight() {
        for (int i = 0; i < weightArr.length; i++) {
            weightArr[i] = (float) (Math.random() - 0.5);
        }
    }

    public void setInput(float input) {
        this.input = input;
    }

    public float getInput() {
        return input;
    }

    public void setWeightByIndex(int index, float deltaWeight) {
        weightArr[index] += deltaWeight;
    }

    public float getWeightByIndex(int index) {
        return weightArr[index];
    }

    public float getResult() {
        return result;
    }

    public void setResult(float result) {
        this.result = result;
    }

    public float getError() {
        return error;
    }

    public void setError(float error) {
        this.error = error;
    }

    public void setDeltaWeightByIndex(int index, float deltaWeight) {
        deltaWeightArr[index] = deltaWeight;
    }

    public float getDeltaWeightByIndex(int index) {
        return deltaWeightArr[index];
    }
}
