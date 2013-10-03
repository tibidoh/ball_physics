package test.ball.core;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Ball {

    private final float SEGMENT_THICKNESS = 0.1f;
    private final int SEGMENT_COUNT = 20;
    private final float RADIUS = 0.6f;

    LinkedList<Body> bodies = new LinkedList<Body>();

    Set<Joint> joints = new HashSet<Joint>();


    public void applyInnerTension( boolean mega ) {
        for ( Body segment : bodies ) {

            float segmentMass = segment.getMassData().mass;

            float normalImpulse = segmentMass * 0.8f;
            float megaImpulse = segmentMass * 3f;
            segment.applyLinearImpulse( new Vector2( mega ? megaImpulse : normalImpulse, 0 ).rotate( (float) ( segment.getAngle() / Math.PI * 180 ) ), segment.getWorldCenter(), true );

        }
    }

    public Ball( World world, Vector2 position ) {
        float segmentLength = (float) ( 2d * Math.PI * RADIUS / SEGMENT_COUNT );
        BodyDef segmentDef = new BodyDef();
        segmentDef.type = BodyDef.BodyType.DynamicBody;

        PolygonShape segmentShape = new PolygonShape();

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = segmentShape;
        fixtureDef.density = 5f;
        fixtureDef.friction = 1f;
        fixtureDef.restitution = 0.5f;

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
                joints.add( setUpJoint( segment, bodies.getLast(), world, segmentLength, jointDef ) );
            }
            segment.setTransform( segment.getPosition(), angle );

            bodies.add( segment );
        }

        //Degree between first and last segments is 360 + step
        jointDef.lowerAngle = (float) ( jointDef.lowerAngle + 2 * Math.PI );
        jointDef.upperAngle = (float) ( jointDef.upperAngle + 2 * Math.PI );
        joints.add( setUpJoint( bodies.getFirst(), bodies.getLast(), world, segmentLength, jointDef ) );

        segmentShape.dispose();
    }

    private RevoluteJoint setUpJoint( Body body1, Body body2, World world, float segmentLength, RevoluteJointDef jointDef ) {

        jointDef.bodyA = body1;
        jointDef.bodyB = body2;

        Vector2 bodyACenter = jointDef.bodyA.getLocalCenter();
        jointDef.localAnchorA.set( bodyACenter.x, bodyACenter.y - segmentLength / 2 );

        Vector2 bodyBCenter = jointDef.bodyB.getLocalCenter();
        jointDef.localAnchorB.set( bodyBCenter.x, bodyACenter.y + segmentLength / 2 );

        return (RevoluteJoint) world.createJoint( jointDef );
    }

    public void awake() {
        for ( Body body : bodies ) {
            body.setAwake( true );
        }
    }
}
