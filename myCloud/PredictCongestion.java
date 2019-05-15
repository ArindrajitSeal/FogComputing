import static org.nd4j.linalg.ops.transforms.Transforms.abs;
import static org.nd4j.linalg.ops.transforms.Transforms.exp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.deeplearning4j.util.ModelSerializer;

import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.datasets.iterator.IteratorDataSetIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.nio.file.StandardOpenOption.*;
import java.nio.file.*;
import java.io.*;

class PredictCongestion{
	public static String call(String imgName)throws Exception{
	//public static void main(String[]args)throws Exception{
		/* Calling of the other class */

		Process p = Runtime.getRuntime().exec("java CocoDBTest "+imgName);
		//Process p = Runtime.getRuntime().exec("java CocoDBTest "+args[0]);
		int i = p.waitFor();
		System.out.println("i = "+i);
		DataSet test = getDataSet();
		DataInputStream dis = new DataInputStream(new FileInputStream("model.bin"));
    		INDArray model1 = Nd4j.read(dis);
    		dis.close();
		
		String result = testModel(test, model1);
		//System.out.println(result);

		p = Runtime.getRuntime().exec("rm testCongestion.txt");
		p.waitFor();
		return result;
	}
	private static Function<String, DataSet> mapRowToDataSet = (String line) -> {
    //sepalLengthCm,sepalWidthCm,petalLengthCm,petalWidthCm,species
    double[] parsedRows = Arrays.stream(line.split(","))
        .mapToDouble(v -> {
          switch (v) {
          case "0":
            return 0.0;
          case "1":
            return 1.0;
          case "2":
            return 2.0;
          default:
            return Double.parseDouble(v);
          }
        }).toArray();
    int columns = parsedRows.length;
    return new DataSet(
        Nd4j.create(Arrays.copyOfRange(parsedRows, 0, columns - 1)),
        Nd4j.create(Arrays.copyOfRange(parsedRows, columns - 1, columns)));
  };
	public static DataSet getDataSet(){
		DataSet test = null;


		try {

      			//File irisData = new ClassPathResource("iris.txt").getFile();
			File f = new ClassPathResource("testCongestion.txt").getFile();
			BufferedReader reader = new BufferedReader(new FileReader(f));
      			List<DataSet> data = reader.lines()
          					.filter(l -> !l.trim().isEmpty())
						.map(mapRowToDataSet)
						.collect(Collectors.toList());

			if (reader != null)
        			reader.close();
      			DataSetIterator iter = new IteratorDataSetIterator(data.iterator(), 150);
      			test = iter.next();

    		} catch (IOException e) {
      			//log.error("IO error", e);
    		}
    		return test;
  	}
	public static String testModel(DataSet testDataSet, INDArray params) {
		//log.info("Testing the model...");
		String result = new String();
		INDArray testFeatures = prependConstant(testDataSet);
		INDArray testLabels = testDataSet.getLabels();
		INDArray predictedLabels = predictLabels(testFeatures, params);
		double[]k = predictedLabels.data().asDouble();
		System.out.println(predictedLabels);
    		if((int)k[0] == 0){
			//System.out.println("Low_Traffic");
			result = "Low_Traffic";
		}
		else if((int)k[0] == 1){
			//System.out.println("Medium_Traffic");
			result = "Medium_Traffic";
		}
		else{
			//System.out.println("High_Traffic");
			result = "High_Traffic";
		}
		return result;
  	}
	public static INDArray prependConstant(DataSet dataset) {
    		INDArray features = Nd4j.hstack(
        				Nd4j.ones(dataset.getFeatures().size(0), 1),
        				dataset.getFeatures());
    		return features;
	
  	}
	public static INDArray predictLabels(INDArray features, INDArray params) {
    		INDArray predictions = features.mmul(params).argMax(1);
    		return predictions;
  	}
}
