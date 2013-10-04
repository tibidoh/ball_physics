package test.ball.core;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Ball {

    private final float SEGMENT_THICKNESS = 0.15f;
    private final int SEGMENT_COUNT = 25;
    private final float RADIUS = 0.6f;

    LinkedList<Body> segments = new LinkedList<Body>();
    Set<Joint> joints = new HashSet<Joint>();


    public void applyInnerTension( boolean mega ) {
        for ( Body segment : segments ) {

            float segmentMass = segment.getMassData().mass;

            float normalImpulse = segmentMass * 1.5f;
            float megaImpulse = segmentMass * 7f;

            segment.applyLinearImpulse(
                    new Vector2( mega ? megaImpulse : normalImpulse, 0 ).rotate( (float) ( segment.getAngle() / Math.PI * 180 ) ),
                    segment.getWorldCenter(), true );
        }
    }

    public Ball( World world, Vector2 position ) {
        float segmentLength = (float) ( 2d * Math.PI * RADIUS / SEGMENT_COUNT );
        BodyDef segmentDef = new BodyDef();
        segmentDef.type = BodyDef.BodyType.DynamicBody;
        segmentDef.allowSleep = false;

        PolygonShape segmentShape = new PolygonShape();

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = segmentShape;
        fixtureDef.density = 5f;
        fixtureDef.friction = 1f;
        fixtureDef.restitution = 0f;

        fixtureDef.filter.categoryBits = 0x0001;
        fixtureDef.filter.maskBits = 0x1110;

        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.enableLimit = true;
        jointDef.lowerAngle = (float) ( Math.PI * -0.25 );
        jointDef.upperAngle = (float) ( Math.PI * 0.05 );

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
                joints.add( setUpJoint( segment, segments.getLast(), world, segmentLength, jointDef ) );
            }
            segment.setTransform( segment.getPosition(), angle );

            segments.add( segment );
        }

        //Degree between first and last segment is 360 degree higher then normal
        jointDef.lowerAngle = (float) ( jointDef.lowerAngle + 2 * Math.PI );
        jointDef.upperAngle = (float) ( jointDef.upperAngle + 2 * Math.PI );
        joints.add( setUpJoint( segments.getFirst(), segments.getLast(), world, segmentLength, jointDef ) );

        segmentShape.dispose();
    }

    private RevoluteJoint setUpJoint( Body body1, Body body2, World world, float segmentLength, RevoluteJointDef jointDef ) {

        jointDef.bodyA = body1;
        jointDef.bodyB = body2;

        Vector2 bodyACenter = jointDef.bodyA.getLocalCenter();
        jointDef.localAnchorA.set( bodyACenter.x + SEGMENT_THICKNESS / 2, bodyACenter.y - segmentLength / 2f );

        Vector2 bodyBCenter = jointDef.bodyB.getLocalCenter();
        jointDef.localAnchorB.set( bodyBCenter.x + SEGMENT_THICKNESS / 2, bodyACenter.y + segmentLength / 2f );

        return (RevoluteJoint) world.createJoint( jointDef );
    }
}
