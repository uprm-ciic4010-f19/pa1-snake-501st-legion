package Game.Entities.Dynamic;

import Main.Handler;

/**
 * Created by AlexVR on 7/2/2018.
 */
public class Tail {
	
    public int x,y;
    
    public Tail(int x, int y,Handler handler){
    	
    	//makes sure the xCoords and yCoords are not out of bounds
    	int gridSize = handler.getWorld().GridWidthHeightPixelCount-1;
    	x = x < 0 ? (x % gridSize + gridSize) % gridSize : x;
        y = y < 0 ? (y % gridSize + gridSize) % gridSize : y;
        x = x > gridSize ? x-1 : x;
        y = y > gridSize ? y-1 : y;
    	
        this.x=x;
        this.y=y;
        
        handler.getWorld().playerLocation[x][y]=true;

    }

}
