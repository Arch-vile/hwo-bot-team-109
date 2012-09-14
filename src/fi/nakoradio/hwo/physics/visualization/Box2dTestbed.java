package fi.nakoradio.hwo.physics.visualization;

import javax.swing.JFrame;

import org.jbox2d.testbed.framework.TestbedFrame;
import org.jbox2d.testbed.framework.TestbedModel;
import org.jbox2d.testbed.framework.TestbedPanel;
import org.jbox2d.testbed.framework.TestbedSetting;
import org.jbox2d.testbed.framework.TestbedSetting.SettingType;
import org.jbox2d.testbed.framework.j2d.TestPanelJ2D;

import fi.nakoradio.hwo.physics.PhysicsWorld;

public class Box2dTestbed {

	
	
	public Box2dTestbed(){
		
	}
	
	
	public void startSimulation(Box2dTest test){
		TestbedModel model = new TestbedModel();         // create our model
		model.addCategory("My Super Tests");             // add a category
		model.addTest(test);                // add our test

		
		// add our custom setting "My Range Setting", with a default value of 10, between 0 and 20
		//model.getSettings().addSetting(new TestbedSetting("My Range Setting", SettingType.ENGINE, 10, 0, 20));
		
		TestbedPanel panel = new TestPanelJ2D(model);    // create our testbed panel
		JFrame testbed = new TestbedFrame(model, panel); // put both into our testbed frame
		testbed.setVisible(true);
		testbed.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	
	
}
