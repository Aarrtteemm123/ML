import java.io.Serializable;
import java.util.ArrayList;

public class NeuronNetwork implements Serializable {
    private Neuron[][] layersNeuron;
    private Genome genome;
    private float percentErrorInTrainingData = 1;
    private float percentErrorInTestingData = 1;
    private float percentErrorInTestingDataArr[];
    private ArrayList<Neuron[][]> neuronNetwork;

    public NeuronNetwork(int[] countNeuronInLayers, Genome genome)
    {
        layersNeuron = new Neuron[countNeuronInLayers.length][];
        this.genome = genome;
        for (int i = 0; i < layersNeuron.length - 1; i++)
        {
            layersNeuron[i] = new Neuron[countNeuronInLayers[i]];
            for (int j = 0; j < layersNeuron[i].length; j++)
            {
                layersNeuron[i][j] = new Neuron();
                layersNeuron[i][j].setCountConnection(countNeuronInLayers[i + 1]);
            }
        }
        layersNeuron[layersNeuron.length - 1] =
                new Neuron[countNeuronInLayers[countNeuronInLayers.length - 1]];
        for (int i = 0; i < layersNeuron[layersNeuron.length - 1].length; i++)
        {
            layersNeuron[layersNeuron.length - 1][i] = new Neuron();
        }
    }

    public float learning(float inputTrainingData[][], float outputTrainingData[][],
                          float inputTestData[][], float outputTestData[][], int maxCountLesson) {
        float result[];
        percentErrorInTestingDataArr = new float[maxCountLesson];
        neuronNetwork = new ArrayList<>(maxCountLesson);
        for (int indexLesson = 0; indexLesson < maxCountLesson; indexLesson++)
        {
            resetNetWeight();
            for (int indexEpoch = 0; indexEpoch < genome.getCountEpoch(); indexEpoch++)
            {
                if(indexEpoch%1==0)
                    System.out.println(indexEpoch);
                mixData(inputTrainingData, outputTrainingData);

                for (int indexInputTrainingData = 0;
                     indexInputTrainingData < inputTrainingData.length;
                     indexInputTrainingData++)
                {
                    result = predict(inputTrainingData[indexInputTrainingData]);

                    for (int indexOutputNeuron = 0;
                         indexOutputNeuron < layersNeuron[layersNeuron.length - 1].length;
                         indexOutputNeuron++)
                    {
                        layersNeuron[layersNeuron.length - 1][indexOutputNeuron]
                                .setError((outputTrainingData[indexInputTrainingData]
                                        [indexOutputNeuron] - result[indexOutputNeuron])
                                        * activationFunctionDx(layersNeuron
                                        [layersNeuron.length - 1][indexOutputNeuron].getInput()));

                    }

                    for (int indexLayer = layersNeuron.length - 2; indexLayer > -1; indexLayer--)//слої
                    {
                        for (int indexNeuronInLayer = 0;
                             indexNeuronInLayer < layersNeuron[indexLayer].length;
                             indexNeuronInLayer++)//нейрони в слоях
                        {
                            float sum = 0;

                            for (int indexNeuron = 0; indexNeuron < layersNeuron[indexLayer + 1].length; indexNeuron++) //помилка для текущого нейрона
                            {
                                sum += layersNeuron[indexLayer + 1][indexNeuron].getError()
                                        * layersNeuron[indexLayer][indexNeuronInLayer]
                                        .getWeightByIndex(indexNeuron);
                            }

                            layersNeuron[indexLayer][indexNeuronInLayer]
                                    .setError(activationFunctionDx
                                            (layersNeuron[indexLayer][indexNeuronInLayer].getInput())
                                            * sum);

                            for (int indexNeuron = 0; indexNeuron < layersNeuron[indexLayer + 1]
                                    .length; indexNeuron++) //обчислюєм дельту ваг для текущого нейрона
                            {
                                float deltaWeight = layersNeuron[indexLayer + 1][indexNeuron]
                                        .getError() * layersNeuron[indexLayer][indexNeuronInLayer].getResult()
                                        * genome.getLearningRate() + genome.getMoment() *
                                        layersNeuron[indexLayer][indexNeuronInLayer]
                                                .getDeltaWeightByIndex(indexNeuron);

                                layersNeuron[indexLayer][indexNeuronInLayer]
                                        .setDeltaWeightByIndex(indexNeuron, deltaWeight);
                                layersNeuron[indexLayer][indexNeuronInLayer]
                                        .setWeightByIndex(indexNeuron, deltaWeight);
                            }
                        }
                    }
                }
                if(indexEpoch%1==0)
                {
                    percentErrorInTrainingData = testing(inputTrainingData, outputTrainingData);

                    System.out.println("Error TrD  " + percentErrorInTrainingData);
                }

            }
            percentErrorInTestingData = testing(inputTestData, outputTestData);
            percentErrorInTestingDataArr[indexLesson] = percentErrorInTestingData;
            neuronNetwork.add(layersNeuron.clone());
        }
        setLayersNeuron(neuronNetwork.get(getIndexMinPercentErrorInTestingData()));
        neuronNetwork.clear();
        return getMinPercentErrorInTestingData();
    }

    public void setLayersNeuron(Neuron[][] layersNeuron) {
        this.layersNeuron = layersNeuron;
    }

    private int getIndexMinPercentErrorInTestingData()
    {
        float minError = percentErrorInTestingDataArr[0];
        int indexMinError = 0;
        for (int i = 1; i < percentErrorInTestingDataArr.length; i++)
        {
            if (percentErrorInTestingDataArr[i] < minError)
            {
                minError = percentErrorInTestingDataArr[i];
                indexMinError = i;
            }

        }
        return indexMinError;
    }

    private float getMinPercentErrorInTestingData()
    {
        float minError = percentErrorInTestingDataArr[0];
        for (int i = 1; i < percentErrorInTestingDataArr.length; i++)
        {
            if (percentErrorInTestingDataArr[i] < minError)
            {
                minError = percentErrorInTestingDataArr[i];
            }

        }
        return minError;
    }


    public float testing(float inputTestData[][], float outputTestData[][])
    {
        int countError = 0;
        for (int i = 0; i < inputTestData.length; i++)
        {
            float result[] = predict(inputTestData[i]);
            result = toBoolean(result);
            if (!equalsArr(result, outputTestData[i]))
                countError++;
        }
        return countError / (float) outputTestData.length;
    }

    private boolean equalsArr(float[] firstArr, float[] secondArr)
    {
        if (firstArr.length != secondArr.length)
            return false;
        for (int i = 0; i < firstArr.length; i++)
        {
            if (firstArr[i] != secondArr[i])
                return false;
        }
        return true;
    }

    private float[] toBoolean(float[] arr)
    {
        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i] < 0.5)
                arr[i] = 0;
            else
                arr[i] = 1;
        }
        return arr;
    }

    private void resetNetWeight() {
        for (int i = 0; i < layersNeuron.length - 1; i++)
        {
            for (int j = 0; j < layersNeuron[i].length; j++)
            {
                layersNeuron[i][j].resetWeight();
            }
        }
    }

    private void mixData(float inputData[][], float outputData[][])
    {
        for (int i = 0; i < inputData.length / 2; i++)
        {
            int firstNum = (int) (Math.random() * inputData.length);
            int secondNum = (int) (Math.random() * inputData.length);
            float bufferArr[] = inputData[firstNum];
            inputData[firstNum] = inputData[secondNum];
            inputData[secondNum] = bufferArr;
            bufferArr = outputData[firstNum];
            outputData[firstNum] = outputData[secondNum];
            outputData[secondNum] = bufferArr;
        }
    }

    private float avgNetError(float outputData[][], float result[][]) {
        float sum = 0, error;
        for (int i = 0; i < outputData.length; i++) {
            for (int j = 0; j < outputData[i].length; j++) {
                sum += Math.pow(outputData[i][j] - result[i][j], 2);
            }
        }
        error = (float) Math.sqrt(sum / (outputData.length * outputData[0].length));
        return error;
    }

    private float activationFunction(float x)
    {
        return (float) (1 / (1 + Math.exp(-x)));
    }

    private float activationFunctionDx(float x)
    {
        return (float) ((Math.exp(-x)) / (Math.pow((1 + Math.exp(-x)), 2)));
    }

    public float[] predict(float inputData[])
    {
        for (int i = 0; i < inputData.length; i++)
        {
            layersNeuron[0][i].setInput(inputData[i]);
            layersNeuron[0][i].setResult(activationFunction(inputData[i]));
        }
        for (int i = 1; i < layersNeuron.length; i++)
        {
            for (int j = 0; j < layersNeuron[i].length; j++)
            {
                float x = 0;
                for (int k = 0; k < layersNeuron[i - 1].length; k++)
                {
                    x += layersNeuron[i - 1][k].getWeightByIndex(j) *
                            layersNeuron[i - 1][k].getResult();
                }
                layersNeuron[i][j].setInput(x);
                layersNeuron[i][j].setResult(activationFunction(x));
            }
        }
        float result[] = new float[layersNeuron[layersNeuron.length - 1].length];
        for (int i = 0; i < result.length; i++)
        {
            result[i] = layersNeuron[layersNeuron.length - 1][i].getResult();
        }
        return result;
    }

    public Genome getGenome() {
        return genome;
    }

    public void setGenome(Genome genome) {
        this.genome = genome;
    }
}
