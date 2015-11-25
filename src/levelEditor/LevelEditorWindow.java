package levelEditor;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.lwjgl.util.vector.Vector3f;

import game.EditorPlayer.SelectionitemListener;
import game.EntityMembers;
import toolbox.Pair;

import javax.swing.JRadioButtonMenuItem;
import javax.swing.JButton;


/**
 * Remember swing isn't thread safe so this is quite a haphazard way to edit but as time dictates this needs
 * to happen and certainly don't have time to build an opengl editor....
 * @author chris
 *
 */
public class LevelEditorWindow {

	
	private JFrame frame;
	private JTextField zPosInput;
	private JTextField rotXField;
	private JTextField rotYField;
	private JTextField rotZField;
	private JTextField scaleField;
	private JTextField xOffsetField;
	private JTextField yOffsetField;
	private JTextField zOffsetField;
	private JTextField saveFolderName;
	
	JComboBox<String> entitySelection = new JComboBox<String>();
//	JComboBox<EntityMembers> entitySelection = new JComboBox<EntityMembers>();
	
	JRadioButtonMenuItem offsetFromTerrainMenuItem = new JRadioButtonMenuItem("Terrain Offset?");
	JRadioButtonMenuItem offsetFromWaterMenuItem = new JRadioButtonMenuItem("Water Offset?");
	
	private JButton btnSave = new JButton("Save");
	
	private float zValue = 0.0f;
	private float rotX = 0.0f;
	private float rotY = 0.0f;
	private float rotZ = 0.0f;
	private float scale = 1.0f;
	
	private boolean aboveWater = false;
	private boolean aboveTerrain = false;
	
	private String entityMember;

	private List<Pair<EntityMembers, String>> entityClassAndLocations;

	/**
	 * @param classesAndLocations 
	 * @param classesAndLocations 
	 * @wbp.parser.entryPoint
	 */
	public LevelEditorWindow(SelectionitemListener entitySelectionListener,List<Pair<EntityMembers, String>> classesAndLocations){
		this.entityClassAndLocations= classesAndLocations;
		
		entitySelectionListener.setBox(entitySelection);
		this.initializeComponents(entitySelectionListener);
	}
	
	public void initializeComponents(SelectionitemListener entitySelectionListener){
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		
		entitySelection.setBounds(42, 44, 127, 24);
		frame.getContentPane().add(entitySelection);
//		for(int i = 0; i < EntityMembers.values().length; i++){
//			entitySelection.addItem(EntityMembers.values()[i]);
//		}
		
		
		for(int i = 0; i < this.entityClassAndLocations.size(); i++){
			System.out.println(this.entityClassAndLocations.get(i).getR());
			entitySelection.addItem(this.entityClassAndLocations.get(i).getR());
		}
		entitySelection.addActionListener(entitySelectionListener);
		
		
		JLabel xPosLabel = new JLabel("X Position");
		xPosLabel.setBounds(49, 93, 70, 15);
		frame.getContentPane().add(xPosLabel);
		
		JLabel yPosLabel = new JLabel("Y Pos");
		yPosLabel.setBounds(161, 93, 70, 15);
		frame.getContentPane().add(yPosLabel);
		
		zPosInput = new JTextField();
		zPosInput.setText("0.0");
		zPosInput.setBounds(271, 89, 114, 19);
		frame.getContentPane().add(zPosInput);
		zPosInput.setColumns(10);
		
		
		offsetFromTerrainMenuItem.setSelected(true);
		offsetFromTerrainMenuItem.setBounds(271, 151, 141, 19);
		frame.getContentPane().add(offsetFromTerrainMenuItem);
		
		
		offsetFromWaterMenuItem.setBounds(271, 120, 141, 19);
		frame.getContentPane().add(offsetFromWaterMenuItem);
		
		JLabel rotYLabel = new JLabel("Rot Y");
		rotYLabel.setBounds(42, 238, 70, 15);
		frame.getContentPane().add(rotYLabel);
		
		JLabel rotXLabel = new JLabel("Rot X");
		rotXLabel.setBounds(42, 211, 70, 15);
		frame.getContentPane().add(rotXLabel);
		
		JLabel rotZLabel = new JLabel("Rot Z");
		rotZLabel.setBounds(42, 263, 70, 15);
		frame.getContentPane().add(rotZLabel);
		
		rotXField = new JTextField();
		rotXField.setText("0.0");
		rotXField.setBounds(117, 207, 114, 19);
		frame.getContentPane().add(rotXField);
		rotXField.setColumns(10);
		
		rotYField = new JTextField();
		rotYField.setText("0.0");
		rotYField.setColumns(10);
		rotYField.setBounds(117, 236, 114, 19);
		frame.getContentPane().add(rotYField);
		
		rotZField = new JTextField();
		rotZField.setText("0.0");
		rotZField.setColumns(10);
		rotZField.setBounds(117, 261, 114, 19);
		frame.getContentPane().add(rotZField);
		
		JLabel scaleLabel = new JLabel("Scale");
		scaleLabel.setBounds(29, 307, 70, 15);
		frame.getContentPane().add(scaleLabel);
		
		scaleField = new JTextField();
		scaleField.setText("1.0");
		scaleField.setBounds(127, 305, 114, 19);
		frame.getContentPane().add(scaleField);
		scaleField.setColumns(10);
		
		JLabel offsetLabel = new JLabel("Offset - x,y,z");
		offsetLabel.setBounds(12, 379, 107, 15);
		frame.getContentPane().add(offsetLabel);
		
		xOffsetField = new JTextField();
		xOffsetField.setText("0.0");
		xOffsetField.setBounds(148, 377, 45, 19);
		frame.getContentPane().add(xOffsetField);
		xOffsetField.setColumns(10);
		
		yOffsetField = new JTextField();
		yOffsetField.setText("0.0");
		yOffsetField.setColumns(10);
		yOffsetField.setBounds(230, 377, 45, 19);
		frame.getContentPane().add(yOffsetField);
		
		zOffsetField = new JTextField();
		zOffsetField.setText("0.0");
		zOffsetField.setColumns(10);
		zOffsetField.setBounds(310, 377, 50, 19);
		frame.getContentPane().add(zOffsetField);
		
		btnSave.setBounds(268, 415, 117, 25);
		frame.getContentPane().add(btnSave);
		
		saveFolderName = new JTextField();
		saveFolderName.setText("randomFolderName");
		saveFolderName.setBounds(59, 418, 114, 19);
		frame.getContentPane().add(saveFolderName);
		saveFolderName.setColumns(20);
	}
	
	
	/**
	 * Reads in fields and converts strings into numerical usable data, this is done
	 * both for convenience and understandably by breaking up work. Additionally
	 * does error handling so program doesn't crash if user inputs bad input.
	 */
	public void readAndSetData(){
//		entityMember = entitySelection.getItemAt(entitySelection.getSelectedIndex());
		
		zValue = 0.0f;
		//read in z value
		try{
			zValue = Float.valueOf(zPosInput.getText());
		}catch(Exception e){
			System.err.println("Could not read z input value - setting to 0!");
			zValue = 0.0f;
		}
		//Z coord, check water, then terrain, if neither then use absolute
		if(offsetFromWaterMenuItem.isSelected()){
			aboveWater = true;
			aboveTerrain = false;
		}else if(offsetFromTerrainMenuItem.isSelected()){
			aboveTerrain = true;
			aboveWater = false;
		}else{
			aboveWater = false;
			aboveTerrain = false;
		}
		
		try{
			rotX = Float.valueOf(rotXField.getText());
			rotY = Float.valueOf(rotYField.getText());
			rotZ = Float.valueOf(rotZField.getText());
			scale = Float.valueOf(scaleField.getText());
		}catch(Exception e){
			System.err.println("Could not read rotations or scale input value - setting values to standard");
			rotX = 0.0f;
			rotY = 0.0f;
			rotZ = 0.0f;
			scale = 1.0f;
		}
		
		
	}
	
	
	
	
	public float getZValue(){
		return this.zValue;
	}
	public float getRotX(){
		return rotX;
	}
	public float getRotY(){
		return rotY;
	}
	public float getRotZ(){
		return rotZ;
	}
	public float getScale(){
		return scale;
	}
	public boolean getIsAboveWater(){
		return aboveWater;
	}
	public boolean getIsAboveTerrain(){
		return aboveTerrain;
	}
	
	public String getEntityMember() {
		return entityMember;
	}
	
	/**
	 * Factory method kind of
	 * @wbp.parser.entryPoint
	 */
	public static LevelEditorWindow intializeEditorWindow(SelectionitemListener entitySelectionListener,
			List<Pair<EntityMembers, String>> classesAndLocations){
		final LevelEditorWindow window = new LevelEditorWindow(entitySelectionListener, classesAndLocations);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return window;
	}
	

}
