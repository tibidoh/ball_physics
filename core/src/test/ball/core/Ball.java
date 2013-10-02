package test.ball.core;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJoint;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Ball {

    private final float SEGMENT_THICKNESS = 0.1f;
    private final int SEGMENT_COUNT = 15;
    private final float RADIUS = 0.5f;

    LinkedList<Body> bodies = new LinkedList<Body>();
    Set<Joint> joints = new HashSet<Joint>();


    public void applyForces() {
        for ( Body segment : bodies ) {
            segment.applyForce( new Vector2( 0.006f, 0 ).rotate( (float) ( segment.getAngle() / Math.PI * 180 ) ), segment.getWorldCenter(), true );
        }
    }

    public Ball( World world, Vector2 position ) {
        float segmentLength = (float) ( 2d * Math.PI * RADIUS / SEGMENT_COUNT / 1.2 ); //Magic 1.2
        BodyDef segmentDef = new BodyDef();
        segmentDef.type = BodyDef.BodyType.DynamicBody;

        PolygonShape segmentShape = new PolygonShape();

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = segmentShape;
        fixtureDef.density = 0.01f;
        fixtureDef.friction = 1f;
        fixtureDef.restitution = 0f;

        RopeJointDef jointDef = new RopeJointDef();
        jointDef.collideConnected = false;
        jointDef.maxLength = 0.07f;


        double angleStep = 2d * Math.PI / SEGMENT_COUNT;
        float angle = 0;
        for ( int segmentNum = 0; segmentNum < SEGMENT_COUNT; segmentNum++ ) {
            angle += angleStep;

            segmentDef.position.set(
                    (float) ( position.x + Math.cos( angle ) * RADIUS ),
                    (float) ( position.y + Math.sin( angle ) * RADIUS ) );
            Body segment = world.createBody( segmentDef );
            segmentShape.setAsBox( SEGMENT_THICKNESS / 2, segmentLength / 2, Vector2.Zero, 0 );
            segment.createFixture( fixtureDef );

            //Not making joint for the first segment
            if ( segmentNum != 0 ) {
                joints.add( setUpJoint( segment, bodies.getLast(), world, segmentLength, jointDef ) );
            }
            segment.setTransform( segment.getPosition(), angle );

            bodies.add( segment );
        }

        joints.add( setUpJoint( bodies.getFirst(), bodies.getLast(), world, segmentLength, jointDef ) );

        segmentShape.dispose();

    }

    private RopeJoint setUpJoint( Body body1, Body body2, World world, float segmentLength, RopeJointDef jointDef ) {
        jointDef.bodyA = body1;
        jointDef.bodyB = body2;

        Vector2 bodyACenter = jointDef.bodyA.getLocalCenter();
        jointDef.localAnchorA.set( bodyACenter.x, bodyACenter.y - segmentLength / 2 );

        Vector2 bodyBCenter = jointDef.bodyB.getLocalCenter();
        jointDef.localAnchorB.set( bodyBCenter.x, bodyACenter.y + segmentLength / 2 );

        return (RopeJoint) world.createJoint( jointDef );
    }

    public void awake() {
        for ( Body body : bodies ) {
            body.setAwake( true );
        }
    }
}
