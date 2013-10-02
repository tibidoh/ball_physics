package test.ball.core;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Ball {

    private final float SEGMENT_THICKNESS = 0.4f;

    LinkedList<Body> bodies = new LinkedList<Body>();
    Set<DistanceJoint> joints = new HashSet<DistanceJoint>();

    public Ball( World world, int segmentCount, float radius, Vector2 position ) {
        float segmentLength = (float) ( 2d * Math.PI * radius / segmentCount / 1.2 ); //Magic 1.2
        BodyDef segmentDef = new BodyDef();
        segmentDef.type = BodyDef.BodyType.DynamicBody;

        PolygonShape segmentShape = new PolygonShape();

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = segmentShape;
        fixtureDef.density = 0.01f;
        fixtureDef.friction = 1f;
        fixtureDef.restitution = 0f;

        DistanceJointDef jointDef = new DistanceJointDef();
        jointDef.collideConnected = true;
        jointDef.dampingRatio = 1f;
        jointDef.frequencyHz = 60;
        jointDef.length = 0.015f;


        double angleStep = 2d * Math.PI / segmentCount;
        float angle = 0;
        for ( int segmentNum = 0; segmentNum < segmentCount; segmentNum++ ) {
            angle += angleStep;

            segmentDef.position.set(
                    (float) ( position.x + Math.cos( angle ) * radius ),
                    (float) ( position.y + Math.sin( angle ) * radius ) );
            Body segment = world.createBody( segmentDef );
            segmentShape.setAsBox( SEGMENT_THICKNESS / 2, segmentLength / 2, Vector2.Zero, 0 );
            segment.createFixture( fixtureDef );

            //Not making joint for first segment
            if ( segmentNum != 0 ) {
                joints.add( setUpJoint( segment, bodies.getLast(), world, segmentLength, jointDef ) );
            }
            if ( segmentNum == segmentCount - 1 ) {
                joints.add( setUpJoint( bodies.getFirst(), segment, world, segmentLength, jointDef ) );
            }

            segment.setTransform( segment.getPosition(), angle );
            bodies.add( segment );
        }

        segmentShape.dispose();

    }

    private DistanceJoint setUpJoint( Body body1, Body body2, World world, float segmentLength, DistanceJointDef jointDef ) {
        jointDef.bodyA = body1;
        jointDef.bodyB = body2;

        Vector2 bodyACenter = jointDef.bodyA.getLocalCenter();
        jointDef.localAnchorA.set( bodyACenter.x, bodyACenter.y - segmentLength / 2 );

        Vector2 bodyBCenter = jointDef.bodyB.getLocalCenter();
        jointDef.localAnchorB.set( bodyBCenter.x, bodyACenter.y + segmentLength / 2 );

        return (DistanceJoint) world.createJoint( jointDef );
    }

    public void awake() {
        for ( Body body : bodies ) {
            body.setAwake( true );
        }
    }
}
