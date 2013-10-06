package test.ball.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class MainScreen implements Screen {

    public static final float GRAVITY = 9.8f;

    public static final float BOX_WIDTH = 5f;
    public static final float BOX_HEIGHT = 5f;

    private double gravityDirection = Math.PI;
    private final double GRAVITY_CHANGE_SPEED = Math.PI / 2;

    private World world = new World( Vector2.Zero, true );
    private OrthographicCamera cam = new OrthographicCamera();
    private Matrix4 debugProjectionMatrix;
    private Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
    private BallInputProcessor inputProcessor = new BallInputProcessor();
    private Ball ball;


    @Override
    public void render( float delta ) {
        Boolean rotationDirection = inputProcessor.getGravityRotationDirection();
        if ( rotationDirection != null ) {
            double gravityRotationAngle = delta * GRAVITY_CHANGE_SPEED;
            if ( rotationDirection ) {
                gravityRotationAngle = -gravityRotationAngle;
            }
            this.gravityDirection += gravityRotationAngle;
            updateGravity();
        }

        ball.applyInnerTension( inputProcessor.isTensed() );
        world.step( 1 / 60f, 50, 50 );
        Gdx.gl.glClearColor( 0.1f, 0.1f, 0.1f, 1 );
        Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );

        debugRenderer.render( world, debugProjectionMatrix );
    }

    private void updateGravity() {
        Vector2 gravityVector = new Vector2(
                (float) Math.sin( gravityDirection ) * GRAVITY,
                (float) Math.cos( gravityDirection ) * GRAVITY );
        world.setGravity( gravityVector );
    }

    @Override
    public void resize( int width, int height ) {
        float boxToWorldByHeight = height / BOX_HEIGHT;
        float boxToWorldByWidth = width / BOX_WIDTH;
        float boxToWorld = Math.min( boxToWorldByHeight, boxToWorldByWidth );

        cam.viewportWidth = width;
        cam.viewportHeight = height;
        cam.position.set( BOX_WIDTH * boxToWorld / 2, BOX_HEIGHT * boxToWorld / 2, 0 );
        cam.update();

        debugProjectionMatrix = new Matrix4( cam.combined );
        debugProjectionMatrix.scale( boxToWorld, boxToWorld, 1f );
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor( this.inputProcessor );

        this.ball = new Ball( world, new Vector2( 2.5f, 2.5f ) );
        updateGravity();
        createBox();
    }

    private void createBox() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        FixtureDef bulkFixtureDef = new FixtureDef();
        bulkFixtureDef.friction = 0.5f;
        bulkFixtureDef.restitution = 0f;
        bulkFixtureDef.density = 1;
        bulkFixtureDef.filter.maskBits = 0x1111;
        bulkFixtureDef.filter.categoryBits = 0x1111;

        bodyDef.position.set( 0f, 2.5f );
        Body leftBulk = world.createBody( bodyDef );
        bodyDef.position.set( 5f, 2.5f );
        Body rightBulk = world.createBody( bodyDef );
        bodyDef.position.set( 2.5f, 5f );
        Body topBulk = world.createBody( bodyDef );
        bodyDef.position.set( 2.5f, 0f );
        Body bottomBulk = world.createBody( bodyDef );

        PolygonShape bulkShape = new PolygonShape();
        bulkShape.setAsBox( 0.1f, 2.49f, Vector2.Zero, 0f );

        bulkFixtureDef.shape = bulkShape;

        leftBulk.createFixture( bulkFixtureDef );
        rightBulk.createFixture( bulkFixtureDef );
        bulkShape.setAsBox( ( 2.49f ), 0.1f, Vector2.Zero, 0f );
        topBulk.createFixture( bulkFixtureDef );
        bottomBulk.createFixture( bulkFixtureDef );

        bodyDef.position.set( 0.6f, 2.5f );
        Body centerBulk = world.createBody( bodyDef );
        bulkShape.setAsBox( 0.5f, 0.2f, Vector2.Zero, 0f );
        centerBulk.createFixture( bulkFixtureDef );

        bulkShape.dispose();
    }

    @Override
    public void hide() {
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
