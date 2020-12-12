package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) {

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		input myInput;
		Diary myDiary = Diary.getInstance();
		try(Reader reader =  Files.newBufferedReader(Paths.get(args[0]))) {

			myInput = gson.fromJson(reader, input.class);

			Ewoks.getInstance(myInput.getEwoks());//create the ewoks

			//initialize Threads
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

			while (!isFinished.get()){//wait for all threads to be done
				try {
					synchronized (isFinished) {
						isFinished.wait();
					}
				} catch (InterruptedException e){
					e.printStackTrace();
				}
			}


		} catch (IOException e) {
			e.printStackTrace();
		}

		//create the output
		Map<String,Long> output = new HashMap<>();
		output.put("totalAttacks",myDiary.getTotalAttack().longValue());
		output.put("HanSoloFinish",myDiary.getHanSoloFinish());
		output.put("C3POFinish",myDiary.getC3POFinish());
		output.put("R2D2Deactivate",myDiary.getR2D2Deactivate());
		output.put("LeiaTerminate",myDiary.getLeiaTerminate());
		output.put("HanSoloTerminate",myDiary.getHanSoloTerminate());
		output.put("C3POTerminate",myDiary.getC3POTerminate());
		output.put("R2D2Terminate",myDiary.getR2D2Terminate());
		output.put("LandoTerminate",myDiary.getLeiaTerminate());


		//writes the output
		try(Writer writer = Files.newBufferedWriter(Paths.get(args[1]))){
			gson.toJson(output, writer);
		}catch (IOException e){
			e.printStackTrace();
		}

	}

}
