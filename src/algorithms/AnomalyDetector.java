package algorithms;

import javafx.scene.layout.AnchorPane;
import viewModel.TimeSeries;

import java.util.List;


public interface AnomalyDetector
{
	void learnNormal(TimeSeries ts);
	public List<AnomalyReport> detect(TimeSeries ts);
	public AnchorPane paint();
}
