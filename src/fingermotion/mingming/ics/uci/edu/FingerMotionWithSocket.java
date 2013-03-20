package fingermotion.mingming.ics.uci.edu;
/******************************************************************************\
* Copyright (C) 2012-2013 Leap Motion, Inc. All rights reserved.               *
* Leap Motion proprietary and confidential. Not for distribution.              *
* Use subject to the terms of the Leap Motion SDK Agreement available at       *
* https://developer.leapmotion.com/sdk_agreement, or another agreement         *
* between Leap Motion and you, your company or other organization.             *
\******************************************************************************/

/**
 * author: mingming fan
 * date: 02/18/2013
 * for research
 * */

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Math;
import java.net.*;

import com.leapmotion.leap.*;

class FingerMotionListener extends Listener {
	float maxx = 0;
	float minx = 0;
	float maxy = 0;
	float miny = 0;
	float maxz = 0;
	float minz = 0;
	ServerSocket socket = null;
	Socket connectionsocket = null;
	PrintWriter out = null;
	final int port = 9011;
    public void onInit(Controller controller) {
        System.out.println("Initialized");
    }

    public void onConnect(Controller controller) {
        System.out.println("Connected");
        
        try {
        	socket = new ServerSocket(port);
			connectionsocket = socket.accept();
			out = new PrintWriter(connectionsocket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void onDisconnect(Controller controller) {
        System.out.println("Disconnected");
        if(out != null)
        	out.close();
        
        try {
			connectionsocket.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

    public void onExit(Controller controller) {
    	System.out.println("maxx:"+maxx+"\nminx:"+minx+"\nmaxy:"+maxy+"\nminy"+miny+"\nmaxz:"+maxz+"\nminz:"+minz);
        System.out.println("Exited");
    }

    Frame lastFrame;
    int click = 0;
    int COUNTER = 0;
    public void onFrame(Controller controller) {
        // Get the most recent frame and report some basic information
        Frame frame = controller.frame();
        
        /*
        if(lastFrame == frame)
        	return;
        
        GestureList gestureList = lastFrame.isValid()? frame.gestures(lastFrame):frame.gestures();
        
        lastFrame = frame;
        
                for(int i = 0; i < gestureList.count(); i++)
        {
        	if(gestureList.get(i).type() == Gesture.Type.TYPE_KEY_TAP)
        	{
        		click = 1;
        		break;
        	}
        }
        */
        click = 0;
        GestureList gestures = frame.gestures();
        for(int i = 0; i < gestures.count();i++)
        {
        	Gesture gesture = gestures.get(i);
        	switch(gesture.type())
        	{
        		case TYPE_KEY_TAP:
        			//System.out.println("Clicking : "+COUNTER);
        			//COUNTER++;
        			click = 1;
        			break;
        		default:
        			break;
        	}
        }
        
        

        /*
        System.out.println("Frame id: " + frame.id()
                         + ", timestamp: " + frame.timestamp()
                         + ", hands: " + frame.hands().count()
                         + ", fingers: " + frame.fingers().count()
                         + ", tools: " + frame.tools().count());
       */

        out.println("Frame id: " + frame.id()
                + ", timestamp: " + frame.timestamp()
                + ", hands: " + frame.hands().count()
                + ", fingers: " + frame.fingers().count()
                + ", tools: " + frame.tools().count());
        out.flush();
      
        if (!frame.hands().empty()) {
            // Get the first hand
            Hand hand = frame.hands().get(0);

            // Check if the hand has any fingers
            FingerList fingers = hand.fingers();
            if (!fingers.empty()) {
         
                // Calculate the hand's average finger tip position
                Vector avgPos = Vector.zero();
                for (Finger finger : fingers) {
                    avgPos = avgPos.plus(finger.tipPosition());
                }
                avgPos = avgPos.divide(fingers.count());
                if(avgPos.getX() > maxx)
                	maxx = avgPos.getX();
                else if(avgPos.getX() < minx)
                	minx = avgPos.getX();
                if(avgPos.getY() > maxy)
                	maxy = avgPos.getY();
                else if(avgPos.getY() < miny)
                	miny = avgPos.getY();
                if(avgPos.getZ() > maxz)
                	maxz = avgPos.getZ();
                else if(avgPos.getZ() < minz)
                	minz = avgPos.getZ();
                out.println( avgPos.getX()+","+avgPos.getY()+","+avgPos.getZ()+","+click);
                out.flush();
               // System.out.println( avgPos.getX()+","+avgPos.getY()+","+avgPos.getZ());
                //System.out.println("Hand has " + fingers.count() + " fingers, average finger tip position: " + avgPos);
            }
            /*
            // Get the hand's sphere radius and palm position
            System.out.println("Hand sphere radius: " + hand.sphereRadius()
                             + " mm, palm position: " + hand.palmPosition());
*/
            
            // Get the hand's normal vector and direction
            Vector normal = hand.palmNormal();
            Vector direction = hand.direction();

            // Calculate the hand's pitch, roll, and yaw angles
          /*
            System.out.println("Hand pitch: " + Math.toDegrees(direction.pitch()) + " degrees, "
                             + "roll: " + Math.toDegrees(normal.roll()) + " degrees, "
                             + "yaw: " + Math.toDegrees(direction.yaw()) + " degrees\n");
         */
         
         /*
            out.println("Hand pitch: " + Math.toDegrees(direction.pitch()) + " degrees, "
                    + "roll: " + Math.toDegrees(normal.roll()) + " degrees, "
                    + "yaw: " + Math.toDegrees(direction.yaw()) + " degrees\n");
            out.flush();
            */
        }
    }
}

class FingerMotionWithSocket {
	
    public static void main(String[] args) {
    	
    	// create server socket 
    	
    	
        // Create a sample listener and controller
    	FingerMotionListener listener = new FingerMotionListener();
        Controller controller = new Controller();

        //enable gesture recognition
        controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
        // Have the sample listener receive events from the controller
        controller.addListener(listener);

        // Keep this process running until Enter is pressed
        System.out.println("Press Enter to quit...");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Remove the sample listener when done
        controller.removeListener(listener);
    }
}
