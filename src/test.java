import java.io.IOException;

public class test {
	public static void main(String[] args) throws IOException {
		
		String[] train_args = new String[] { "-t", "2", "-c","128", "-g", "1","ncrna_s.train", "model.txt" };
		svm_train.main(train_args);
		String[] predict_args = new String[] { "ncrna_s.test", "model.txt", "output.txt" };
		Predictor predictor = new Predictor();
		predictor.main(predict_args);
		System.out.println(predictor.accuracy);
	}
	}
