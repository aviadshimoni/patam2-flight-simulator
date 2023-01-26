package utils;

public class CorrelatedFeatures {
	public  String feature1, feature2;
	public  float correlation;
	public  Line linearRegression;
	public  float threshold;
	
	public CorrelatedFeatures(String feature1, String feature2, float correlation, Line linearReg, float threshold) {
		this.feature1 = feature1;
		this.feature2 = feature2;
		this.correlation = correlation;
		this.linearRegression = linearReg;
		this.threshold = threshold;
	}
}
