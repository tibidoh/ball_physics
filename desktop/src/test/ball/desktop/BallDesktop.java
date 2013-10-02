package test.ball.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import test.ball.core.BallGame;

public class BallDesktop {
    public static void main( String[] args ) {
        new LwjglApplication( new BallGame(), "BallGame", 500, 500, true );
    }
}
