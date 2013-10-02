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
    private int screenWidth;
    private int screenHeight;

    private double gravityDirection = Math.PI;
    private final double GRAVITY_CHANGE_SPEED = Math.PI / 2;


    static final float BOX_TO_WORLD = 100f;
    static final float WORLD_TO_BOX = 1 / BOX_TO_WORLD;

    private World world = new World( Vector2.Zero, true );

    private OrthographicCamera cam = new OrthographicCamera();
    Matrix4 debugProjectionMatrix;
    Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

    BallInputProcessor inputProcessor = new BallInputProcessor();

    Ball ball;

    @Override
    public void render( float delta ) {

        Boolean rotationDirection = inputProcessor.getGravityRotationDirection();
        if ( rotationDirection != null ) {
            double gravityRotationAngle = delta * GRAVITY_CHANGE_SPEED;
            if ( rotationDirection ) {
                gravityRotationAngle = -gravityRotationAngle;
            }
            this.gravityDirection += gravityRotationAngle;
            ball.awake();
        }

        world.setGravity( new Vector2( (float) Math.sin( gravityDirection ) * GRAVITY, (float) Math.cos( gravityDirection ) * GRAVITY ) );
        world.step( 1 / 60f, 10, 7 );
        Gdx.gl.glClearColor( 0.1f, 0.1f, 0.1f, 1 );
        Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );

        debugRenderer.render( world, debugProjectionMatrix );
    }

    @Override
    public void resize( int width, int height ) {
        screenWidth = width;
        screenHeight = height;

        cam.viewportWidth = width;
        cam.viewportHeight = height;
        cam.position.set( screenWidth / 2f, screenHeight / 2f, 0 );
        cam.update();

        debugProjectionMatrix = new Matrix4( cam.combined );
        debugProjectionMatrix.scale( BOX_TO_WORLD, BOX_TO_WORLD, 1f );

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this.inputProcessor);

        this.ball = new Ball( world, 50, 1f, new Vector2( 2.5f, 2.5f ) );
        createBox();
    }

    private void createBox() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        FixtureDef bulkFixtureDef = new FixtureDef();
        bulkFixtureDef.friction = 0.5f;
        bulkFixtureDef.restitution = 0.5f;
        bulkFixtureDef.density = 1;

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

        leftBulk.createFixture(bulkFixtureDef);
        rightBulk.createFixture( bulkFixtureDef );
        bulkShape.setAsBox( ( 2.49f ), 0.1f, Vector2.Zero, 0f );
        topBulk.createFixture(bulkFixtureDef);
        bottomBulk.createFixture(bulkFixtureDef);

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
