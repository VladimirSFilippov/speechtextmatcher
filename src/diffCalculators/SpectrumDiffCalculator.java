package diffCalculators;


public class SpectrumDiffCalculator implements ISpectrumDiffCalculator
{
	private double[] weights = null;
	
	public SpectrumDiffCalculator()
	{
	}
	public SpectrumDiffCalculator(double[] weights)
	{
		this.weights = weights;
		for (int i = 0; i < weights.length; ++i) 
			this.weights[i] = Math.pow(2, weights[i]);
	}
	
	public double diff(double[] first, double[] second)
	{
		return diffNorm2(first, second);
	}
	
	public double diffNorm1(double[] first, double[] second)
	{
		double res = 0;
		for (int i = 0; i < first.length; ++i) {
			double weight = (weights == null) ? 1.0 : weights[i];
			res += Math.abs(first[i] - second[i]) * weight;
		}
		return res;
	}
	
	public double diffNorm2(double[] first, double[] second)
	{
		double res = 0;
		for (int i = 0; i < first.length; ++i) {
			double weight = (weights == null) ? 1.0 : weights[i];
			res += (first[i] - second[i]) * (first[i] - second[i]) * weight;
		}
		return res;
	}
	
	double diffNormInf(double[] first, double[] second)
	{
		double res = 0;
		for (int i = 0; i < first.length; ++i) {
			double weight = (weights == null) ? 1.0 : weights[i];
			res = Math.max(res, (first[i] - second[i]) * weight);
		}
		return res;
	}
}
