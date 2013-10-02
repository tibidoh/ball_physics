package test.ball.core;

import com.badlogic.gdx.Game;

public class BallGame extends Game {
    @Override
    public void create() {
        setScreen( new MainScreen() );
    }

    @Override
    public void resize( int width, int height ) {
        getScreen().resize( width, height );
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
