package test.ball.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.Map;

public class BallInputProcessor implements InputProcessor {

    static Map<Integer, Boolean> keys = new HashMap<Integer, Boolean>();
    static boolean touched = false;
    static boolean accelerometerAvailable = Gdx.input.isPeripheralAvailable( Input.Peripheral.Accelerometer );
    ;

    static {
        keys.put( Input.Keys.LEFT, false );
        keys.put( Input.Keys.RIGHT, false );
        keys.put( Input.Keys.SPACE, false );
    }

    @Override
    public boolean keyDown( int keycode ) {
        keys.put( keycode, true );
        return true;
    }

    @Override
    public boolean keyUp( int keycode ) {
        keys.put( keycode, false );
        return false;
    }

    @Override
    public boolean keyTyped( char character ) {
        return false;
    }

    @Override
    public boolean touchDown( int screenX, int screenY, int pointer, int button ) {
        touched = true;
        return true;
    }

    @Override
    public boolean touchUp( int screenX, int screenY, int pointer, int button ) {
        touched = false;
        return true;
    }

    @Override
    public boolean touchDragged( int screenX, int screenY, int pointer ) {
        return false;
    }

    @Override
    public boolean mouseMoved( int screenX, int screenY ) {
        return false;
    }

    @Override
    public boolean scrolled( int amount ) {
        return false;
    }

    public boolean isTensed() {
        return touched || keys.get( Input.Keys.SPACE );
    }

    public Vector2 getGravityUnitVector() {
        if ( !accelerometerAvailable ) {
            return null;
        }

        return new Vector2( Gdx.input.getAccelerometerX(), Gdx.input.getAccelerometerY() ).nor();
    }

    public Boolean getGravityRotationDirection() {
        boolean left = keys.get( Input.Keys.LEFT );
        boolean right = keys.get( Input.Keys.RIGHT );
        if ( !left && !right ) {
            return null;
        } else { return right; }
    }

}
