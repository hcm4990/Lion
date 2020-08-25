import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class TopBlockClass {

	public static void main(String[] args) throws IOException {
		// Setting up the file reading variables in the next 6 lines
		String studentInfoFile = "./StudentInfo.csv";
		String testRetakeScoresFile = "./TestRetakeScores.csv";
		String testScoresFile = "./TestScores.csv";
        BufferedReader br1, br2, br3 = null;
        String line = "";
        String cvsSplitBy = ",";
        // Initializing some variables in the next 4 lines
        int classSum = 0;
        int classNumStudents = 0;
        HashMap<String, Integer> map=new HashMap<String, Integer>();
        List<String> listOfFemaleCompSciMajors = new ArrayList<String>(); 
		// This try/catch block runs all of the code
        try {
        	// Making BufferedReaders to read the csv files
            br1 = new BufferedReader(new FileReader(studentInfoFile));
            br2 = new BufferedReader(new FileReader(testScoresFile));
            br3 = new BufferedReader(new FileReader(testRetakeScoresFile));
            br1.readLine(); br2.readLine(); br3.readLine();
            // The next 14 lines create a data structure holding the student scores, factoring in if the retake score is higher than the old score. 
            while ((line = br2.readLine()) != null) {
                String[] studentBr2 = line.split(cvsSplitBy);
                map.put(studentBr2[0], Integer.parseInt(studentBr2[1]));
                classNumStudents += 1;
            }
            while ((line = br3.readLine()) != null) {
                String[] studentBr3 = line.split(cvsSplitBy);
                if (Integer.parseInt(studentBr3[1]) > map.get(studentBr3[0])) {
                	map.put(studentBr3[0], Integer.parseInt(studentBr3[1]));
                }
            }
            for (int f : map.values()) {
                classSum += f;
            }
            // The next 6 lines make a list of the female Computer Science majors
            while ((line = br1.readLine()) != null) {
                String[] studentBr1 = line.split(cvsSplitBy);
                if (studentBr1[2].equals("F") && studentBr1[1].equals("computer science")) {
                	listOfFemaleCompSciMajors.add(studentBr1[0]);
                }
            }
        // The next 3 lines catch exceptions if they occur
        } catch (IOException e) {
            e.printStackTrace();
        }
        float classAverage = classSum/classNumStudents;
        System.out.println(classAverage);
        System.out.println(listOfFemaleCompSciMajors);
        // This posts the required data to the server
        try {
			URL url = new URL ("http://54.90.99.192:5000/challenge");
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json; utf-8");
			con.setRequestProperty("Accept", "application/json");
			con.setDoOutput(true);
			String jsonInputString = "{ id:" + "harishm365@gmail.com" + " name:" + "Harish Mundluru" + " average: "
			+ classAverage + "studentIds" + listOfFemaleCompSciMajors + "}";
			try(OutputStream os = con.getOutputStream()) {
			    byte[] input = jsonInputString.getBytes("utf-8");
			    os.write(input, 0, input.length);			
			}
			try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
				StringBuilder response = new StringBuilder();
				String responseLine = null;
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}
				System.out.println(response.toString());
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
