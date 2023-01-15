package algorithms;

import javafx.scene.layout.AnchorPane;
import model.TimeSeries;

import java.util.List;


public interface AnomalyDetector
{
	void learnNormal(TimeSeries ts);
	List<AnomalyReport> detect(TimeSeries ts);
	AnchorPane paint();
}
