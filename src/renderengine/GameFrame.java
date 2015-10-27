package renderengine;



public class GameFrame {

	private static final long MS_PER_UPDATE = 2;
	
	
	public GameFrame(){
		
	}
	
	public void gameloop(){
		double previous = System.currentTimeMillis();
		double lag = 0.0;
		while (true)
		{
		  double current = System.currentTimeMillis();
		  double elapsed = current - previous;
		  previous = current;
		  lag += elapsed;

		  processInput();

		  while (lag >= MS_PER_UPDATE)
		  {
		    update();
		    lag -= MS_PER_UPDATE;
		  }

		  render();
		}
	}


	private void render() {
		// TODO Auto-generated method stub
		
	}


	private void update() {
		// TODO Auto-generated method stub
		
	}


	private void processInput() {
		// TODO Auto-generated method stub
		
	}
	
	
}
