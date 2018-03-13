import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ChronoTimer {
	boolean power = false;
	
	Time endTime;
	Race r1 = new Race();
	ArrayList<Racer> queued = new ArrayList<Racer>();
	ArrayList<Racer> running = new ArrayList<Racer>();
	ArrayList<Racer> finished = new ArrayList<Racer>();
	
	int runs = 0;
	
	Channel c1 = new Channel(1);
	Channel c2 = new Channel(2);
	
	public ChronoTimer() {
		
	}
	
	public void setRace(Race r){
		r1 = r;
	}
	
	public void setRace(String in){
		Gson g = new Gson();
		ArrayList<Racer> racer = (g.fromJson(in,new TypeToken<Collection<Racer>>(){}.getType()));
		Race r = new Race(racer);
		setRace(r);
	}
	
	public void printConsole(String s) {
		System.out.println("console: " + s);
	}
	
	public boolean power(){
		power = !power;
		if (power) {
			printConsole("System is on.");
		}
		else {
			printConsole("System is off.");
		}
		return power;
	}
	
	public void connectChannel(int n, String s) {
		if (n == 1) c1.connect(s);
		else if (n == 2) c2.connect(s);
		printConsole("channel " + n + " is now connected to " + s);
	}
	
	public void toggle(int n){
		if (n == 1) c1.toggle();
		else if (n == 2) c2.toggle();
		printConsole("channel " + n + " is ready to record time.");
	}
	
	public void setRunner(int num) {//adds runner to racequeue based off their number
		if(r1.getRacer(num) != null) {
			queued.add(r1.getRacer(num));
		}
		else {
			Racer r = new Racer(num, "Unnamed");
			r1.addRacer(r);
			queued.add(r);
			
		}
		printConsole("Racer " + num + " has been added to queue.");
	}
	
	public boolean clear(int num) {
		Racer temp = r1.getRacer(num);
		if(temp != null) {
			queued.remove(temp);
			return true;
		}
		return false;
	}
	
	
	public void trigger(int c, String s) {
		
		if (c == 1) {
			if (!c1.enable) {
				printConsole("Channel 1 is not active");
			}
			if (!queued.isEmpty()) {
				Racer r = queued.remove(0);
				running.add(r);
				r.setStart(s);
				printConsole("Racer " + r.num + " starts running at time " + s);
			}
			else {
				printConsole("There are no racers queued to start.");
			}
		}
		else if (c == 2) {
			if (!c2.enable) {
				printConsole("Channel 2 is not active");
			}
			if (!running.isEmpty()) {
				Racer r = running.remove(0);
				finished.add(r);
				r.finished(s);
				printConsole("Racer " + r.num + " finishes running at time " + s);
			}
			else {
				printConsole("There are no racers currently running.");
			}
		}
		else {
			printConsole("Only channels 1 & 2 are being used.");
		}
	}
	
	public void cancel(){
		queued.add(0, running.remove(0));
	}

	public void print() {
		++runs;
		System.out.println("-----FINAL RESULTS-----");
		for (Racer r : finished) {
			System.out.println("Racer " + r.num + " " + r.finishTime());
		}
		try (Writer w = new FileWriter("RUN" + runs +".txt")){
   		 System.out.println("exiting");
			 Gson gson = new GsonBuilder().create();
			 gson.toJson(r1,w);
		 } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void dnf() {
		finished.add(running.remove(0));
		
	}

	public void endRun() {
		queued.clear();
		running.clear();
		finished.clear();
		
	}
	
}