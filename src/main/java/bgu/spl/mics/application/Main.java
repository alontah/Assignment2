package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.services.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) {
		JSONParser jsonParser = new JSONParser();
		try (FileReader reader = new FileReader("C:\\Users\\alont\\Desktop\\EWOKS\\input5.json"))
		{
			//Read JSON file
			Object obj = jsonParser.parse(reader);
			JSONObject myObj = (JSONObject) obj;

			JSONArray attackList = (JSONArray) myObj.get("attacks");
			Attack [] attackArr = new Attack[attackList.size()];
			Vector<Attack> attackVec = new Vector<Attack>(attackArr.length);

			for (Object att: attackList) {
				parseAttack( (JSONObject) att, attackVec);
			}

			for (int i = 0; i<attackVec.size();i++){
				attackArr[i] = attackVec.get(i);
				//System.out.println(attackArr[i].getDuration()+", "+ attackArr[i].getSerials());
			}

			Long R2D2Duration = (Long) myObj.get("R2D2");
			Long landoDuration = (Long) myObj.get("Lando");
			Long numOfEwoks = (Long) myObj.get("Ewoks");

			//update input values
			input myInput = input.getInstance();
			myInput.setAttacks(attackArr);
			myInput.setR2D2(R2D2Duration.intValue());
			myInput.setLando(landoDuration.intValue());
			myInput.setEwoks(numOfEwoks.intValue());


			Ewoks.getInstance(numOfEwoks.intValue());


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}


		//initialize Threads
		Diary myDiary = Diary.getInstance();
		input myInput = input.getInstance();
		AtomicBoolean isFinished = myDiary.getIsFinished();
		Thread leia = new Thread(new LeiaMicroservice(myInput.getAttacks()));
		Thread han = new Thread(new HanSoloMicroservice());
		Thread c3po = new Thread(new C3POMicroservice());
		Thread r2d2 = new Thread(new R2D2Microservice(myInput.getR2D2()));
		Thread lando = new Thread(new LandoMicroservice(myInput.getLando()));
		leia.start();
		han.start();
		c3po.start();
		r2d2.start();
		lando.start();


		while (!isFinished.get()){
			try {
				synchronized (isFinished) {
					isFinished.wait();
				}
			} catch (InterruptedException e){
				e.printStackTrace();
			}
		}
		System.out.println(myDiary.getThreadFinishCounter());


		try(FileWriter file = new FileWriter("C:\\Users\\alont\\Desktop\\EWOKS\\output5.json")){
			file.write(createOutput(myDiary).toJSONString());
			file.flush();
		}catch (IOException e){
			e.printStackTrace();
		}



	}

	private static void parseAttack(JSONObject attack, Vector<Attack> attacks){
		Long duration = (Long) attack.get("duration");
		int dur = duration.intValue();
		JSONArray serialList = (JSONArray) attack.get("serials");
		List<Integer> l = new ArrayList<Integer>();
		for (Object serial: serialList) {
			Long i = (Long)serial;
			l.add(i.intValue());
		}
		Attack temp = new Attack(l,dur);
		attacks.add(temp);
	}

	private static JSONObject createOutput(Diary myDiary){
		JSONObject output = new JSONObject();
		output.put("totalAttacks", myDiary.getTotalAttack());
		output.put("HanSoloFinish", myDiary.getHanSoloFinish());
		output.put("C3POFinish", myDiary.getC3POFinish());
		output.put("R2D2Deactivate", myDiary.getR2D2Deactivate());
		output.put("LeiaTerminate", myDiary.getLeiaTerminate());
		output.put("HanSoloTerminate", myDiary.getHanSoloTerminate());
		output.put("C3POTerminate", myDiary.getC3POTerminate());
		output.put("R2D2Terminate", myDiary.getR2D2Terminate());
		output.put("LandoTerminate", myDiary.getLandoTerminate());
		return output;
	}

}
