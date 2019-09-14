package Game.GameStates;

import Main.Handler;
import Resources.Images;
import UI.UIImageButton;
import UI.UIManager;

import java.awt.*;

public class DeathState extends State{
	
	private int count = 0;
    private UIManager uiManager;

	public DeathState(Handler handler) {
		super(handler);
		uiManager = new UIManager(handler);
        handler.getMouseManager().setUimanager(uiManager);

        uiManager.addObjects(new UIImageButton(56, 223, 128, 64, Images.BTitle, () -> {
            handler.getMouseManager().setUimanager(null);
            State.setState(handler.getGame().menuState);
        }));
	}

	@Override
	public void tick() {
		handler.getMouseManager().setUimanager(uiManager);
        uiManager.tick();
        count++;
        if( count>=30){
            count=30;
        }
        if(handler.getKeyManager().pbutt && count>=30){
            count=0;
            State.setState(handler.getGame().menuState);
        }
	}

	@Override
	public void render(Graphics g) {
		g.drawImage(Images.Death,0,0,800,600,null);
        uiManager.Render(g);
	}

}
