public class Calculation extends Thread {
    private int startIndex;
    private int finishIndex;
    private NeuronNetwork populationNeuronNetwork[];
    private float errorNeuronNetwork[];
    private float inputTrainingData[][];
    private float outputTrainingData[][];
    private float inputTestData[][];
    private float outputTestData[][];
    private int maxCountLesson;

    public Calculation(int startIndex, int finishIndex, NeuronNetwork[] populationNeuronNetwork,
                       float[] errorNeuronNetwork, float[][] inputTrainingData,
                       float[][] outputTrainingData, float[][] inputTestData,
                       float[][] outputTestData, int maxCountLesson)
    {
        this.startIndex = startIndex;
        this.finishIndex = finishIndex;
        this.populationNeuronNetwork = populationNeuronNetwork;
        this.errorNeuronNetwork = errorNeuronNetwork;
        this.inputTrainingData = inputTrainingData;
        this.outputTrainingData = outputTrainingData;
        this.inputTestData = inputTestData;
        this.outputTestData = outputTestData;
        this.maxCountLesson = maxCountLesson;
    }

    @Override
    public void run()
    {
        while (startIndex <= finishIndex)
        {
            errorNeuronNetwork[startIndex] = populationNeuronNetwork[startIndex].learning(
                    inputTrainingData, outputTrainingData, inputTestData, outputTestData,
                    maxCountLesson);

            startIndex++;
        }
    }
}
