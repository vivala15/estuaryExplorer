package game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import assetload.AssetLoader;
import entities.Camera;
import entities.Player;
import levelEditor.LevelEditorWindow;
import levelEditor.PreviewEntityHolder;
import renderengine.DisplayManager;
import renderengine.Loader;
import terrains.Terrain;
import toolbox.MousePicker;
import toolbox.Pair;
import water.WaterTile;

/**
 * Designed to be used to edit a world - hmm will have to open a swing application to read
 * and use different entities and set them
 * @author chris
 *
 */
public class EditorPlayer extends Player{

	JComboBox<String> myBox;
	
	LevelEditorWindow levelEditor;
	PreviewEntityHolder previewEntity = null;
	AssetLoader loader; 
	
	MousePicker picker;
	 List<Pair<EntityMembers, String>> classesAndLocations;
	 SelectionitemListener entitySelectionListener = new SelectionitemListener();
	 
	 Vector3f terrainPoint = new Vector3f(0f,0f,0f);
	 
	volatile boolean readNewObject = false;
	boolean removeEntity = true;
	
	float elapsedTime = 0f;
	
	public EditorPlayer(Camera camera, MousePicker picker,
			List<Terrain> terrains, List<WaterTile> waterTiles, AssetLoader loader){
		this.classesAndLocations = loader.getClassAndLocation();
		levelEditor = LevelEditorWindow.intializeEditorWindow(entitySelectionListener,classesAndLocations);
		this.picker = picker;
		this.loader = loader;
	}
	
	
	
	
	@Override
	public void checkInputs() {
		picker.update();
		levelEditor.readAndSetData();
		
		//If new entity selected from drop down, remove old one from world and inputthe new one
		if(readNewObject){
			readNewObject = false;
			System.out.println("Creating new object");
			if(previewEntity != null && removeEntity){
				previewEntity.deleteEntity();
			}else{
				removeEntity = true;
			}
			EntityMembers instance = null;
			String folderName = myBox.getItemAt(myBox.getSelectedIndex());
			for(int i =0; i < classesAndLocations.size(); i++){
				if(classesAndLocations.get(i).getR() == folderName){
					instance = classesAndLocations.get(i).getL();
					break;
				}
			}
			previewEntity = loader.loadSingleEntityUnit(instance, folderName);
		}
		if(previewEntity != null){
			// Get mouse data
			if (picker.getCurrentTerrainPoint() != null) {
				terrainPoint = picker.getCurrentTerrainPoint();
			}
			//System.out.println(terrainPoint);
			// set new transformation matrix for preview Entity
			terrainPoint.y += levelEditor.getZValue();
			previewEntity.getEntity().setPosition(terrainPoint);

			previewEntity.getEntity().setRotX(levelEditor.getRotX());
			previewEntity.getEntity().setRotY(levelEditor.getRotY());
			previewEntity.getEntity().setRotZ(levelEditor.getRotZ());

			previewEntity.getEntity().setScale(levelEditor.getScale());
		}
		//Place new object, set read new object to true, gives preview entity a new object
		//by setting remove entity to false the previous entity won't be deleted but left in world
		//add a time check to keep double placements from occuring
		elapsedTime += DisplayManager.getFrameTimeSeconds();
		if(Mouse.isButtonDown(0) && elapsedTime > .4){
			removeEntity = false;
			readNewObject = true;
			elapsedTime = 0f;
		}
	}
	
	private void saveGame(){
		//read folder name to be used
		
	}

	@Override
	public void shiftCamera() {
		// TODO Auto-generated method stub
		
	}

	
	public class SelectionitemListener implements ActionListener{
		
		public void setBox(JComboBox<String> selectionBox){
			myBox = selectionBox;
		}
		/**
		 * Used for the combo box to select entities, so when called just use this to up date entity
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Read new object set to True");
			readNewObject = true;
			
			//Can't do this as this is called on another thread and breaks!!!!
//			//delete the current
//			if(previewEntity != null){
//				previewEntity.deleteEntity();
//			}
//			//set the new
//			//Find which matches
//			EntityMembers instance = null;
//			String folderName = myBox.getItemAt(myBox.getSelectedIndex());
//			for(int i =0; i < classesAndLocations.size(); i++){
//				if(classesAndLocations.get(i).getR() == folderName){
//					instance = classesAndLocations.get(i).getL();
//					break;
//				}
//			}
//			previewEntity = loader.loadSingleEntityUnit(instance, myBox.getItemAt(myBox.getSelectedIndex()));
//			System.out.println();
			
		}
	}
}
