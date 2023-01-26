package utils;

public class CorrelatedFeatureForAll {
    public String feature1, feature2,nameALG;
    public float correlation;


    public CorrelatedFeatureForAll(String feature1, String feature2, String nameALG,float correlation) {
        this.feature1 = feature1;
        this.feature2 = feature2;
        this.nameALG=nameALG;
        this.correlation = correlation;
    }
}
