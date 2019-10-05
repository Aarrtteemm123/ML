public class GeneticAlgorithm {
    private int maxCountGeneration;
    private int countNeuronNetwork;
    private int sizeGroup;
    private int countThread;
    private float probabilityMutation;
    private int[] countNeuronInLayers;
    private NeuronNetwork populationNeuronNetwork[];
    private float errorNeuronNetwork[];

    public GeneticAlgorithm(int maxCountGeneration, int countNeuronNetwork,
                            float probabilityMutation, int[] countNeuronInLayers) {
        this.maxCountGeneration = maxCountGeneration;
        this.countNeuronNetwork = countNeuronNetwork;
        this.probabilityMutation = probabilityMutation;
        this.countNeuronInLayers = countNeuronInLayers;
        this.populationNeuronNetwork = new NeuronNetwork[countNeuronNetwork];
        this.errorNeuronNetwork = new float[countNeuronNetwork];
    }

    public NeuronNetwork learning(final int countThread, float inputTrainingData[][],
                                  float outputTrainingData[][], float inputTestData[][],
                                  float outputTestData[][], final int maxCountLesson,
                                  final float percentError)
    {
        this.countThread = countThread;
        createFirstPopulationNeuronNetwork();
        sizeGroup = countNeuronNetwork / countThread;

        for (int indexGeneration = 0; indexGeneration < maxCountGeneration; indexGeneration++) {
            System.out.println("\nGeneration " + indexGeneration);
            Calculation threadArr[] = createThreadArr(inputTrainingData, outputTrainingData,
                    inputTestData, outputTestData, maxCountLesson);

            startAllThreads(threadArr);

            float minError = getMinErrorNeuronNetwork();
            System.out.println("minError " + minError);
            if (minError == 0 || minError <= percentError)
            {
                return populationNeuronNetwork[getIndexMinErrorNeuronNetwork()];
            }

            float sum = 0, x;
            for (int i = 0; i < errorNeuronNetwork.length; i++)
                sum += 1 / errorNeuronNetwork[i];

            x = 1 / sum;
            float scopeArr[][] = new float[populationNeuronNetwork.length][2];
            scopeArr[0][0] = 0;
            scopeArr[0][1] = x / errorNeuronNetwork[0];
            for (int i = 1; i < scopeArr.length; i++)
            {
                scopeArr[i][0] += scopeArr[i - 1][1];
                scopeArr[i][1] = scopeArr[i][0] + x / errorNeuronNetwork[i];
            }

            NeuronNetwork newGenerationNet[] = new NeuronNetwork[countNeuronNetwork];
            for (int i = 0; i < countNeuronNetwork; i++)
            {
                NeuronNetwork firstNet = null;
                NeuronNetwork secondNet = null;
                for (int k = 0; k < 2; k++) {
                    float randomNum = (float) Math.random();
                    for (int j = 0; j < countNeuronNetwork; j++)
                    {
                        if (scopeArr[j][0] <= randomNum && scopeArr[j][1] > randomNum) // 1 викидаєм
                        {
                            if (k == 0)
                            {
                                firstNet = populationNeuronNetwork[j];
                                j = countNeuronNetwork;
                            }
                            else
                                {
                                secondNet = populationNeuronNetwork[j];
                                if (firstNet.equals(secondNet))
                                {
                                    j = countNeuronNetwork;
                                    k--;
                                }
                            }
                        }
                    }
                }

                Genome newGenome;
                Genome firstGenome = firstNet.getGenome();
                Genome secondGenome = secondNet.getGenome();
                float randomNum = (float) Math.random();
                if (randomNum < 0.5)
                {
                    randomNum = (float) Math.random();
                    if (randomNum < 0.5)
                    {
                        // a1b2c2
                        newGenome = new Genome(firstGenome.getCountEpoch(),
                                secondGenome.getLearningRate(), secondGenome.getMoment());
                    }
                    else
                        {
                        // a2b1c1
                        newGenome = new Genome(secondGenome.getCountEpoch(),
                                firstGenome.getLearningRate(), firstGenome.getMoment());
                    }
                }
                else
                    {
                    randomNum = (float) Math.random();
                    if (randomNum < 0.5) {
                        // a1b1c2
                        newGenome = new Genome(firstGenome.getCountEpoch(),
                                firstGenome.getLearningRate(), secondGenome.getMoment());
                    }
                    else
                        {
                        // a2b2c1
                        newGenome = new Genome(secondGenome.getCountEpoch(),
                                secondGenome.getLearningRate(), firstGenome.getMoment());
                    }
                }
                randomNum = (float) Math.random();
                if (randomNum < probabilityMutation)
                {
                    randomNum = (float) Math.random();

                    if (randomNum <= 0.333)
                        newGenome.setCountEpoch((int) (Math.random() * 390 + 10));//390+10

                    if (randomNum > 0.333 && randomNum <= 0.666)
                        newGenome.setLearningRate((float) Math.random());

                    if (randomNum > 0.666)
                        newGenome.setMoment((float) Math.random());
                }
                newGenerationNet[i] = new NeuronNetwork(countNeuronInLayers, newGenome);

            }
            populationNeuronNetwork = newGenerationNet;
        }

        return populationNeuronNetwork[getIndexMinErrorNeuronNetwork()];
    }


    private void startAllThreads(Calculation threadArr[])
    {
        for (int i = 0; i < threadArr.length; i++)
        {
            threadArr[i].start();
        }
        for (int i = 0; i < threadArr.length; i++) {
            try {
                threadArr[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void createFirstPopulationNeuronNetwork()
    {
        for (int i = 0; i < populationNeuronNetwork.length; i++)
        {
            populationNeuronNetwork[i] = new NeuronNetwork(countNeuronInLayers,
                    createRandomGenome());
            errorNeuronNetwork[i] = 1f;
        }
    }

    private Calculation[] createThreadArr(float inputTrainingData[][],
                                          float outputTrainingData[][], float inputTestData[][],
                                          float outputTestData[][], final int maxCountLesson)
    {
        Calculation threadArr[] = new Calculation[countThread];
        for (int indexThread = 0; indexThread < countThread; indexThread++)
        {
            if (indexThread == 0)
            {
                threadArr[indexThread] = new Calculation(indexThread, (sizeGroup - 1),
                        populationNeuronNetwork, errorNeuronNetwork, inputTrainingData,
                        outputTrainingData, inputTestData, outputTestData, maxCountLesson);
            }
            else
                {
                threadArr[indexThread] = new Calculation((sizeGroup * indexThread),
                        (sizeGroup * (indexThread + 1) - 1), populationNeuronNetwork,
                        errorNeuronNetwork, inputTrainingData, outputTrainingData,
                        inputTestData, outputTestData, maxCountLesson);
            }
        }
        return threadArr;
    }

    private float getMinErrorNeuronNetwork()
    {
        float minError = errorNeuronNetwork[0];
        for (int i = 1; i < errorNeuronNetwork.length; i++)
        {
            if (errorNeuronNetwork[i] < minError)
                minError = errorNeuronNetwork[i];
        }
        return minError;
    }

    private int getIndexMinErrorNeuronNetwork()
    {
        float minError = errorNeuronNetwork[0];
        int indexMinError = 0;
        for (int i = 1; i < errorNeuronNetwork.length; i++)
        {
            if (errorNeuronNetwork[i] < minError)
            {
                minError = errorNeuronNetwork[i];
                indexMinError = i;
            }

        }
        return indexMinError;
    }

    private Genome createRandomGenome()
    {
        int countEpoch = (int) (Math.random() * 390 + 10);//390+10
        float learningRate = (float) Math.random();
        float moment = (float) Math.random();
        Genome genome = new Genome(countEpoch, learningRate, moment);
        return genome;
    }
}
