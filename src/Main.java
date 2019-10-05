import java.io.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        /*
        Example
        if you use genetic algorithm comment string 43,44,104,106 in class NeuronNetwork
        GENETIC ALGORITHM IS USED ONLY FOR THE SELECTION OF HYPERPARAMETERS
         */

        // parameters for GA
        final int maxCountLesson = 1;
        final int maxCountGeneration = 20;
        final int countStartPopulation = 8;    // countStartPopulation%countThread=0 !!!
        final int countThread = 4;
        final float probabilityMutation = 0.15f;
        final float percentError = 0.1f;
        String fileName = "Inversion bits"; // save

        int countNeuronInLayers[] = {16,2};//structure NN

        // 10000 number rows,16 number input neuron
        float inputTrainingData[][] = new float[10000][16];
        float outputTrainingData[][] = new float[10000][2];

        float inputTestData[][] = new float[55536][16];
        float outputTestData[][] = new float[55536][2];

        // read NN
        //NeuronNetwork neuronNetwork = (NeuronNetwork) readNet(fileName);

        // data formatting
        data(inputTrainingData,outputTrainingData,inputTestData,outputTestData);
        // parameters learning(hyperparameters)
        Genome genome = new Genome(100, 0.2f, 0.65f);
        NeuronNetwork neuronNetwork = new NeuronNetwork(countNeuronInLayers, genome);
        //learning and return error
        float error = neuronNetwork.learning(inputTrainingData, outputTrainingData, inputTestData, outputTestData, 1);


        /*GeneticAlgorithm geneticAlgorithm=new GeneticAlgorithm(maxCountGeneration,
                countStartPopulation,probabilityMutation,countNeuronInLayers);

        NeuronNetwork neuronNetwork=geneticAlgorithm.learning(countThread,inputTrainingData,outputTrainingData,
                inputTestData,outputTestData,maxCountLesson,percentError);*/

        // testing
        System.out.println("\nError TrD "+neuronNetwork.testing(inputTrainingData,outputTrainingData));
        System.out.println("Error TsD "+neuronNetwork.testing(inputTestData,outputTestData));

        // save NN
         /*if(neuronNetwork.testing(inputTrainingData, outputTrainingData)<=0.01)
        {
            System.out.println("\nSave");
            saveNet(fileName, neuronNetwork);
        }*/

         // 0-7 bits - first number in binary system(a),
        // 8-15 - second number in binary system(b)
        // if a>b (output 10) a<b(01) a=b(11)
        float input[]={1,1,1,0,0,0,0,0,  1,0,1,1,0,0,0,1};
        float result[]=neuronNetwork.predict(input);

        System.out.print("Input ");
        for (int i = 0; i < input.length; i++) {
            System.out.print((int) input[i]+"");
        }
        System.out.print("\nOutput ");
        for (int i = 0; i < result.length; i++) {
            if(result[i]<0.5)
                System.out.print("0");
            else
                System.out.print("1");
        }


    }

    public static void saveNet(String fileName, NeuronNetwork obj) throws IOException {
        try (ObjectOutputStream fout = new ObjectOutputStream(new FileOutputStream(fileName))) {
            fout.writeObject(obj);
        }
    }

    public static Object readNet(String fileName) throws IOException, ClassNotFoundException {
        try (ObjectInputStream fin = new ObjectInputStream(new FileInputStream(fileName))) {
            return fin.readObject();
        }
    }

    public static void  data(float inputTrainingData[][], float outputTrainingData[][],
                              float inputTestData[][], float outputTestData[][])
    {
        ArrayList<Long> inputTrainingDataList = new ArrayList<>(inputTrainingData.length);
        ArrayList<Long> inputTestDataList = new ArrayList<>(65536 - inputTrainingData.length);

        for (int i = 0; i < inputTrainingData.length; i++) {
            long randomNum = (long) (Math.random() * 65536);
            if (!inputTrainingDataList.contains(randomNum)) {
                inputTrainingDataList.add(randomNum);
            } else {
                i--;
            }
        }

        for (long i = 0; i < 65536; i++) {
            if (!inputTrainingDataList.contains(i)) {
                inputTestDataList.add(i);
            }
        }

        for (int i = 0; i < inputTrainingData.length; i++) {
            String buffer = Long.toBinaryString(inputTrainingDataList.get(i));
            while (buffer.length() != 16) {
                buffer = "0" + buffer;
            }
            for (int j = 0; j < inputTrainingData[i].length; j++) {
                inputTrainingData[i][j] = Float.valueOf(buffer.substring(j, j + 1));
            }
        }

        for (int i = 0; i < inputTestData.length; i++) {
            String buffer = Long.toBinaryString(inputTestDataList.get(i));
            while (buffer.length() != 16) {
                buffer = "0" + buffer;
            }
            for (int j = 0; j < inputTestData[i].length; j++) {
                inputTestData[i][j] = Float.valueOf(buffer.substring(j, j + 1));
            }
        }

        for (int i = 0; i < outputTrainingData.length; i++) {
            long lenght = inputTrainingDataList.get(i);
            String buffer = translateNum(lenght);
            for (int j = 0; j < outputTrainingData[i].length; j++) {
                outputTrainingData[i][j] = Float.valueOf(buffer.substring(j, j + 1));
            }
        }

        for (int i = 0; i < outputTestData.length; i++) {
            long lenght = inputTestDataList.get(i);
            String buffer = translateNum(lenght);
            for (int j = 0; j < outputTestData[i].length; j++) {
                outputTestData[i][j] = Float.valueOf(buffer.substring(j, j + 1));
            }
        }

    }

    public static String translateNum(long inputNum)
    {

        String buffer = Long.toBinaryString(inputNum);
        while (buffer.length() != 16) {
            buffer = "0" + buffer;
        }


        long firstNum=0;
        long secondNum=0;


        for (int i = 0; i < buffer.length()/2; i++)
        {
            if(buffer.charAt((buffer.length()/2)-i-1)=='1')
            {
                firstNum+=Math.pow(2,i);
            }
        }
        for (int i = 0; i < buffer.length()/2; i++)
        {
            if(buffer.charAt((buffer.length())-i-1)=='1')
            {
                secondNum+=Math.pow(2,i);
            }
        }
        //task NN
        //---------
        if (firstNum>secondNum)
            return "10";
        if (firstNum<secondNum)
            return "01";
        else return "11";
        //---------

    }

}