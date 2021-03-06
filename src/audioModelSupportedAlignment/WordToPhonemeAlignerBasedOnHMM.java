package audioModelSupportedAlignment;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import phonemeAligner.audioBased.HMMPhonemeSearch;
import common.AudioLabel;
import common.GenericListContainer;
import common.algorithms.DataByTimesExtractor;
import common.exceptions.ImplementationError;
import common.statistics.OneDimensionalDataStatistics;
import edu.cmu.sphinx.frontend.FrontEnd;
import edu.cmu.sphinx.frontend.util.AudioFileDataSource;
import edu.cmu.sphinx.linguist.acoustic.AcousticModel;
import edu.cmu.sphinx.linguist.acoustic.UnitManager;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;
import graphemesToPhonemesConverters.IWordToPhonemesConverter;


public class WordToPhonemeAlignerBasedOnHMM {
	HMMPhonemeSearch phonemeSearch;
	FrontEnd frontend;
	AudioFileDataSource dataSource;

	public WordToPhonemeAlignerBasedOnHMM(
			URL acousticModelUrl,
			AudioLabel[] prepared,
			IWordToPhonemesConverter converter) throws PropertyException, IOException
	{
		ConfigurationManager cm = new ConfigurationManager(
				new URL("file:/home/bartek/workspace/speechtextmatcher/src/phonemeAlignmentConfig.xml"));
//				ConfigurationManagerUtils.resourceToURL("resource:/edu/cmu/sphinx/config/aligner.xml"));
		cm.setGlobalProperty("acousticModel", acousticModelUrl.toString());
		cm.setGlobalProperty("filler", acousticModelUrl.toString() + "/noisedict");
		cm.setGlobalProperty("g2p", "");
		cm.setGlobalProperty("dictionary", "");
		
		this.dataSource = (AudioFileDataSource)cm.lookup("audioFileDataSource");
		UnitManager unitManager = (UnitManager)cm.lookup("unitManager");
		AcousticModel acousticModel = (AcousticModel)cm.lookup("wsj");
		acousticModel.allocate();
		this.phonemeSearch = new HMMPhonemeSearch(converter, unitManager, acousticModel);
		this.frontend = (FrontEnd)cm.lookup("frontend");
	}

	public ArrayList<AudioLabel> align(
	        ArrayList<double[]> data, AudioLabel[] words, double totalTime) throws ImplementationError
	{
		ArrayList<AudioLabel> ret = new ArrayList<AudioLabel>();
		
		OneDimensionalDataStatistics powerStatistics = new OneDimensionalDataStatistics(createPowerData(data));

		DataByTimesExtractor<double[]> dataExtractor = new DataByTimesExtractor<double[]>(
                new GenericListContainer<double[]>(data), totalTime, 0);
		
		for (AudioLabel word : words) {
			ArrayList<double[]> wordData = dataExtractor.extract(word.getStart(), word.getEnd());
			ret.addAll(this.phonemeSearch.findPhonemes(word, wordData, powerStatistics.getBackgroundMean()));
		}
		
		return ret;
	}

    private double[] createPowerData(ArrayList<double[]> data)
    {
        double[] ret = new double[data.size()];
        for (int i = 0; i < ret.length; ++i)
            ret[i] = data.get(i)[0];
        return ret;
    }
}
