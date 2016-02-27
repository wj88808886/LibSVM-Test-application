import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class Main {

	static ArrayList<Double> accuracies = new ArrayList<Double>();
	static ArrayList<String> lines = new ArrayList<>();
	static double[][] p2accuracies = new double[13][13];

	public static void seperateTrain() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("ncrna_s.train"));
		String line = br.readLine();

		while (line != null) {
			lines.add(line);
			line = br.readLine();
		}

		br.close();
		Collections.shuffle(lines);
		PrintWriter w1 = new PrintWriter("trainset0.txt", "UTF-8");
		PrintWriter w2 = new PrintWriter("trainset1.txt", "UTF-8");
		PrintWriter w3 = new PrintWriter("trainset2.txt", "UTF-8");
		PrintWriter w4 = new PrintWriter("trainset3.txt", "UTF-8");
		PrintWriter w5 = new PrintWriter("trainset4.txt", "UTF-8");

		for (int i = 0; i < 5; i++) {
			PrintWriter writer = new PrintWriter("testset" + i + ".txt", "UTF-8");
			for (int j = 0; j < 200; j++) {
				String temp = lines.get(i * 200 + j);
				if (i == 0) {
					w2.println(temp);
					w3.println(temp);
					w4.println(temp);
					w5.println(temp);
				}
				if (i == 1) {
					w1.println(temp);
					w3.println(temp);
					w4.println(temp);
					w5.println(temp);
				}
				if (i == 2) {
					w1.println(temp);
					w2.println(temp);
					w4.println(temp);
					w5.println(temp);
				}
				if (i == 3) {
					w1.println(temp);
					w2.println(temp);
					w3.println(temp);
					w5.println(temp);
				}
				if (i == 4) {
					w1.println(temp);
					w2.println(temp);
					w3.println(temp);
					w4.println(temp);
				}

				writer.println(temp);
			}
			writer.close();
		}
		w1.close();
		w2.close();
		w3.close();
		w4.close();
		w5.close();
	}

	public static void main(String[] args) throws IOException {

		String[] params = new String[] { "0.0625", "0.125", "0.25", "0.5", "1", "2", "4", "8", "16", "32", "64", " 128",
				"256" };

		for (int i = 0; i < 13; i++) {
			String[] train_args = new String[] { "-t", "0", "-c", params[i], "ncrna_s.train", "model.txt" };
			svm_train.main(train_args);
			String[] predict_args = new String[] { "ncrna_s.test", "model.txt", "output.txt" };
			Predictor predictor = new Predictor();
			predictor.main(predict_args);
			accuracies.add(predictor.accuracy);
		}
		PrintWriter writer = new PrintWriter("p1accuracy.txt", "UTF-8");

		for (int i = 0; i < accuracies.size(); i++) {
			writer.println(accuracies.get(i)+"%");
		}
		writer.close();

		seperateTrain();
		for (int i = 0; i < 13; i++) {
			for (int j = 0; j < 13; j++) {
				System.out.println(i*13+j);
				double aver = 0;
				for (int k = 0; k < 5; k++) {
					String[] train_args = new String[] { "-t", "2", "-c", params[i], "-g", params[j],
							"trainset" + k + ".txt", "model.txt" };
					svm_train.main(train_args);
					String[] predict_args = new String[] { "testset" + k + ".txt", "model.txt", "output.txt" };

					Predictor predictor = new Predictor();
					predictor.main(predict_args);
					aver += predictor.accuracy;
				}
				p2accuracies[i][j] = (aver / 5);
			}
		}

		PrintWriter p2w = new PrintWriter("p2accuracy.csv", "UTF-8");
		double max = 0;
		ArrayList<Integer> maxc = new ArrayList<Integer>();
		ArrayList<Integer> maxa = new ArrayList<Integer>();
		for (int i = 0; i < 13; i++) {
			for (int j = 0; j < 13; j++) {
				if (j == 12) p2w.print(p2accuracies[i][j]+"%");
				else p2w.print(p2accuracies[i][j] + "%,");
				if (max < p2accuracies[i][j]){
					max = p2accuracies[i][j];
				}
			}
			p2w.println();
		}
		
		
		for (int i = 0; i < 13; i++) {
			for (int j = 0; j < 13; j++) {
				if (max == p2accuracies[i][j]){
					maxc.add(i);
					maxa.add(j);
				}
			}
		}
		p2w.close();
		PrintWriter p3w = new PrintWriter("p3accuracy.txt", "UTF-8");
		System.out.println("best C is " + maxc + ", best Gamma is " + maxa);
		p3w.println("best C is " + maxc + ", best Gamma is " + maxa);
		for (int i = 0; i < maxc.size(); i++) {
			String[] train_args = new String[] { "-t", "2", "-c", params[maxc.get(i)], "-g", params[maxa.get(i)],"ncrna_s.train", "model.txt" };
			svm_train.main(train_args);
			String[] predict_args = new String[] { "ncrna_s.test", "model.txt", "output.txt" };
			Predictor predictor = new Predictor();
			predictor.main(predict_args);
			
			p3w.println("C is " + params[maxc.get(i)] + " a is " + params[maxa.get(i)] +" accuracy is "+ predictor.accuracy+"%");
		}
		
		p3w.close();
	}
}
