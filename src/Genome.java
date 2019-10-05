import java.io.Serializable;

public class Genome implements Serializable {
    private int countEpoch;
    private float learningRate;
    private float moment;

    public Genome() {
    }

    public Genome(int countEpoch, float learningRate, float moment) {
        this.countEpoch = countEpoch;
        this.learningRate = learningRate;
        this.moment = moment;
    }

    public int getCountEpoch() {
        return countEpoch;
    }

    public void setCountEpoch(int countEpoch) {
        this.countEpoch = countEpoch;
    }

    public float getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(float learningRate) {
        this.learningRate = learningRate;
    }

    public float getMoment() {
        return moment;
    }

    public void setMoment(float moment) {
        this.moment = moment;
    }

}
