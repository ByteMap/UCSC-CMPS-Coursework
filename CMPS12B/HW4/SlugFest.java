import java.util.*;
import java.io.*;

public class SlugFest {
	
	public static void main(String[] args) throws IOException{
		int time, timeStarted1, timeFinished1, timeStarted2, timeFinished2, timeStarted3, timeFinished3, timeStarted4, timeFinished4, timeStarted5, timeFinished5;
		Queue queue1 = new Queue();
		Queue queue2 = new Queue();
		Queue queue3 = new Queue();
		Queue queue4 = new Queue();
		Queue queue5 = new Queue();
		Customer c = null;
		Scanner input = new Scanner(new File(args[0]));
		PrintWriter out = new PrintWriter(new FileWriter(args[1]));
		
		while(input.hasNextLine()) {
			String list = input.nextLine();
			String[] split = list.split("\\s+");
			int lineNumber = Integer.parseInt(split[0]);
			String name = split[1];
			int timeStart = Integer.parseInt(split[2]);
			int timeStop = Integer.parseInt(split[3]);
			if(lineNumber == 1) {
				c = new Customer(lineNumber, name, timeStart, timeStop);
				queue1.enqueue(c);
			}
			if(lineNumber == 2) {
				c = new Customer(lineNumber, name, timeStart, timeStop);
				queue2.enqueue(c);
			}
			if(lineNumber == 3) {
				c = new Customer(lineNumber, name, timeStart, timeStop);
				queue3.enqueue(c);
			}
			if(lineNumber == 4) {
				c = new Customer(lineNumber, name, timeStart, timeStop);
				queue4.enqueue(c);
			}
			if(lineNumber == 5) {
				c = new Customer(lineNumber, name, timeStart, timeStop);
				queue5.enqueue(c);
			}
        }
		
		timeStarted1 = queue1.peek().timeStart;
		timeFinished1 = timeStarted1;
		timeStarted2 = queue2.peek().timeStart;
		timeFinished2 = timeStarted2;
		timeStarted3 = queue3.peek().timeStart;
		timeFinished3 = timeStarted3;
		timeStarted4 = queue4.peek().timeStart;
		timeFinished4 = timeStarted4;
		timeStarted5 = queue5.peek().timeStart;
		timeFinished5 = timeStarted5;
		
		for(time = 0; time < 1000; time++) {	
			
			if(!queue1.isEmpty() && time == timeStarted1) {
				//timeStarted = timeStarted + timeFinished;
				out.println(queue1.peek().lineNumber + " " + queue1.peek().name + " begins " + time);
				timeFinished1 = timeFinished1 + queue1.peek().timeStop;
			}
			if(!queue1.isEmpty() && time == timeFinished1) {
				out.println(queue1.peek().lineNumber + " " + queue1.peek().name + " ends " + time);
				timeStarted1 = time;
				queue1.dequeue();
				if(!queue1.isEmpty() && queue1.peek().timeStart > timeStarted1) {
					timeStarted1 = queue1.peek().timeStart;
					timeFinished1 = timeStarted1;
				}
				if(!queue1.isEmpty() && time == timeStarted1) {
					out.println(queue1.peek().lineNumber + " " + queue1.peek().name + " begins " + time);
					timeFinished1 = timeFinished1 + queue1.peek().timeStop;			
				}
			}
			
			if(!queue2.isEmpty() && time == timeStarted2) {
				out.println(queue2.peek().lineNumber + " " + queue2.peek().name + " begins " + time);
				timeFinished2 = timeFinished2 + queue2.peek().timeStop;
			}
			if(!queue2.isEmpty() && time == timeFinished2) {
				out.println(queue2.peek().lineNumber + " " + queue2.peek().name + " ends " + time);
				timeStarted2 = time;
				queue2.dequeue();
				if(!queue2.isEmpty() && queue2.peek().timeStart > timeStarted2) {
					timeStarted2 = queue2.peek().timeStart;
					timeFinished2 = timeStarted2;
				}
				if(!queue2.isEmpty() && time == timeStarted2) {
					out.println(queue2.peek().lineNumber + " " + queue2.peek().name + " begins " + time);
					timeFinished2 = timeFinished2 + queue2.peek().timeStop;
				}
			}
			
			if(!queue3.isEmpty() && time == timeStarted3) {
				out.println(queue3.peek().lineNumber + " " + queue3.peek().name + " begins " + time);
				timeFinished3 = timeFinished3 + queue3.peek().timeStop;
			}
			if(!queue3.isEmpty() && time == timeFinished3) {
				out.println(queue3.peek().lineNumber + " " + queue3.peek().name + " ends " + time);
				timeStarted3 = time;
				queue3.dequeue();
				if(!queue3.isEmpty() && queue3.peek().timeStart > timeStarted3) {
					timeStarted3 = queue3.peek().timeStart;
					timeFinished3 = timeStarted3;
				}
				if(!queue3.isEmpty() && time == timeStarted3) {
					out.println(queue3.peek().lineNumber + " " + queue3.peek().name + " begins " + time);
					timeFinished3 = timeFinished3 + queue3.peek().timeStop;
				}
			}
			
			if(!queue4.isEmpty() && time == timeStarted4) {
				out.println(queue4.peek().lineNumber + " " + queue4.peek().name + " begins " + time);
				timeFinished4 = timeFinished4 + queue4.peek().timeStop;
			}
			if(!queue4.isEmpty() && time == timeFinished4) {
				out.println(queue4.peek().lineNumber + " " + queue4.peek().name + " ends " + time);
				timeStarted4 = time;
				queue4.dequeue();
				if(!queue4.isEmpty() && queue4.peek().timeStart > timeStarted4) {
					timeStarted4 = queue4.peek().timeStart;
					timeFinished4 = timeStarted4;
				}
				if(!queue4.isEmpty() && time == timeStarted4) {
					out.println(queue4.peek().lineNumber + " " + queue4.peek().name + " begins " + time);
					timeFinished4 = timeFinished4 + queue4.peek().timeStop;
				}
			}
			
			if(!queue5.isEmpty() && time == timeStarted5) {
				out.println(queue5.peek().lineNumber + " " + queue5.peek().name + " begins " + time);
				timeFinished5 = timeFinished5 + queue5.peek().timeStop;
				}
			if(!queue5.isEmpty() && time == timeFinished5) {
				out.println(queue5.peek().lineNumber + " " + queue5.peek().name + " ends " + time);
				timeStarted5 = time;
				queue5.dequeue();
				if(!queue5.isEmpty() && queue5.peek().timeStart > timeStarted5) {
					timeStarted5 = queue5.peek().timeStart;
					timeFinished5 = timeStarted5;
				}
				if(!queue5.isEmpty() && time == timeStarted5) {
					out.println(queue5.peek().lineNumber + " " + queue5.peek().name + " begins " + time);
					timeFinished5 = timeFinished5 + queue5.peek().timeStop;
				}
			}
		}
		input.close();
		out.close();
	}
}

